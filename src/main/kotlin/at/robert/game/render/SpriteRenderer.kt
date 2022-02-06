package at.robert.game.render

import at.robert.game.component.TransformComponent
import com.badlogic.gdx.graphics.g2d.TextureRegion

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
