package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import ktx.ashley.getSystem

class Box2DDebugRenderSystem(
    private val camera: OrthographicCamera
) : EntitySystem(-1) {

    private val debugRenderer = Box2DDebugRenderer(
        true,
        true,
        false,
        true,
        false,
        true
    )

    override fun update(deltaTime: Float) {
        val physicsSystem = engine.getSystem<PhysicsSystem>()
        debugRenderer.render(physicsSystem.box2DWorld, camera.combined)
    }
}
