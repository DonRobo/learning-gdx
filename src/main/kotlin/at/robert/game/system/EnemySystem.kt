package at.robert.game.system

import at.robert.game.component.Animated
import at.robert.game.component.EnemyComponent
import at.robert.game.component.Player
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.absoluteValue

class EnemySystem : IteratingSystem(
    allOf(EnemyComponent::class, TransformComponent::class).get(),
) {
    private lateinit var playerList: ImmutableArray<Entity>
    private lateinit var player: Entity

    override fun addedToEngine(engine: Engine) {
        playerList = engine.getEntitiesFor(allOf(Player::class, TransformComponent::class).get())
        super.addedToEngine(engine)
    }

    override fun update(deltaTime: Float) {
        player = playerList.singleOrNull() ?: return
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemy = entity[EnemyComponent.mapper]!!
        val transform = entity[TransformComponent.mapper]!!
        val playerTransform = player[TransformComponent.mapper]!!
        val enemyAnimator = entity[Animated.mapper]?.animator

        val xDist = playerTransform.x - transform.x
        val yDist = playerTransform.y - transform.y

        when {
            xDist.absoluteValue < 1f && yDist.absoluteValue < 1f -> {
                enemyAnimator?.idle()
            }
            xDist.absoluteValue > yDist.absoluteValue -> {
                if (xDist > 0) {
                    enemyAnimator?.walkRight()
                    transform.x += enemy.speed * deltaTime
                } else {
                    enemyAnimator?.walkLeft()
                    transform.x -= enemy.speed * deltaTime
                }
            }
            yDist.absoluteValue > xDist.absoluteValue -> {
                if (yDist > 0) {
                    enemyAnimator?.walkUp()
                    transform.y += enemy.speed * deltaTime
                } else {
                    enemyAnimator?.walkDown()
                    transform.y -= enemy.speed * deltaTime
                }
            }
        }
    }
}
