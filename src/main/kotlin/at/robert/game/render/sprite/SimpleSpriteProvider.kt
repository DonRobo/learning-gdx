package at.robert.game.render.sprite

import at.robert.game.render.RenderEngine
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class SimpleSpriteProvider(val spriteFile: String) : SpriteProvider {
    private var textureRegion: TextureRegion? = null
    override fun getSprite(renderEngine: RenderEngine): TextureRegion {
        if (textureRegion == null) {
            val t = renderEngine.resourceManager.load(spriteFile) { Texture(it) }
            textureRegion = TextureRegion(t)
        }
        return textureRegion!!
    }
}
