package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import kotlin.time.Duration

object PerformanceMetrics {
    var simpleMoveSystem: Duration? = null
    var despawnSystem: Duration? = null
    var spriteRenderTime: Duration? = null
    var placeholderRenderTime: Duration? = null
    var box2D: Duration? = null
}

class DebugRenderSystem(
    private val spriteBatch: PolygonSpriteBatch,
    private val font: BitmapFont,
) : EntitySystem(15) {

    override fun update(deltaTime: Float) {
        val oldProjectionMatrix = spriteBatch.projectionMatrix.cpy()
        spriteBatch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        spriteBatch.begin()
        font.draw(spriteBatch, "FPS: ${Gdx.graphics.framesPerSecond}", 10f, Gdx.graphics.height - 10f)
        font.draw(spriteBatch, "Entities: ${engine.entities.size()}", 10f, Gdx.graphics.height - 30f)
        font.draw(
            spriteBatch,
            "SimpleMoveSystem: ${PerformanceMetrics.simpleMoveSystem}",
            10f,
            Gdx.graphics.height - 50f
        )
        font.draw(
            spriteBatch,
            "SpriteRenderSystem: ${PerformanceMetrics.spriteRenderTime}",
            10f,
            Gdx.graphics.height - 70f
        )
        font.draw(
            spriteBatch,
            "PlaceholderRenderSystem: ${PerformanceMetrics.placeholderRenderTime}",
            10f,
            Gdx.graphics.height - 90f
        )
        font.draw(spriteBatch, "DespawnSystem: ${PerformanceMetrics.despawnSystem}", 10f, Gdx.graphics.height - 110f)
        font.draw(spriteBatch, "Box2D: ${PerformanceMetrics.box2D}", 10f, Gdx.graphics.height - 130f)
        spriteBatch.end()

        spriteBatch.projectionMatrix = oldProjectionMatrix
    }
}
