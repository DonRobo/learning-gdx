package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class SimpleRigidBody : Component {
    var item: Item<Entity>? = null

    companion object : Mapper<SimpleRigidBody>()
}

fun EngineEntity.withSimpleRigidBody() {
    with<SimpleRigidBody> {
        item = null
    }
}
