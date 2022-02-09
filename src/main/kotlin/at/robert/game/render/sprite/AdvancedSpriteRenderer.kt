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
        require(spriteModifier.rotatedBy90 in 0..3) { "rotatedBy90 must be in range 0..3 -> is ${spriteModifier.rotatedBy90}" }
        val rotationSwitchHeightWidth = spriteModifier.rotatedBy90 % 2 == 1
        val switchedWidth = if (rotationSwitchHeightWidth) transform.height else transform.width
        val switchedHeight = if (rotationSwitchHeightWidth) transform.width else transform.height
        renderEngine.spriteBatch.draw(
            tr,
            transform.x - switchedWidth / 2,
            transform.y - switchedHeight / 2,
            switchedWidth / 2,
            switchedHeight / 2,
            switchedWidth,
            switchedHeight,
            if (spriteModifier.flipX) -1f else 1f,
            if (spriteModifier.flipY) -1f else 1f,
            spriteModifier.rotatedBy90 * 90f,
        )
    }
}
