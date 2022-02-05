package at.robert.game

import at.robert.game.component.*
import at.robert.game.system.*
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.ashley.entity

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

        println("Camera: $width x ${width / aspectRatio}")
    }

    private val engine = PooledEngine(
        entityPoolMaxSize = 1000,
        componentPoolMaxSize = 1000,
    )
    private val batch = PolygonSpriteBatch()
    private val shapeRenderer = ShapeRenderer(100000)
    private val debugFont = BitmapFont()

    init {
        engine.addSystem(Box2DSystem())
        engine.addSystem(PlayerControlSystem())
        engine.addSystem(RenderSystem(batch, shapeRenderer, camera))
        engine.addSystem(DespawnSystem(30f))
        engine.addSystem(DebugRenderSystem(batch, debugFont))
        engine.addSystem(SimpleMoveSystem())
        engine.addSystem(EnemySystem())
//        engine.addSystem(Box2DDebugRenderSystem(camera))

        engine.entity {
            withPlayerControlled()
            withRenderPlaceholder()
            withTransformComponent(x = 0f, y = 0f, width = 0.25f, height = .5f)
            withRigidBody(BodyDef.BodyType.KinematicBody, 40f, 0.1f, 1f)
        }

        engine.entity {
            withRenderPlaceholder()
            withTransformComponent(x = 3f, y = 0f, width = 0.25f, height = .5f)
            withRigidBody(BodyDef.BodyType.KinematicBody, 40f, 0.1f, 1f)
            withMoveTowardsPlayer(1f)
        }
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
