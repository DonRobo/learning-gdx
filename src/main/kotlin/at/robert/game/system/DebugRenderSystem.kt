package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import kotlin.math.roundToInt
import kotlin.time.Duration

object PerformanceMetrics {
    var renderModeSwitches: Int = 0
    var despawnSystem: Duration? = null
    var renderTime: Duration? = null
    var physics: Duration? = null
    var jbumpCells: Int? = null
    var overallTime: Duration? = null
}

class DebugRenderSystem(
    private val spriteBatch: PolygonSpriteBatch,
    private val font: BitmapFont,
) : EntitySystem(15) {

    override fun update(deltaTime: Float) {
        val oldProjectionMatrix = spriteBatch.projectionMatrix.cpy()
        spriteBatch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        spriteBatch.begin()
        var currentPos = 10f
        val lineDistance = 20f
        font.draw(spriteBatch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        font.draw(spriteBatch, "Entities: ${engine.entities.size()}", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        font.draw(
            spriteBatch,
            "Render time: ${PerformanceMetrics.renderTime}",
            10f,
            Gdx.graphics.height - currentPos
        ).also { currentPos += lineDistance }
        font.draw(
            spriteBatch,
            "Render mode switches: ${PerformanceMetrics.renderModeSwitches}",
            10f,
            Gdx.graphics.height - currentPos
        ).also { currentPos += lineDistance }
        font.draw(
            spriteBatch,
            "DespawnSystem: ${PerformanceMetrics.despawnSystem}",
            10f,
            Gdx.graphics.height - currentPos
        ).also { currentPos += lineDistance }
        font.draw(spriteBatch, "Physics: ${PerformanceMetrics.physics}", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        font.draw(spriteBatch, "JBump cells: ${PerformanceMetrics.jbumpCells}", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        font.draw(spriteBatch, "Overall time: ${PerformanceMetrics.overallTime}", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        val theoreticalFps =
            (1000000f / (PerformanceMetrics.overallTime?.inWholeMicroseconds?.toFloat() ?: 100f)).roundToInt()
        font.draw(spriteBatch, "Theoretical FPS: $theoreticalFps", 10f, Gdx.graphics.height - currentPos)
            .also { currentPos += lineDistance }
        spriteBatch.end()

        spriteBatch.projectionMatrix = oldProjectionMatrix
    }
}
