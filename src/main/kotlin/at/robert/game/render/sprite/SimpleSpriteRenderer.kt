package at.robert.game.render.sprite

import at.robert.game.component.TransformComponent
import at.robert.game.render.RenderEngine
import at.robert.game.render.RenderState
import at.robert.game.render.Renderer
import com.badlogic.ashley.core.Entity
import ktx.ashley.get

class SimpleSpriteRenderer(
    val textureProvider: TextureProvider,
) : Renderer {
    override fun render(renderEngine: RenderEngine, entity: Entity) {
        val tr = textureProvider.getTextureRegion(renderEngine) ?: return

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
            1f,
            1f,
            0f,
        )
    }
}
