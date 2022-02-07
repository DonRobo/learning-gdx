package at.robert.game.render

import at.robert.game.ResourceManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

enum class RenderState {
    SPRITE, FILLED, LINES, NONE
}

class RenderEngine(
    val spriteBatch: PolygonSpriteBatch,
    val shapeRenderer: ShapeRenderer,
    val resourceManager: ResourceManager,
) {
    private var currentState = RenderState.NONE

    private fun end() {
        when (currentState) {
            RenderState.SPRITE -> spriteBatch.end()
            RenderState.FILLED -> shapeRenderer.end()
            RenderState.LINES -> shapeRenderer.end()
            RenderState.NONE -> {}
        }
        currentState = RenderState.NONE
    }

    fun setState(state: RenderState) {
        when (state) {
            currentState -> return
            RenderState.NONE -> end()
            RenderState.SPRITE -> {
                end()
                spriteBatch.begin()
            }
            RenderState.FILLED -> {
                end()
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
            }
            RenderState.LINES -> {
                end()
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            }
        }
        currentState = state
        switches++
    }

    fun setCamera(camera: OrthographicCamera) {
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined
    }

    private var switches = 0
    fun measureSwitches(block: (RenderEngine) -> Unit): Int {
        switches = 0
        block(this)
        return switches
    }
}

