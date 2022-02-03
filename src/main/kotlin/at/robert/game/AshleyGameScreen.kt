package at.robert.game

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.*
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
                transform.x,
                transform.y,
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
        entities = engine.getEntitiesFor(allOf(Rotating::class, Transform::class).get())
    }

    override fun update(deltaTime: Float) {
        entities.forEach {
            val transform = it[Transform.mapper]!!

            transform.rotation += it[Rotating.mapper]!!.speed * deltaTime
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

    init {
        engine.addSystem(SpriteSystem(batch))
        engine.addSystem(RotationSystem())
        for (i in 0 until 10) {
            engine.entity {
                with<Rotating> {
                    speed = 180f / i
                }
                with<Transform> {
                    x = 100f + i * 30f
                    y = 100f
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

    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.projectionMatrix = camera.combined
        batch.begin()
        engine.update(delta)
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        testSprite.texture.dispose()
    }

}
