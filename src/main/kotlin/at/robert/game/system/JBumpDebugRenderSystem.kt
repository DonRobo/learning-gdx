package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.dongbat.jbump.Point
import ktx.ashley.getSystem

class JBumpDebugRenderSystem(
    private val camera: OrthographicCamera,
    private val shapeRenderer: ShapeRenderer,
) : EntitySystem(11) {


    override fun update(deltaTime: Float) {
        val physicsSystem = engine.getSystem<PhysicsSystem>()
        val jbumpWorld = physicsSystem.jbumpWorld
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        jbumpWorld.rects.forEach {
            shapeRenderer.rect(it.x, it.y, it.w, it.h)
        }
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        val p = Point()
        jbumpWorld.rects.forEach {
            val c = jbumpWorld.toCell(it.x + it.w / 2f, it.y + it.h / 2f, p)!!
            shapeRenderer.rect(
                (c.x - 1f) * jbumpWorld.cellSize,
                (c.y - 1f) * jbumpWorld.cellSize,
                jbumpWorld.cellSize,
                jbumpWorld.cellSize
            )
        }
        shapeRenderer.end()
    }
}
