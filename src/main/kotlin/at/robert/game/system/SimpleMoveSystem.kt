package at.robert.game.system

import at.robert.game.component.MovingComponent
import at.robert.game.component.TransformComponent
import at.robert.game.toRadians
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils.cos
import com.badlogic.gdx.math.MathUtils.sin
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class SimpleMoveSystem : IteratingSystem(allOf(TransformComponent::class, MovingComponent::class).get()) {
    override fun update(deltaTime: Float) {
        measureTime {
            super.update(deltaTime)
        }.let {
            PerformanceMetrics.simpleMoveSystem = it
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]!!
        val moving = entity[MovingComponent.mapper]!!

        transform.x += cos(moving.directionDeg.toRadians()) * moving.speed * deltaTime
        transform.y += sin(moving.directionDeg.toRadians()) * moving.speed * deltaTime
    }
}
