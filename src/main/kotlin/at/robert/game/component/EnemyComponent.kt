package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class EnemyComponent : Component {
    var speed: Float = 0f

    companion object : Mapper<EnemyComponent>()
}

fun EngineEntity.withEnemyComponent(speed: Float) {
    with<EnemyComponent> {
        this.speed = speed
    }
}
