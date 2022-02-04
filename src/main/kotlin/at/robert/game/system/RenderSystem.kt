package at.robert.game.system

import at.robert.game.component.RenderPlaceholder
import at.robert.game.component.SpriteComponent
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class RenderSystem(private val batch: PolygonSpriteBatch, private val shapeRenderer: ShapeRenderer) : EntitySystem(10) {

    private lateinit var spriteEntities: ImmutableArray<Entity>
    private lateinit var placeholderEntities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        spriteEntities = engine.getEntitiesFor(allOf(SpriteComponent::class, TransformComponent::class).get())
        placeholderEntities = engine.getEntitiesFor(allOf(RenderPlaceholder::class, TransformComponent::class).get())
    }

    override fun update(deltaTime: Float) {
        measureTime {
            batch.begin()
            spriteEntities.forEach { processSpriteEntity(it) }
            batch.end()
        }.let {
            PerformanceMetrics.spriteRenderTime = it
        }

        measureTime {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            placeholderEntities.forEach { processPlaceholderEntity(it) }
            shapeRenderer.end()
        }.let {
            PerformanceMetrics.placeholderRenderTime = it
        }
    }

    private fun processSpriteEntity(entity: Entity) {
        val spriteComponent = entity[SpriteComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!
        batch.draw(
            spriteComponent.textureRegion,
            transform.x - transform.width / 2,
            transform.y - transform.height / 2,
            transform.width / 2,
            transform.height / 2,
            transform.width,
            transform.height,
            1f,
            1f,
            transform.rotationDeg
        )
    }

    private fun processPlaceholderEntity(entity: Entity) {
        val transform = entity[TransformComponent.mapper]!!
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
            transform.rotationDeg
        )
    }
}
