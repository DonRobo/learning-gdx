package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class PlayerControlled : Component {
    companion object : Mapper<PlayerControlled>()
}

fun EngineEntity.withPlayerControlled() {
    with<PlayerControlled>()
}
