package at.robert.game.system

import at.robert.game.component.PlayerControlled
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerControlSystem : IteratingSystem(
    allOf(
        PlayerControlled::class,
    ).get(),
    5
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.mapper]!!

        val speed = 2f

        val priorityDirection = arrayOf(
            Gdx.input.isKeyJustPressed(Input.Keys.W),
            Gdx.input.isKeyJustPressed(Input.Keys.D),
            Gdx.input.isKeyJustPressed(Input.Keys.S),
            Gdx.input.isKeyJustPressed(Input.Keys.A),
        )
        val regularDirection = arrayOf(
            Gdx.input.isKeyPressed(Input.Keys.W),
            Gdx.input.isKeyPressed(Input.Keys.D),
            Gdx.input.isKeyPressed(Input.Keys.S),
            Gdx.input.isKeyPressed(Input.Keys.A),
        )

        val pc = entity[PlayerControlled.mapper]!!
        val chosenDirection: Int = when {
            pc.currentDirection >= 0 && priorityDirection[pc.currentDirection] -> pc.currentDirection
            priorityDirection.any { it } -> priorityDirection.indexOf(true)
            pc.currentDirection >= 0 && regularDirection[pc.currentDirection] -> pc.currentDirection
            regularDirection.any { it } -> regularDirection.indexOf(true)
            else -> -1
        }

        pc.currentDirection = chosenDirection
        when (chosenDirection) {
            0 -> transformComponent.y += speed * deltaTime
            1 -> transformComponent.x += speed * deltaTime
            2 -> transformComponent.y -= speed * deltaTime
            3 -> transformComponent.x -= speed * deltaTime
        }
    }
}
