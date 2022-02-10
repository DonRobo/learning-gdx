package at.robert.game.system

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import ktx.ashley.getSystem

class Box2DDebugRenderSystem(
    private val camera: OrthographicCamera
) : EntitySystem(13) {

    private val debugRenderer = Box2DDebugRenderer(
        true,
        true,
        false,
        true,
        false,
        true
    )

    private var active = false

    override fun update(deltaTime: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            active = !active
        }
        if (!active) return

        val physicsSystem = engine.getSystem<Box2DPhysicsSystem>()
        debugRenderer.render(physicsSystem.world, camera.combined)
    }
}
