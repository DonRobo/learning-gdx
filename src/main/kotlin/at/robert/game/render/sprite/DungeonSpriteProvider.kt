package at.robert.game.render.sprite

import at.robert.game.render.RenderEngine
import at.robert.game.util.ResourceManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class DungeonSpriteProvider(
    val spriteName: String,
    val animationSpeed: Float? = null,
) : AnimatedSpriteProvider {

    private class DungeonSprite(
        val name: String,
        val width: Int,
        val height: Int,
        val frames: Int,
        val data: Array<TextureRegion>
    )

    private lateinit var dungeonSprite: DungeonSprite
    override var animationProgress: Float = 0f

    private fun init(resourceManager: ResourceManager) {
        val dungeonSpriteMap = resourceManager.load("dungeontileset") { spritesheetName ->
            val r = Regex("(\\w+) (\\d+) (\\d+) (\\d+) (\\d+)(?: (\\d+))?")
            val spriteMap = mutableMapOf<String, DungeonSprite>()
            val t = Texture("$spritesheetName.png")
            Gdx.files.internal("$spritesheetName.txt").reader().forEachLine {
                val match = r.matchEntire(it) ?: return@forEachLine

                val (name, x, y, width, height, animationFrames) = match.destructured
                val animationRange = 0 until (animationFrames.toIntOrNull() ?: 1)
                val widthI = width.toInt()
                val heightI = height.toInt()

                spriteMap[name] = DungeonSprite(
                    name,
                    widthI,
                    heightI,
                    (animationFrames.toIntOrNull() ?: 1),
                    animationRange.map { frameIndex ->
                        TextureRegion(t, x.toInt() + frameIndex * widthI, y.toInt(), widthI, heightI)
                    }.toTypedArray()
                )
            }
            spriteMap
        }
        dungeonSprite = dungeonSpriteMap[spriteName]
            ?: throw IllegalStateException("DungeonSpriteProvider: Sprite $spriteName not found")
    }

    override fun getSprite(renderEngine: RenderEngine): TextureRegion {
        if (!::dungeonSprite.isInitialized) {
            init(renderEngine.resourceManager)
        }
        if (animationSpeed != null) {
            val dt = Gdx.graphics.deltaTime
            animationProgress += dt * animationSpeed
            while (animationProgress >= 1f) {
                animationProgress -= 1f
            }
        }
        return dungeonSprite.data[(animationProgress * dungeonSprite.frames).toInt()]
    }

    override fun getSprite(renderEngine: RenderEngine, frame: Int): TextureRegion {
        if (!::dungeonSprite.isInitialized) {
            init(renderEngine.resourceManager)
        }
        return dungeonSprite.data[frame]
    }

    override fun resetAnimation() {
        animationProgress = 0f
    }

    override val frameCount: Int
        get() = dungeonSprite.frames
}
