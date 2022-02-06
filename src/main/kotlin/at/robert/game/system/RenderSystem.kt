package at.robert.game.system

import at.robert.game.component.DungeonTileSprite
import at.robert.game.component.RenderPlaceholder
import at.robert.game.component.SpriteComponent
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class RenderSystem(
    private val batch: PolygonSpriteBatch,
    private val shapeRenderer: ShapeRenderer,
    private val camera: OrthographicCamera
) : EntitySystem(10) {

    private lateinit var spriteEntities: ImmutableArray<Entity>
    private lateinit var dungeonSpriteEntities: ImmutableArray<Entity>
    private lateinit var placeholderEntities: ImmutableArray<Entity>

    private lateinit var dungeonSprite: Texture
    private lateinit var dungeonSpriteMap: Map<String, TextureRegion>

    override fun addedToEngine(engine: Engine) {
        spriteEntities = engine.getEntitiesFor(allOf(SpriteComponent::class, TransformComponent::class).get())
        dungeonSpriteEntities = engine.getEntitiesFor(allOf(DungeonTileSprite::class, TransformComponent::class).get())
        placeholderEntities = engine.getEntitiesFor(allOf(RenderPlaceholder::class, TransformComponent::class).get())

        dungeonSprite = Texture("dungeontileset.png")
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

    override fun removedFromEngine(engine: Engine) {
        dungeonSprite.dispose()
    }

    override fun update(deltaTime: Float) {
        measureTime {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            placeholderEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                if (transform.isVisible()) {
                    processPlaceholderEntity(transform)
                }
            }
            shapeRenderer.end()
        }.let {
            PerformanceMetrics.placeholderRenderTime = it
        }

        measureTime {
            batch.begin()
            spriteEntities.forEach { entity ->
                val transform = entity[TransformComponent.mapper]!!
                if (transform.isVisible()) {
                    val spriteComponent = entity[SpriteComponent.mapper]!!

                    processSpriteEntity(transform, spriteComponent.textureRegion)
                }
            }
            dungeonSpriteEntities.forEach { entity ->
                val transform = entity[TransformComponent.mapper]!!
                if (transform.isVisible()) {
                    val dungeonSpriteComponent = entity[DungeonTileSprite.mapper]!!

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

                    processSpriteEntity(
                        transform,
                        dungeonSpriteComponent.textureRegion!![min(
                            dungeonSpriteComponent.animationProgress.toInt(),
                            dungeonSpriteComponent.animationFrames - 1
                        )]
                    )
                }
            }
            batch.end()
        }.let {
            PerformanceMetrics.spriteRenderTime = it
        }
    }

    private fun processSpriteEntity(transform: TransformComponent, textureRegion: TextureRegion) {
        batch.draw(
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

    private fun processPlaceholderEntity(transform: TransformComponent) {
        shapeRenderer.setColor(0f, 0f, 0f, 1f)
        shapeRenderer.rect(
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

    private fun TransformComponent.isVisible(): Boolean {
        return this.x in camera.position.x - camera.viewportWidth / 2f - this.width..camera.position.x + camera.viewportWidth / 2f + this.width &&
                this.y in camera.position.y - camera.viewportHeight / 2f - this.height..camera.position.y + camera.viewportHeight / 2f + this.height
    }
}
