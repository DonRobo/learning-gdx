package at.robert.game.component

import at.robert.game.render.Renderer
import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Renderable : Component {
    var zIndex: Int = 0
    lateinit var renderer: Renderer

    companion object : Mapper<Renderable>()
}

fun EngineEntity.withRenderable(renderer: Renderer, zIndex: Int = 0) {
    with<Renderable> {
        this.renderer = renderer
        this.zIndex = zIndex
    }
}
