package at.robert.game.entity

import com.badlogic.ashley.core.Engine
import ktx.ashley.EngineEntity
import ktx.ashley.entity

@JvmInline
value class EntityPrefab(
    val prefab: EngineEntity.() -> Unit
)

fun Engine.addEntity(entityPrefab: EntityPrefab) {
    entity {
        entityPrefab.prefab(this)
    }
}
