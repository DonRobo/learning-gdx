package at.robert.game.system

import at.robert.game.component.RigidBody
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class Box2DSystem : EntitySystem() {

    val world = createWorld(Vector2(0f, 0f))

    private lateinit var entities: ImmutableArray<Entity>

    override fun addedToEngine(engine: Engine) {
        entities = engine.getEntitiesFor(allOf(RigidBody::class, TransformComponent::class).get())
        engine.addEntityListener(allOf(RigidBody::class, TransformComponent::class).get(), object : EntityListener {
            override fun entityAdded(entity: Entity) {
                val transform = entity[TransformComponent.mapper]!!
                val rigidBody = entity[RigidBody.mapper]!!
                rigidBody.body = world.body {
                    type = rigidBody.type
                    box(
                        width = transform.width,
                        height = transform.height,
                    ) {
                        userData = entity
                        density = rigidBody.density
                        restitution = rigidBody.restitution
                        friction = rigidBody.friction
                    }
                }
                rigidBody.body.setTransform(
                    transform.x,
                    transform.y,
                    transform.rotationDeg * MathUtils.degreesToRadians
                )
                rigidBody.body.linearVelocity = rigidBody.initialVelocity
            }

            override fun entityRemoved(entity: Entity) {
                val body = entity[RigidBody.mapper]!!
                world.destroyBody(body.body)
            }
        })
    }

    override fun update(deltaTime: Float) {
        measureTime {
            world.step(min(1f / 30f, deltaTime), 6, 2)
            entities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val body = it[RigidBody.mapper]!!.body

                transform.x = body.position.x
                transform.y = body.position.y
                transform.rotationDeg = body.angle * MathUtils.radiansToDegrees
            }
        }.let {
            PerformanceMetrics.box2D = it
        }
    }
}
