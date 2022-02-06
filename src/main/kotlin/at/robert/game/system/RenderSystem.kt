package at.robert.game.system

import at.robert.game.component.*
import at.robert.game.render.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class RenderSystem(
    batch: PolygonSpriteBatch,
    shapeRenderer: ShapeRenderer,
    private val camera: OrthographicCamera,
    private val assetManager: AssetManager,
) : SortedIteratingSystem(
    allOf(Renderable::class).get(),
    Comparator<Entity> { o1, o2 ->
        val z1 = o1[Renderable.mapper]!!.zIndex
        val z2 = o2[Renderable.mapper]!!.zIndex

        if (z1 > z2) {
            return@Comparator 1
        } else if (z2 > z1) {
            return@Comparator -1
        }

        fun Entity.renderY(): Float {
            val colliding = this[CollidingComponent.mapper]
            val r = colliding?.rect
            val offset = if (colliding != null && r != null) {
                r.y + r.h / 2f
            } else {
                0f
            }
            val transform = this[TransformComponent.mapper] ?: return Float.NEGATIVE_INFINITY

            return transform.y - transform.height / 2f + offset
        }

        val y1 = o1.renderY()
        val y2 = o2.renderY()

        y2.compareTo(y1)
    },
    10
), Disposable {

    private val disposable = mutableListOf<Disposable>()

    private fun <T : Disposable> T.register(): T {
        disposable.add(this)
        return this
    }

    private val renderEngine = RenderEngine(batch, shapeRenderer)
    private val spriteRenderer = SpriteRenderer(renderEngine)
    private val dungeonRenderer = DungeonSpriteRenderer(spriteRenderer).register()
    private val placeholderRenderer = PlaceholderRenderer(renderEngine)

    init {
        batch.register()
        shapeRenderer.register()
    }

    override fun update(deltaTime: Float) {
        measureTime {
            forceSort()
            renderEngine.setCamera(camera)
            renderEngine.measureSwitches {
                super.update(deltaTime)
            }.let { PerformanceMetrics.renderModeSwitches = it }
            renderEngine.setState(RenderState.NONE)
        }.let {
            PerformanceMetrics.renderTime = it
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        if (transform != null && !transform.isVisible()) return

        val placeholder = entity[RenderPlaceholder.mapper]
        val dungeonTileSprite = entity[DungeonTileSprite.mapper]
        val spriteComponent = entity[SpriteComponent.mapper]

        when {
            spriteComponent != null -> spriteRenderer.render(transform!!, spriteComponent.textureRegion)
            dungeonTileSprite != null -> dungeonRenderer.render(entity, deltaTime)
            placeholder != null -> placeholderRenderer.render(transform!!)
            else -> error("Can't render entity $entity")
        }
    }

    private fun TransformComponent.isVisible(): Boolean {
        return this.x in camera.position.x - camera.viewportWidth / 2f - this.width..camera.position.x + camera.viewportWidth / 2f + this.width &&
                this.y in camera.position.y - camera.viewportHeight / 2f - this.height..camera.position.y + camera.viewportHeight / 2f + this.height
    }

    override fun dispose() {
        disposable.forEach(Disposable::dispose)
    }
}
