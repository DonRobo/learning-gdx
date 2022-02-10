package at.robert.game.system

import at.robert.game.component.CollidingComponent
import at.robert.game.component.Pushable
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import ktx.math.vec2
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class Box2DPhysicsSystem : EntitySystem(6) {
    private lateinit var physicsEntities: ImmutableArray<Entity>
    internal lateinit var world: World

    private val entityListener = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            val transform = entity[TransformComponent.mapper]!!
            val colliding = entity[CollidingComponent.mapper]!!
            val pushable = entity[Pushable.mapper]

            colliding.body = world.body {
                type = when {
                    pushable != null -> BodyType.DynamicBody
                    else -> BodyType.StaticBody //TODO kinetic body?
                }
                linearDamping = 1f
                fixedRotation = true
                box(
                    position = vec2(colliding.rect.x, colliding.rect.y),
                    width = colliding.rect.w,
                    height = colliding.rect.h,
                ) {
                    userData = entity
                    density = pushable?.density ?: 40f
                    restitution = 0f //TODO configure?
                    friction = 0f //TODO configure?
                }
            }
            colliding.body!!.setTransform(
                transform.x,
                transform.y,
                0f
            )
        }

        override fun entityRemoved(entity: Entity) {
            val collidingComponent = entity[CollidingComponent.mapper]!!
            world.destroyBody(collidingComponent.body)
        }
    }

    override fun addedToEngine(engine: Engine) {
        world = createWorld()
        physicsEntities = engine.getEntitiesFor(allOf(CollidingComponent::class, TransformComponent::class).get())
        engine.addEntityListener(
            allOf(CollidingComponent::class, TransformComponent::class).get(), entityListener
        )
    }

    override fun removedFromEngine(engine: Engine) {
        engine.removeEntityListener(entityListener)
        world.dispose()
    }

    override fun update(deltaTime: Float) {
        measureTime {
            physicsEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val body = it[CollidingComponent.mapper]!!.body!!

                val multiplier = 1f / deltaTime
                body.setLinearVelocity(
                    (transform.x - body.position.x) * multiplier,
                    (transform.y - body.position.y) * multiplier,
                )
//                body.setTransform(
//                    transform.x,
//                    transform.y,
//                    0f
//                )
            }
            world.step(deltaTime, 6, 2)
            physicsEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val body = it[CollidingComponent.mapper]!!.body!!

//                val oldX = transform.x
//                val oldY = transform.y

                transform.x = body.position.x
                transform.y = body.position.y

//                val movedTooMuchX = transform.x - oldX
//                val movedTooMuchY = transform.y - oldY
//                if (movedTooMuchX.absoluteValue > 0.01f || movedTooMuchY.absoluteValue > 0.01f) {
//                    println("$movedTooMuchX $movedTooMuchY")
//                }
            }
        }.let {
            PerformanceMetrics.physics = it
        }
    }
}
