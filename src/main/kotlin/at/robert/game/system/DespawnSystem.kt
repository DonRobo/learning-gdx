package at.robert.game.system

import at.robert.game.component.DontDespawn
import at.robert.game.component.PlayerControlled
import at.robert.game.component.TransformComponent
import at.robert.game.component.squaredDistanceTo
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.allOf
import ktx.ashley.exclude
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class DespawnSystem(
    var maxDistance: Float = 100f
) : IteratingSystem(allOf(TransformComponent::class).exclude(DontDespawn::class, PlayerControlled::class).get()) {

    private lateinit var players: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        players = engine.getEntitiesFor(allOf(PlayerControlled::class).get())
    }

    private val tm = ComponentMapper.getFor(TransformComponent::class.java)

    private lateinit var playerTransform: TransformComponent
    override fun update(deltaTime: Float) {
        measureTime {
            val p = players.singleOrNull() ?: return
            playerTransform = tm.get(p)
            super.update(deltaTime)
        }.let {
            PerformanceMetrics.despawnSystem = it
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val entTransform = tm.get(entity)
        if (entTransform.squaredDistanceTo(playerTransform) > maxDistance * maxDistance) {
            engine.removeEntity(entity)
        }
    }
}
