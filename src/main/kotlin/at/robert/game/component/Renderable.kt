package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Renderable : Component {
    var zIndex: Int = 0

    companion object : Mapper<Renderable>()
}

fun EngineEntity.withRenderable(zIndex: Int = 0) {
    with<Renderable> {
        this.zIndex = zIndex
    }
}
