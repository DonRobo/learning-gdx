package at.robert.game.system

import at.robert.game.component.SimpleRigidBody
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.dongbat.jbump.CollisionFilter
import com.dongbat.jbump.Item
import com.dongbat.jbump.World
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class PhysicsSystem : EntitySystem() {

    val jbumpWorld = World<Entity>(3f).apply {
        this.isTileMode = false
    }

    private lateinit var jbumpEntities: ImmutableArray<Entity>

    private val entityListener = object : EntityListener {
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
    }

    override fun addedToEngine(engine: Engine) {
        jbumpEntities = engine.getEntitiesFor(allOf(SimpleRigidBody::class, TransformComponent::class).get())
        engine.addEntityListener(
            allOf(SimpleRigidBody::class, TransformComponent::class).get(), entityListener
        )
    }

    override fun removedFromEngine(engine: Engine) {
        engine.removeEntityListener(entityListener)
        jbumpWorld.reset()
    }

    override fun update(deltaTime: Float) {
        measureTime {
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
