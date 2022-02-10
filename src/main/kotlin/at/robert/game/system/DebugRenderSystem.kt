package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import kotlin.math.roundToInt
import kotlin.time.Duration

object PerformanceMetrics {
    var renderModeSwitches: Int = 0
    var despawnSystem: Duration? = null
    var renderTime: Duration? = null
    var physics: Duration? = null
    var pushes: Int? = null
    var jbumpCells: Int? = null
    var jbumpMoves: Int = 0
    var overallTime: Duration? = null
}

class DebugRenderSystem(
    private val spriteBatch: PolygonSpriteBatch,
    private val font: BitmapFont,
) : EntitySystem(15) {

    var active = true

    override fun update(deltaTime: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            active = !active
        }
        if (!active) return

        val oldProjectionMatrix = spriteBatch.projectionMatrix.cpy()
        spriteBatch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        spriteBatch.begin()
        var currentPos = 10f
        val lineDistance = 20f
        fun print(text: String) {
            font.draw(spriteBatch, text, 10f, Gdx.graphics.height - currentPos)
                .also { currentPos += lineDistance }
        }

        print("FPS: ${Gdx.graphics.framesPerSecond}")
        print("Entities: ${engine.entities.size()}")
        print("Render time: ${PerformanceMetrics.renderTime}")
        print("Render mode switches: ${PerformanceMetrics.renderModeSwitches}")
        print("DespawnSystem: ${PerformanceMetrics.despawnSystem}")
        print("Physics: ${PerformanceMetrics.physics}")
//        print("Push calculations: ${PerformanceMetrics.pushes}")
//        print("JBump cells: ${PerformanceMetrics.jbumpCells}")
//        print("JBump moves: ${PerformanceMetrics.jbumpMoves}")
        print("Overall time: ${PerformanceMetrics.overallTime}")
        val theoreticalFps =
            (1000000f / (PerformanceMetrics.overallTime?.inWholeMicroseconds?.toFloat() ?: 100f)).roundToInt()
        print("Theoretical FPS: $theoreticalFps")
        spriteBatch.end()

        spriteBatch.projectionMatrix = oldProjectionMatrix
    }
}
