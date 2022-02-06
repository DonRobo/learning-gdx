package at.robert.game.system

import at.robert.game.component.RigidBody
import at.robert.game.component.SimpleRigidBody
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.dongbat.jbump.CollisionFilter
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.createWorld
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class PhysicsSystem : EntitySystem() {

    val box2DWorld = createWorld(Vector2(0f, 0f))
    val jbumpWorld = World<Entity>(3f).apply {
        this.isTileMode = false
    }

    private lateinit var box2DEntities: ImmutableArray<Entity>
    private lateinit var jbumpEntities: ImmutableArray<Entity>

    private var alreadyRemoved = false

    override fun addedToEngine(engine: Engine) {
        require(!alreadyRemoved) { "PhysicsSystem already removed from engine" }
        box2DEntities = engine.getEntitiesFor(allOf(RigidBody::class, TransformComponent::class).get())
        engine.addEntityListener(
            allOf(RigidBody::class, TransformComponent::class).get(),
            object : EntityListener {
                override fun entityAdded(entity: Entity) {
                    val transform = entity[TransformComponent.mapper]!!
                    val rigidBody = entity[RigidBody.mapper]!!
                    rigidBody.body = box2DWorld.body {
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
                    box2DWorld.destroyBody(body.body)
                }
            })
        jbumpEntities = engine.getEntitiesFor(allOf(SimpleRigidBody::class, TransformComponent::class).get())
        engine.addEntityListener(
            allOf(SimpleRigidBody::class, TransformComponent::class).get(),
            object : EntityListener {
                override fun entityAdded(entity: Entity) {
                    val transform = entity[TransformComponent.mapper]!!
                    val item = Item(entity)
                    jbumpWorld.add(
                        item,
                        transform.x - transform.width / 2f,
                        transform.y - transform.height / 2f,
                        transform.width,
                        transform.height
                    )

                    val simpleRigidBody = entity[SimpleRigidBody.mapper]!!
                    simpleRigidBody.item = item
                }

                override fun entityRemoved(entity: Entity) {
                    val simpleRigidBody = entity[SimpleRigidBody.mapper]!!
                    jbumpWorld.remove(simpleRigidBody.item)
                }
            })
    }

    override fun removedFromEngine(engine: Engine) {
        jbumpWorld.reset()
        alreadyRemoved = true
    }

    override fun update(deltaTime: Float) {
        measureTime {
            box2DWorld.step(min(1f / 30f, deltaTime), 6, 2)
            box2DEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val body = it[RigidBody.mapper]!!.body

                transform.x = body.position.x
                transform.y = body.position.y
                transform.rotationDeg = body.angle * MathUtils.radiansToDegrees
            }
            jbumpEntities.forEach {
                val item = it[SimpleRigidBody.mapper]!!.item
                val transform = it[TransformComponent.mapper]!!

                val moved = jbumpWorld.move(
                    item,
                    transform.x - transform.width / 2f,
                    transform.y - transform.height / 2f,
                    CollisionFilter.defaultFilter
                )
                transform.x = moved.goalX + transform.width / 2f
                transform.y = moved.goalY + transform.height / 2f
            }
        }.let {
            PerformanceMetrics.physics = it
            PerformanceMetrics.jbumpCells = jbumpWorld.countCells()
        }
    }
}
