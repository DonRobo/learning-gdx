package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class RenderPlaceholder : Component {
    companion object : Mapper<RenderPlaceholder>()
}

fun EngineEntity.withRenderPlaceholder() {
    with<RenderPlaceholder>()
}
