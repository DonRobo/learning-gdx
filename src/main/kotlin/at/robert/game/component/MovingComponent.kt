package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class MovingComponent(
    var speed: Float = 0f,
    var directionDeg: Float = 0f,
) : Component {
    companion object : Mapper<MovingComponent>()
}

fun EngineEntity.withMovingComponent(speed: Float = 0f, direction: Float = 0f) {
    with<MovingComponent> {
        this.speed = speed
        this.directionDeg = direction
    }
}
