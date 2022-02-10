package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Destroyable : Component {
    var hp: Int = 0

    companion object : Mapper<Destroyable>()
}

fun EngineEntity.withDestroyable(hp: Int) {
    with<Destroyable> {
        this.hp = hp
    }
}
