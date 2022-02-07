package at.robert.game.render

import com.badlogic.ashley.core.Entity

interface Renderer {
    fun render(renderEngine: RenderEngine, entity: Entity)
}
