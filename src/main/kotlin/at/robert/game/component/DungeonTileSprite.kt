package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class DungeonTileSprite(
    var sprite: String = "",
    var animationFrames: Int = 1,
    var animationProgress: Float = 0f,
    var animationSpeed: Float = 1f,
    //TODO store this in renderer and only index here
    var textureRegion: Array<TextureRegion>? = null
) : Component {
    companion object : Mapper<DungeonTileSprite>()
}

fun EngineEntity.withDungeonTileSprite(
    sprite: String,
    animationFrames: Int = 1,
    animationProgress: Float = 0f,
    animationSpeed: Float = 1f,
) {
    with<DungeonTileSprite> {
        this.sprite = sprite
        this.animationFrames = animationFrames
        this.animationProgress = animationProgress
        this.animationSpeed = animationSpeed
        this.textureRegion = null
    }
}
