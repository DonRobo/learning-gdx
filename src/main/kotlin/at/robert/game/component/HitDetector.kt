package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class HitDetector internal constructor() : Component {
    companion object : Mapper<HitDetector>()
}

fun EngineEntity.withHitDetector() {
    with<HitDetector>()
}
