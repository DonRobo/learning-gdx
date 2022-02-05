package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class MoveTowardsPlayer(
    var speed: Float = 0f,
) : Component {
    companion object : Mapper<MoveTowardsPlayer>()
}

fun EngineEntity.withMoveTowardsPlayer(speed: Float) {
    with<MoveTowardsPlayer> {
        this.speed = speed
    }
}
