package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Pushable : Component {
    var density: Float = 40f
    var awake = false

    companion object : Mapper<Pushable>()
}

fun EngineEntity.withPushable(density: Float) {
    with<Pushable> {
        this.density = density
        this.awake = false
    }
}
