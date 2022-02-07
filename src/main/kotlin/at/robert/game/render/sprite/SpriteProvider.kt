package at.robert.game.render.sprite

import at.robert.game.render.RenderEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion

interface SpriteProvider {
    fun getSprite(renderEngine: RenderEngine): TextureRegion
}
