package at.robert.game

import at.robert.game.entity.*
import at.robert.game.system.*
import at.robert.game.util.ResourceManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import ktx.app.KtxScreen
import ktx.app.clearScreen
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class AshleyGameScreen : KtxScreen {

    private val cameraWidth = 50f

    private var camera = OrthographicCamera().apply {
        setToOrtho(false, cameraWidth, cameraWidth)
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
    private val resourceManager = ResourceManager()

    init {
        engine.addSystem(Box2DPhysicsSystem())
        engine.addSystem(PlayerControlSystem())
        engine.addSystem(RenderSystem(batch, shapeRenderer, camera, resourceManager))
        engine.addSystem(DespawnSystem(30f))
        engine.addSystem(DebugRenderSystem(batch, debugFont))
        engine.addSystem(EnemySystem())
//        engine.addSystem(JBumpDebugRenderSystem(camera, shapeRenderer))
        engine.addSystem(Box2DDebugRenderSystem(camera))
        engine.addSystem(TransformDebugRenderSystem(camera, shapeRenderer))

        engine.systems.sortedBy { it.priority }.forEachIndexed { index, entitySystem ->
            println("${index + 1}. ${entitySystem.javaClass.simpleName} (${entitySystem.priority})")
        }

        engine.addEntity(PlayerEntity())

        for (y in -10 until 10) {
            for (x in -10 until 10) {
                engine.addEntity(FloorTile(x, y))
            }
        }

        engine.addEntity(ColumnTile(3, 3))

        for (i in 0 until 100) {
            engine.addEntity(OrcEnemy(-3f, 3f))
        }
        for (y in -5..5) {
            for (x in -5..+5) {
                engine.addEntity(Crate(x * 1.5f, y * 1.5f))
            }
        }
    }


    override fun render(delta: Float) {
        measureTime {
            clearScreen(0f, 0f, 0f, 1f)
            batch.projectionMatrix = camera.combined
            shapeRenderer.projectionMatrix = camera.combined
            engine.update(delta)
        }.let {
            PerformanceMetrics.overallTime = it
        }
    }

    override fun resize(width: Int, height: Int) {
        setCamera(cameraWidth, width.toFloat() / height.toFloat())
    }

    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        debugFont.dispose()
        resourceManager.dispose()
        engine.removeAllSystems()
        engine.removeAllEntities()
    }

}
