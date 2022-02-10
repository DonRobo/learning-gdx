package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Pushable : Component {
    companion object : Mapper<Pushable>()
}

fun EngineEntity.withPushable() {
    with<Pushable>()
}
