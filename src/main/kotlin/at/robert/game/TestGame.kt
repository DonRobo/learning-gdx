package at.robert.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import kotlin.math.*

class TestGame : Game() {
    private lateinit var batch: PolygonSpriteBatch

    private lateinit var car: TextureRegion
    private lateinit var camera: OrthographicCamera

    private lateinit var polygonRegion: PolygonRegion

    private lateinit var carRenderObject: RenderObject

    private var x = 400f
    private var y = 300f
    private var angle = 0f
    private var speed = 0f

    var maxTimestep = 0.1f

    override fun create() {
        batch = PolygonSpriteBatch()

        car = TextureRegion(Texture("car.png"))
        carRenderObject = RenderObject(80f, car)
        camera = OrthographicCamera()
        camera.setToOrtho(true, 800f, 600f)

        polygonRegion = createPolyRegion()
    }

    private fun createPolyRegion(): PolygonRegion {
        val vertices = floatArrayOf(0.0f, 0.0f, 100.0f, 0.0f, 70.0f, 100.0f, 0.0f, 100.0f)
        val indices = shortArrayOf(0, 1, 3, 1, 2, 3)
        return PolygonRegion(car, vertices, indices).apply {
            val coords = floatArrayOf(0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f)
            coords.forEachIndexed { index, fl ->
                textureCoords[index] = fl
            }
        }
    }

    private var gameStart = 0L
    private var lastUpdate = 0L
    override fun render() {
        update()

        ScreenUtils.clear(.4f, .4f, .5f, 1f)
        batch.projectionMatrix = camera.combined
        batch.begin()
        renderGame()
        batch.end()
    }

    private fun renderGame() {
        batch.renderObject(
            x, y, angle + 90, carRenderObject
        )
        batch.draw(polygonRegion, 0f, 0f)
    }

    private fun update(gameTime: Float, delta: Float) {
        val turningSpeed = 0.9f * 0.9f.pow(speed / 100f)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle -= turningSpeed * delta * speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle += turningSpeed * delta * speed
        }

        x += cos(Math.toRadians(angle.toDouble()).toFloat()) * speed * delta
        y += sin(Math.toRadians(angle.toDouble()).toFloat()) * speed * delta

        speed *= 0.9f.pow(delta)
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            speed += 300f * delta
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            speed -= 900f * delta
        }
        speed = min(speed, 1000f)
        speed = max(speed, 0f)

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            x = Gdx.input.x.toFloat() + carRenderObject.width / 2f
            y = Gdx.input.y.toFloat() + carRenderObject.height / 2f
        }
    }

    private fun update() {
        val now = System.nanoTime()
        if (gameStart == 0L)
            gameStart = now

        val delta = now - lastUpdate
        val gameTime = now - gameStart
        update(secondsFromNanos(gameTime), min(secondsFromNanos(delta), maxTimestep))
        lastUpdate = now
    }

    override fun dispose() {
        batch.dispose()
        car.texture.dispose()
    }
}
