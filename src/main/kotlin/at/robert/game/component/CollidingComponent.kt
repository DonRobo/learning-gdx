package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import com.dongbat.jbump.Item
import com.dongbat.jbump.Rect
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class CollidingComponent : Component {
    var item: Item<Entity>? = null
    var body: Body? = null
    lateinit var rect: Rect
    var lastPositionX: Float? = null
    var lastPositionY: Float? = null

    companion object : Mapper<CollidingComponent>()
}

fun EngineEntity.withColliding(
    negativeXOffset: Float,
    positiveXOffset: Float,
    negativeYOffset: Float,
    positiveYOffset: Float,
) {
    with<CollidingComponent> {
        item = null
        body = null
        lastPositionX = null
        lastPositionY = null
        this.rect = Rect(
            negativeXOffset,
            negativeYOffset,
            positiveXOffset - negativeXOffset,
            positiveYOffset - negativeYOffset
        )
    }
}
