package at.robert.game.render.sprite

import at.robert.game.ResourceManager
import at.robert.game.render.RenderEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class DungeonSpriteProvider(
    val spriteName: String,
) : TextureProvider {

    private class DungeonSprite(
        val name: String,
        val width: Int,
        val height: Int,
        val frames: Int,
        val data: Array<TextureRegion>
    )

    private lateinit var dungeonSprite: DungeonSprite

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

    override fun getTextureRegion(renderEngine: RenderEngine): TextureRegion {
        if (!::dungeonSprite.isInitialized) {
            init(renderEngine.resourceManager)
        }
        return dungeonSprite.data[0]
    }

    //
//    fun render(entity: Entity, deltaTime: Float) {
//        val dungeonSpriteComponent = entity[DungeonTileSprite.mapper]!!
//
//        //TODO move into animation system
//        dungeonSpriteComponent.animationProgress += deltaTime * dungeonSpriteComponent.animationSpeed * dungeonSpriteComponent.animationFrames
//        if (dungeonSpriteComponent.animationProgress >= dungeonSpriteComponent.animationFrames) {
//            dungeonSpriteComponent.animationProgress -= dungeonSpriteComponent.animationFrames
//        }
//
//        if (dungeonSpriteComponent.textureRegion == null) {
//            dungeonSpriteComponent.textureRegion =
//                (0 until dungeonSpriteComponent.animationFrames).map {
//                    dungeonSpriteMap[dungeonSpriteComponent.sprite + it]!!
//                }.toTypedArray()
//        }
//
//        val transform = entity[TransformComponent.mapper]!!
//        spriteRenderer.render(
//            transform,
//            dungeonSpriteComponent.textureRegion!![
//                    min(dungeonSpriteComponent.animationProgress.toInt(), dungeonSpriteComponent.animationFrames - 1)
//            ]
//        )
//    }
//
//    fun dispose() {
//        dungeonSprite.dispose()
//    }
}
