package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with


class TransformComponent(
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 1f,
    var height: Float = 1f,
    var rotationDeg: Float = 0f,
) : Component {
    companion object : Mapper<TransformComponent>()
}

fun EngineEntity.withTransformComponent(
    x: Float = 0f,
    y: Float = 0f,
    width: Float,
    height: Float,
    rotationDeg: Float = 0f,
) {
    with<TransformComponent> {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        this.rotationDeg = rotationDeg
    }
}
