package at.robert.game

import at.robert.game.component.withSpriteComponent
import at.robert.game.component.withTransformComponent
import at.robert.game.system.Box2DDebugRenderSystem
import at.robert.game.system.Box2DSystem
import at.robert.game.system.RotationSystem
import at.robert.game.system.SpriteSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity
import com.badlogic.gdx.graphics.g2d.Sprite as GdxSprite

class AshleyGameScreen : KtxScreen {

    private var camera = OrthographicCamera()
    private fun setCamera(width: Float, aspectRatio: Float) {
        val height = width / aspectRatio
        camera.setToOrtho(true, width, height)
        camera.position.set(0f, 0f, 0f)
        camera.update()

        println("Camera: $width x ${width / aspectRatio}")
    }

    private val engine = PooledEngine(
        entityPoolMaxSize = 1000,
        componentPoolMaxSize = 1000,
    )
    private val batch = SpriteBatch()
    private val testSprite = GdxSprite(Texture("car.png"))
    private val box2dSystem = Box2DSystem()

    init {
        engine.addSystem(SpriteSystem(batch))
        engine.addSystem(RotationSystem())
        engine.addSystem(box2dSystem)
        engine.addSystem(Box2DDebugRenderSystem(camera))

        engine.entity {
            withSpriteComponent(testSprite)
            withTransformComponent(
                width = 1f,
                height = 1f,
                rotationDeg = 0f
            )
        }
    }


    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.projectionMatrix = camera.combined
        batch.begin()
        engine.update(delta)
        batch.end()
    }

    override fun resize(width: Int, height: Int) {
        setCamera(10f, width.toFloat() / height.toFloat())
    }

    override fun dispose() {
        batch.dispose()
        testSprite.texture.dispose()
    }

}
