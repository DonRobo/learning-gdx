package at.robert.game.render

import at.robert.game.component.DungeonTileSprite
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import ktx.ashley.get
import kotlin.math.min

class DungeonSpriteRenderer(
    private val spriteRenderer: SpriteRenderer,
) : Disposable {
    private val dungeonSprite: Texture = Texture("dungeontileset.png")
    private val dungeonSpriteMap: Map<String, TextureRegion>

    init {
        val r = Regex("(\\w+) (\\d+) (\\d+) (\\d+) (\\d+)(?: (\\d+))?")
        val spriteMap = mutableMapOf<String, TextureRegion>()
        Gdx.files.internal("dungeontileset.txt").reader().forEachLine {
            val match = r.matchEntire(it) ?: return@forEachLine

            val (name, x, y, width, height, animationFrames) = match.destructured
            val animationRange = 0 until (animationFrames.toIntOrNull() ?: 1)
            for (i in animationRange) {
                val widthInt = width.toInt()
                spriteMap[name + i] = TextureRegion(
                    dungeonSprite,
                    x.toInt() + widthInt * i,
                    y.toInt(),
                    widthInt,
                    height.toInt()
                )
            }
        }
        this.dungeonSpriteMap = spriteMap
    }

    fun render(entity: Entity, deltaTime: Float) {
        val dungeonSpriteComponent = entity[DungeonTileSprite.mapper]!!

        //TODO move into animation system
        dungeonSpriteComponent.animationProgress += deltaTime * dungeonSpriteComponent.animationSpeed * dungeonSpriteComponent.animationFrames
        if (dungeonSpriteComponent.animationProgress >= dungeonSpriteComponent.animationFrames) {
            dungeonSpriteComponent.animationProgress -= dungeonSpriteComponent.animationFrames
        }

        if (dungeonSpriteComponent.textureRegion == null) {
            dungeonSpriteComponent.textureRegion =
                (0 until dungeonSpriteComponent.animationFrames).map {
                    dungeonSpriteMap[dungeonSpriteComponent.sprite + it]!!
                }.toTypedArray()
        }

        val transform = entity[TransformComponent.mapper]!!
        spriteRenderer.render(
            transform,
            dungeonSpriteComponent.textureRegion!![
                    min(dungeonSpriteComponent.animationProgress.toInt(), dungeonSpriteComponent.animationFrames - 1)
            ]
        )
    }

    override fun dispose() {
        dungeonSprite.dispose()
    }
}
