package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.Body
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class CollidingComponent : Component {
    var body: Body? = null
    var rect: Rectangle? = null
    var circleRadius: Float? = null

    companion object : Mapper<CollidingComponent>()
}

fun EngineEntity.withColliding(
    negativeXOffset: Float,
    positiveXOffset: Float,
    negativeYOffset: Float,
    positiveYOffset: Float,
) {
    with<CollidingComponent> {
        body = null
        this.rect = Rectangle(
            negativeXOffset,
            negativeYOffset,
            positiveXOffset - negativeXOffset,
            positiveYOffset - negativeYOffset
        )
    }
}

fun EngineEntity.withCircularCollider(radius: Float) {
    with<CollidingComponent> {
        this.body = null
        this.rect = null
        this.circleRadius = radius
    }
}
