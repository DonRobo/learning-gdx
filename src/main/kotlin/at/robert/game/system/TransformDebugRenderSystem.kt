package at.robert.game.system

import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.ashley.allOf
import ktx.ashley.get

class TransformDebugRenderSystem(
    private val camera: OrthographicCamera,
    private val shapeRenderer: ShapeRenderer,
) : IteratingSystem(
    allOf(TransformComponent::class).get(),
    12,
) {
    private var active = false

    override fun update(deltaTime: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            active = !active
        }
        if (!active) return

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.GREEN
        super.update(deltaTime)
        shapeRenderer.end()
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        shapeRenderer.rect(
            transform.x - transform.width / 2f,
            transform.y - transform.height / 2f,
            transform.width,
            transform.height
        )
    }

}
