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
) : Component {
    companion object : Mapper<TransformComponent>()
}

fun EngineEntity.withTransformComponent(
    x: Float = 0f,
    y: Float = 0f,
    width: Float,
    height: Float,
) {
    with<TransformComponent> {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }
}

fun TransformComponent.squaredDistanceTo(other: TransformComponent): Float {
    val dx = x - other.x
    val dy = y - other.y
    return dx * dx + dy * dy
}
