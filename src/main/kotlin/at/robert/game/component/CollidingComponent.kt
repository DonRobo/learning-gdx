package at.robert.game.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.dongbat.jbump.Item
import com.dongbat.jbump.Rect
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class CollidingComponent : Component {
    var item: Item<Entity>? = null
    var moved = false
    var rect: Rect? = null

    companion object : Mapper<CollidingComponent>()
}

fun EngineEntity.withColliding(
    negativeXOffset: Float? = null,
    positiveXOffset: Float? = null,
    negativeYOffset: Float? = null,
    positiveYOffset: Float? = null
) {
    with<CollidingComponent> {
        item = null
        moved = false
        this.rect =
            if (negativeXOffset != null && positiveXOffset != null && negativeYOffset != null && positiveYOffset != null) {
                Rect(
                    negativeXOffset,
                    negativeYOffset,
                    positiveXOffset - negativeXOffset,
                    positiveYOffset - negativeYOffset
                )
            } else {
                null
            }
    }
}
