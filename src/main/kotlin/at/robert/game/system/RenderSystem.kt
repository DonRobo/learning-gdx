package at.robert.game.system

import at.robert.game.component.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Disposable
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

enum class RenderState {
    SPRITE, FILLED, LINES, NONE
}

class RenderEngine(
    val spriteBatch: PolygonSpriteBatch,
    val shapeRenderer: ShapeRenderer,
) {
    private var currentState = RenderState.NONE

    private fun end() {
        when (currentState) {
            RenderState.SPRITE -> spriteBatch.end()
            RenderState.FILLED -> shapeRenderer.end()
            RenderState.LINES -> shapeRenderer.end()
            RenderState.NONE -> {}
        }
        currentState = RenderState.NONE
    }

    fun setState(state: RenderState) {
        when (state) {
            currentState -> return
            RenderState.NONE -> end()
            RenderState.SPRITE -> {
                end()
                spriteBatch.begin()
            }
            RenderState.FILLED -> {
                end()
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            }
            RenderState.LINES -> {
                end()
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            }
        }
        currentState = state
        switches++
    }

    fun setCamera(camera: OrthographicCamera) {
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
    }

    private var switches = 0
    fun measureSwitches(block: (RenderEngine) -> Unit): Int {
        switches = 0
        block(this)
        return switches
    }
}

class PlaceholderRenderer(
    private val renderEngine: RenderEngine,
) {
    fun render(transform: TransformComponent) {
        renderEngine.setState(RenderState.FILLED)
        renderEngine.shapeRenderer.setColor(0f, 0f, 0f, 1f)
        renderEngine.shapeRenderer.rect(
            transform.x - transform.width / 2,
            transform.y - transform.height / 2,
            transform.width / 2,
            transform.height / 2,
            transform.width,
            transform.height,
            1f,
            1f,
            0f,
        )
    }

}

class SpriteRenderer(
    private val renderEngine: RenderEngine,
) {
    fun render(transform: TransformComponent, textureRegion: TextureRegion) {
        renderEngine.setState(RenderState.SPRITE)
        renderEngine.spriteBatch.draw(
            textureRegion,
            transform.x - transform.width / 2,
            transform.y - transform.height / 2,
            transform.width / 2,
            transform.height / 2,
            transform.width,
            transform.height,
            1f,
            1f,
            0f,
        )
    }
}

class DungeonSpriteRenderer(
    private val spriteRenderer: SpriteRenderer,
) : Disposable {
    private val dungeonSprite: Texture = Texture("dungeontileset.png")
    private val dungeonSpriteMap: Map<String, TextureRegion>

    init {
        val r = Regex("(\\w+) (\\d+) (\\d+) (\\d+) (\\d+)(?: (\\d+))?")
        val spriteMap = mutableMapOf<String, TextureRegion>()
        Gdx.files.internal("dungeontileset.txt").reader().forEachLine {
            val match = r.matchEntire(it) ?: return@forEachLine

            val (name, x, y, width, height, animationFrames) = match.destructured
            val animationRange = 0 until (animationFrames.toIntOrNull() ?: 1)
            for (i in animationRange) {
                val widthInt = width.toInt()
                spriteMap[name + i] = TextureRegion(
                    dungeonSprite,
                    x.toInt() + widthInt * i,
                    y.toInt(),
                    widthInt,
                    height.toInt()
                )
            }
        }
        this.dungeonSpriteMap = spriteMap
    }

    fun render(entity: Entity, deltaTime: Float) {
        val dungeonSpriteComponent = entity[DungeonTileSprite.mapper]!!

        //TODO move into animation system
        dungeonSpriteComponent.animationProgress += deltaTime * dungeonSpriteComponent.animationSpeed * dungeonSpriteComponent.animationFrames
        if (dungeonSpriteComponent.animationProgress >= dungeonSpriteComponent.animationFrames) {
            dungeonSpriteComponent.animationProgress -= dungeonSpriteComponent.animationFrames
        }

        if (dungeonSpriteComponent.textureRegion == null) {
            dungeonSpriteComponent.textureRegion =
                (0 until dungeonSpriteComponent.animationFrames).map {
                    dungeonSpriteMap[dungeonSpriteComponent.sprite + it]!!
                }.toTypedArray()
        }

        val transform = entity[TransformComponent.mapper]!!
        spriteRenderer.render(
            transform,
            dungeonSpriteComponent.textureRegion!![
                    min(dungeonSpriteComponent.animationProgress.toInt(), dungeonSpriteComponent.animationFrames - 1)
            ]
        )
    }

    override fun dispose() {
        dungeonSprite.dispose()
    }
}

@OptIn(ExperimentalTime::class)
class RenderSystem(
    batch: PolygonSpriteBatch,
    shapeRenderer: ShapeRenderer,
    private val camera: OrthographicCamera
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
