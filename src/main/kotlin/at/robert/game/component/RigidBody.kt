package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import ktx.ashley.Mapper

class RigidBody : Component {
    lateinit var body: Body

    companion object : Mapper<RigidBody>()
}
