package at.robert.game.system

import at.robert.game.component.Animated
import at.robert.game.component.Player
import at.robert.game.component.TransformComponent
import at.robert.game.entity.Bullet
import at.robert.game.entity.addEntity
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import ktx.ashley.allOf
import ktx.ashley.get

class PlayerControlSystem : IteratingSystem(
    allOf(
        Player::class,
    ).get(),
    4
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformComponent = entity[TransformComponent.mapper]!!

        val speed = 8f

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

        val pc = entity[Player.mapper]!!
        val chosenDirection: Int = when {
            pc.currentDirection >= 0 && priorityDirection[pc.currentDirection] -> pc.currentDirection
            priorityDirection.any { it } -> priorityDirection.indexOf(true)
            pc.currentDirection >= 0 && regularDirection[pc.currentDirection] -> pc.currentDirection
            regularDirection.any { it } -> regularDirection.indexOf(true)
            else -> -1
        }

        pc.currentDirection = chosenDirection
        val animator = entity[Animated.mapper]?.animator
        when (chosenDirection) {
            0 -> {
                transformComponent.y += speed * deltaTime
                animator?.walkUp()
            }
            1 -> {
                transformComponent.x += speed * deltaTime
                animator?.walkRight()
            }
            2 -> {
                transformComponent.y -= speed * deltaTime
                animator?.walkDown()
            }
            3 -> {
                transformComponent.x -= speed * deltaTime
                animator?.walkLeft()
            }
            else -> {
                animator?.idle()
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            engine.addEntity(Bullet(transformComponent.x, transformComponent.y, MathUtils.random(0f, 360f)))
        }
    }
}
