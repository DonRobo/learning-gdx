package at.robert.game.render.sprite

import at.robert.game.component.TransformComponent
import at.robert.game.render.RenderEngine
import at.robert.game.render.RenderState
import at.robert.game.render.Renderer
import com.badlogic.ashley.core.Entity
import ktx.ashley.get

class AdvancedSpriteRenderer(
    val spriteProvider: SpriteProvider,
    val spriteModifier: SpriteModifier,
) : Renderer {
    override fun render(renderEngine: RenderEngine, entity: Entity) {
        val tr = spriteProvider.getSprite(renderEngine)

        val transform = entity[TransformComponent.mapper]!!
        renderEngine.setState(RenderState.SPRITE)
        renderEngine.spriteBatch.draw(
            tr,
            transform.x - transform.width / 2,
            transform.y - transform.height / 2,
            transform.width / 2,
            transform.height / 2,
            transform.width,
            transform.height,
            if (spriteModifier.flipX) -1f else 1f,
            if (spriteModifier.flipY) -1f else 1f,
            0f,
        )
    }
}
