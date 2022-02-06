package at.robert.game.render

import at.robert.game.component.TransformComponent

class PlaceholderRenderer(
    private val renderEngine: RenderEngine,
) {
    fun render(transform: TransformComponent) {
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
