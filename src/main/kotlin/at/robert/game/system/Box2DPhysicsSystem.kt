package at.robert.game.system

import at.robert.game.component.*
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.box2d.*
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
            val pushable = entity[Pushable.mapper]
            val colliding = entity[CollidingComponent.mapper]!!
            val moving = entity[MovingComponent.mapper]
            val hitDetector = entity[HitDetector.mapper]
            val collidingRect = colliding.rect
            val collidingCircle = colliding.circleRadius

            colliding.body = world.body {
                type = when {
                    pushable != null -> BodyType.DynamicBody
                    moving != null -> BodyType.KinematicBody
                    else -> BodyType.StaticBody //TODO kinetic body?
                }
                linearDamping = 1f
                fixedRotation = true
                fun FixtureDefinition.defineFixture() {
                    userData = entity
                    density = pushable?.density ?: 40f
                    restitution = 0f
                    friction = 0f
                    isSensor = hitDetector != null
                }
                when {
                    collidingRect != null && pushable == null -> {
                        box(
                            position = vec2(
                                collidingRect.x + collidingRect.width / 2f,
                                collidingRect.y + collidingRect.height / 2f
                            ),
                            width = collidingRect.width,
                            height = collidingRect.height,
                        ) {
                            defineFixture()
                        }
                    }
                    collidingRect != null && pushable != null -> {
                        val vertices = FloatArray(8)

                        vertices[0] = collidingRect.x + collidingRect.width
                        vertices[1] = collidingRect.y + collidingRect.height / 2f

                        vertices[2] = collidingRect.x + collidingRect.width / 2f
                        vertices[3] = collidingRect.y + collidingRect.height

                        vertices[4] = collidingRect.x
                        vertices[5] = collidingRect.y + collidingRect.height / 2f

                        vertices[6] = collidingRect.x + collidingRect.width / 2f
                        vertices[7] = collidingRect.y

                        polygon(
                            vertices = vertices,
                        ) {
                            defineFixture()
                        }
                    }
                    collidingCircle != null -> {
                        circle(radius = collidingCircle) { defineFixture() }
                    }
                    else -> throw IllegalStateException("CollidingComponent is not configured correctly")
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
                val moving = it[MovingComponent.mapper]

                val multiplier = 1f / deltaTime
                body.setLinearVelocity(
                    (transform.x - body.position.x) * multiplier + (moving?.vX ?: 0f),
                    (transform.y - body.position.y) * multiplier + (moving?.vY ?: 0f)
                )
            }
            world.step(deltaTime, 6, 2)
            physicsEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val body = it[CollidingComponent.mapper]!!.body!!

                transform.x = body.position.x
                transform.y = body.position.y
            }
        }.let {
            PerformanceMetrics.physics = it
        }
    }
}
