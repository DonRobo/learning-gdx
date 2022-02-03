package at.robert.game.system

import at.robert.game.component.RigidBody
import at.robert.game.component.Rotating
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get

class RotationSystem : EntitySystem() {
    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities =
            engine.getEntitiesFor(allOf(Rotating::class, TransformComponent::class).exclude(RigidBody::class).get())
    }

    override fun update(deltaTime: Float) {
        entities.forEach {
            val transform = it[TransformComponent.mapper]!!

            transform.rotationDeg += it[Rotating.mapper]!!.speed * deltaTime
        }
    }

}
