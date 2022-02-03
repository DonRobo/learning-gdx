package at.robert.game

import at.robert.game.component.withRigidBody
import at.robert.game.component.withSpriteComponent
import at.robert.game.component.withTransformComponent
import at.robert.game.system.Box2DSystem
import at.robert.game.system.RenderSystem
import at.robert.game.system.RotationSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity
import ktx.box2d.body
import ktx.box2d.box
import com.badlogic.gdx.graphics.g2d.Sprite as GdxSprite

class AshleyGameScreen : KtxScreen {

    private var camera = OrthographicCamera().apply {
        setToOrtho(true, 10f, 10f)
        position.set(Vector3.Zero)
    }

    private fun setCamera(width: Float, aspectRatio: Float) {
        val height = width / aspectRatio

        camera.viewportWidth = width
        camera.viewportHeight = height
        camera.update()

        println("Camera: $width x ${width / aspectRatio}")
    }

    private val engine = PooledEngine(
        entityPoolMaxSize = 1000,
        componentPoolMaxSize = 1000,
    )
    private val batch = PolygonSpriteBatch()
    private val testSprite = GdxSprite(Texture("car.png"))
    private val box2dSystem = Box2DSystem()

    init {
        engine.addSystem(RenderSystem(batch))
        engine.addSystem(RotationSystem())
        engine.addSystem(box2dSystem)
//        engine.addSystem(Box2DDebugRenderSystem(camera))

        box2dSystem.world.body {
            type = BodyDef.BodyType.StaticBody
            box(10f, 1f) {}
            position.set(0f, -3f)
        }
        box2dSystem.world.body {
            type = BodyDef.BodyType.StaticBody
            box(1f, 10f) {}
            position.set(-5f, 0f)
        }
        box2dSystem.world.body {
            type = BodyDef.BodyType.StaticBody
            box(1f, 10f) {}
            position.set(5f, 0f)
        }

        for (i in 0 until 200) {
            engine.entity {
                withSpriteComponent(testSprite)
                withTransformComponent(
                    width = .3f,
                    height = .3f,
                    rotationDeg = 0f,
                    y = i * -.20f
                )
                withRigidBody(
                    type = BodyDef.BodyType.DynamicBody,
                    density = 40f,
                    restitution = 0.4f,
                    friction = 0.3f,
                )
            }
        }
    }


    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.projectionMatrix = camera.combined
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        setCamera(10f, width.toFloat() / height.toFloat())
    }

    override fun dispose() {
        batch.dispose()
        testSprite.texture.dispose()
    }

}
