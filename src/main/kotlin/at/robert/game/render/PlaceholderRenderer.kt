package at.robert.game.render

import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import ktx.ashley.get

class PlaceholderRenderer() : Renderer {
    override fun render(renderEngine: RenderEngine, entity: Entity) {
        val transform = entity[TransformComponent.mapper]!!
        renderEngine.setState(RenderState.FILLED)
        renderEngine.shapeRenderer.setColor(0f, 0f, 0f, 1f)
        renderEngine.shapeRenderer.rect(
            transform.x - transform.width / 2,
            transform.y - transform.height / 2,
            transform.width / 2,
            transform.height / 2,
            transform.width,
            transform.height,
            1f,
            1f,
            0f,
        )
    }

}
