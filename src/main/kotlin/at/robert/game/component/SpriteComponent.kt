package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class SpriteComponent : Component {
    lateinit var textureRegion: TextureRegion

    companion object : Mapper<SpriteComponent>()
}

fun EngineEntity.withSpriteComponent(texture: TextureRegion) {
    with<SpriteComponent> {
        textureRegion = texture
    }
}
