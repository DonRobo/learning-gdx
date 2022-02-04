package at.robert.game.system

import at.robert.game.component.PlayerControlled
import at.robert.game.component.RigidBody
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerControlSystem : IteratingSystem(
    allOf(
        RigidBody::class,
        PlayerControlled::class,
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val kinetic = entity[RigidBody.mapper]!!

        var xV = 0f
        var yV = 0f

        val speed = 100f

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xV += deltaTime * speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xV -= deltaTime * speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yV += deltaTime * speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yV -= deltaTime * speed
        }

        kinetic.body.setLinearVelocity(xV, yV)
    }
}
