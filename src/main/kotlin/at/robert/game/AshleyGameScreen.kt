package at.robert.game

import at.robert.game.entity.PlayerEntity
import at.robert.game.entity.addEntity
import at.robert.game.system.*
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import ktx.app.KtxScreen
import ktx.app.clearScreen

class AshleyGameScreen : KtxScreen {

    private var camera = OrthographicCamera().apply {
        setToOrtho(false, 10f, 10f)
        position.set(Vector3.Zero)
    }

    private fun setCamera(width: Float, aspectRatio: Float) {
        val height = width / aspectRatio

        camera.viewportWidth = width
        camera.viewportHeight = height
        camera.update()
    }

    private val engine = PooledEngine(
        entityPoolMaxSize = 1000,
        componentPoolMaxSize = 1000,
    )
    private val batch = PolygonSpriteBatch()
    private val shapeRenderer = ShapeRenderer()
    private val debugFont = BitmapFont()

    init {
        engine.addSystem(PhysicsSystem())
        engine.addSystem(PlayerControlSystem())
        engine.addSystem(RenderSystem(batch, shapeRenderer, camera))
        engine.addSystem(DespawnSystem(30f))
        engine.addSystem(DebugRenderSystem(batch, debugFont))
//        engine.addSystem(JBumpDebugRenderSystem(camera, shapeRenderer))

        engine.addEntity(PlayerEntity())

    }


    override fun render(delta: Float) {
        clearScreen(0.8f, 0.8f, 0.8f)
        batch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
        engine.update(delta)
    }

    override fun resize(width: Int, height: Int) {
        setCamera(10f, width.toFloat() / height.toFloat())
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        debugFont.dispose()
        engine.removeAllSystems()
        engine.removeAllEntities()
    }

}
