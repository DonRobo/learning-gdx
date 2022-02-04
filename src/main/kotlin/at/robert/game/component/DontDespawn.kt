package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class DontDespawn : Component {
    companion object : Mapper<DontDespawn>()
}

fun EngineEntity.withDontDespawn() {
    with<DontDespawn>()
}
