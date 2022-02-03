package at.robert.game.system

import at.robert.game.component.SpriteComponent
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get

class RenderSystem(private val batch: PolygonSpriteBatch) : IteratingSystem(
    allOf(SpriteComponent::class, TransformComponent::class).get()
) {

    override fun update(deltaTime: Float) {
        batch.begin()
        super.update(deltaTime)
        batch.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
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
}
