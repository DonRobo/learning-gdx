package at.robert.game

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.*
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import kotlin.math.min
import com.badlogic.gdx.graphics.g2d.Sprite as GdxSprite

class Rotating(var speed: Float = 0f) : Component {
    companion object : Mapper<Rotating>()
}

class Sprite : Component {
    lateinit var textureRegion: TextureRegion

    companion object : Mapper<Sprite>()
}

class Transform(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 0f,
    var height: Float = 0f,
    var rotation: Float = 0f,
) : Component {
    companion object : Mapper<Transform>()
}

class RigidBody : Component {
    lateinit var body: Body

    companion object : Mapper<RigidBody>()
}

class SpriteSystem(private val batch: SpriteBatch) : EntitySystem() {
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(Sprite::class, Transform::class).get())
    }

    override fun update(deltaTime: Float) {
        entities.forEach {
            val sprite = it[Sprite.mapper]!!
            val transform = it[Transform.mapper]!!
            batch.draw(
                sprite.textureRegion,
                transform.x - transform.width / 2,
                transform.y - transform.height / 2,
                transform.width / 2,
                transform.height / 2,
                transform.width,
                transform.height,
                1f,
                1f,
                transform.rotation
            )
        }
    }
}

class RotationSystem : EntitySystem() {
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(Rotating::class, Transform::class).exclude(RigidBody::class).get())
    }

    override fun update(deltaTime: Float) {
        entities.forEach {
            val transform = it[Transform.mapper]!!

            transform.rotation += it[Rotating.mapper]!!.speed * deltaTime
        }
    }

}

class Box2DSystem(private val world: World) : EntitySystem() {
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(RigidBody::class, Transform::class).get())
    }

    override fun update(deltaTime: Float) {
        world.step(min(1f / 30f, deltaTime), 6, 2)
        entities.forEach {
            val transform = it[Transform.mapper]!!
            val body = it[RigidBody.mapper]!!.body

            transform.x = body.position.x
            transform.y = body.position.y
            transform.rotation = body.angle * MathUtils.radiansToDegrees
        }
    }
}

class AshleyGameScreen : KtxScreen {

    private var camera = OrthographicCamera().apply {
        setToOrtho(true, 800f, 600f)
    }

    private val engine = PooledEngine(
        entityPoolMaxSize = 1000,
        componentPoolMaxSize = 1000,
    )
    private val batch = SpriteBatch()
    private val testSprite = GdxSprite(Texture("car.png"))
    private val world = createWorld(Vector2(0f, 70f), true).also {
        it.body {
            type = StaticBody
            box(800f, 100f) {}
            position.set(400f, 650f)
        }
    }
    private val debugRenderer = Box2DDebugRenderer(
        true,
        true,
        false,
        true,
        false,
        true
    )

    init {
        engine.addSystem(SpriteSystem(batch))
        engine.addSystem(RotationSystem())
        engine.addSystem(Box2DSystem(world))
        engine.addEntityListener(allOf(RigidBody::class, Transform::class).get(), object : EntityListener {
            override fun entityAdded(entity: Entity) {
                val transform = entity[Transform.mapper]!!
                val rigidBody = entity[RigidBody.mapper]!!
                rigidBody.body = world.body {
                    type = DynamicBody
                    box(
                        width = transform.width,
                        height = transform.height,
                    ) {
                        userData = entity
                        density = 40f
                        restitution = 0.5f
                    }
                }
                rigidBody.body.setTransform(transform.x, transform.y, transform.rotation * MathUtils.degreesToRadians)
            }

            override fun entityRemoved(entity: Entity) {
                val body = entity[RigidBody.mapper]!!
                world.destroyBody(body.body)
            }
        })
        for (i in 0 until 10) {
            for (j in 0 until 8) {
                engine.entity {
//                with<Rotating> {
//                    speed = 180f / i
//                }
                    with<RigidBody>()
                    with<Transform> {
                        x = 100f + i * 30f
                        y = 100f + j * 40f
                        width = 30f
                        height = 30f
                        rotation = 33f * i
                    }
                    with<Sprite> {
                        textureRegion = testSprite
                    }
                }
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.projectionMatrix = camera.combined
        batch.begin()
        engine.update(delta)
        batch.end()
        debugRenderer.render(world, camera.combined)
    }

    override fun dispose() {
        batch.dispose()
        testSprite.texture.dispose()
    }

}
