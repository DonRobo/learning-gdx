package at.robert.game.system

import at.robert.game.component.MoveTowardsPlayer
import at.robert.game.component.PlayerControlled
import at.robert.game.component.RigidBody
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.math.minus
import ktx.math.times
import ktx.math.vec2

class EnemySystem : IteratingSystem(allOf(RigidBody::class, MoveTowardsPlayer::class).get()) {

    lateinit var playerPosition: Vector2

    override fun update(deltaTime: Float) {
        playerPosition =
            engine.getEntitiesFor(allOf(PlayerControlled::class, TransformComponent::class).get()).singleOrNull()?.let {
                val tc = it[TransformComponent.mapper]!!
                vec2(tc.x, tc.y)
            } ?: return
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val rb = entity[RigidBody.mapper]!!
        val moveTowardsPlayer = entity[MoveTowardsPlayer.mapper]!!

        val direction = (playerPosition - rb.body.position).nor()

        if (rb.body.type == BodyDef.BodyType.KinematicBody)
            rb.body.linearVelocity = direction * moveTowardsPlayer.speed
        else if (rb.body.type == BodyDef.BodyType.DynamicBody)
            rb.body.applyForceToCenter(direction * (moveTowardsPlayer.speed * deltaTime * rb.body.mass * 50f), true)
    }
}
