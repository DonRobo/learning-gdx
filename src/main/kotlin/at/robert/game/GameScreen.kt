package at.robert.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.PolygonRegion
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.ScreenUtils
import ktx.app.KtxScreen
import kotlin.math.*

class GameScreen : KtxScreen {
    private val batch = PolygonSpriteBatch()

    private val car = TextureRegion(Texture("car.png"))
    private var camera = OrthographicCamera().apply {
        setToOrtho(true, 800f, 600f)
    }

    private var polygonRegion = createPolyRegion()

    private var carRenderObject = RenderObject(80f, car)

    private var x = 400f
    private var y = 300f
    private var angle = 0f
    private var speed = 0f

    override fun render(delta: Float) {
        update(delta)

        ScreenUtils.clear(.4f, .4f, .5f, 1f)
        batch.projectionMatrix = camera.combined
        batch.begin()
        renderGame()
        batch.end()
    }

    override fun dispose() {
        batch.dispose()
        car.texture.dispose()
    }

    private fun update(delta: Float) {
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

    private fun renderGame() {
        batch.renderObject(
            x, y, angle + 90, carRenderObject
        )
        batch.draw(polygonRegion, 0f, 0f)
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
}
