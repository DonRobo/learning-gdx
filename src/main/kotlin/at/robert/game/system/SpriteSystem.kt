package at.robert.game.system

import at.robert.game.component.SpriteComponent
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.ashley.allOf
import ktx.ashley.get

class SpriteSystem(private val batch: SpriteBatch) : EntitySystem() {
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(SpriteComponent::class, TransformComponent::class).get())
    }

    override fun update(deltaTime: Float) {
        entities.forEach {
            val spriteComponent = it[SpriteComponent.mapper]!!
            val transform = it[TransformComponent.mapper]!!
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
}
