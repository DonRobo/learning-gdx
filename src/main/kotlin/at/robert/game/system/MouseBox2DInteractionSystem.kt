package at.robert.game.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.ashley.getSystem
import ktx.box2d.query

class MouseBox2DInteractionSystem(
    private val camera: OrthographicCamera,
) : EntitySystem() {

    private lateinit var box2d: PhysicsSystem

    private var body: Body? = null

    override fun addedToEngine(engine: Engine) {
        box2d = engine.getSystem()
    }

    override fun update(deltaTime: Float) {
        if (body == null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            val pos: Vector3 = camera.unproject(
                Vector3(
                    Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat(),
                    0f
                )
            )

            var body: Body? = null
            box2d.box2DWorld.query(pos.x, pos.y, pos.x, pos.y) {
                if (body == null && it.body.type == BodyDef.BodyType.DynamicBody && it.testPoint(pos.x, pos.y)) {
                    body = it.body
                    true
                } else {
                    false
                }
            }
            if (body == null) {
                return
            }

            body!!.type = BodyDef.BodyType.KinematicBody
            this.body = body
        } else if (body != null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            val pos: Vector3 = camera.unproject(
                Vector3(
                    Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat(),
                    0f
                )
            )

            body!!.angularVelocity = 0f
            body!!.setLinearVelocity(0f, 0f)
            body!!.setTransform(pos.x, pos.y, body!!.angle)
        } else if (body != null) {
            body!!.type = BodyDef.BodyType.DynamicBody
            body = null
        }
    }
}
