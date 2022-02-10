package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Despawnable : Component {
    companion object : Mapper<Despawnable>()
}

fun EngineEntity.withDespawnable() {
    with<Despawnable>()
}
