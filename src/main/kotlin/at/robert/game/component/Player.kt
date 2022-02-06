package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Player : Component {
    var currentDirection = 0

    companion object : Mapper<Player>()
}

fun EngineEntity.withPlayer() {
    with<Player> {
        currentDirection = 0
    }
}
