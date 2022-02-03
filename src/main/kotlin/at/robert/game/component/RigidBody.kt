package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class RigidBody(
    var type: BodyDef.BodyType = BodyDef.BodyType.DynamicBody,
    var density: Float = 1f,
    var restitution: Float = .1f,
    var friction: Float = .1f,
) : Component {
    lateinit var body: Body

    companion object : Mapper<RigidBody>()
}

fun EngineEntity.withRigidBody(
    type: BodyDef.BodyType,
    density: Float,
    restitution: Float,
    friction: Float,
) {
    with<RigidBody> {
        this.type = type
        this.density = density
        this.restitution = restitution
        this.friction = friction
    }
}
