package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class MovingComponent() : Component {
    var vX: Float = 0f
    var vY: Float = 0f

    companion object : Mapper<MovingComponent>()
}

fun EngineEntity.withMovingComponent(speed: Float = 0f, direction: Float = 0f) {
    with<MovingComponent> {
        vX = speed * MathUtils.cos(direction * MathUtils.degreesToRadians)
        vY = speed * MathUtils.sin(direction * MathUtils.degreesToRadians)
    }
}
