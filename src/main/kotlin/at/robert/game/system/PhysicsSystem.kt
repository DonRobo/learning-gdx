package at.robert.game.system

import at.robert.game.component.CollidingComponent
import at.robert.game.component.TransformComponent
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.utils.ImmutableArray
import com.dongbat.jbump.CollisionFilter
import com.dongbat.jbump.Item
import com.dongbat.jbump.Rect
import com.dongbat.jbump.World
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class PhysicsSystem : EntitySystem(6) {

    val jbumpWorld = World<Entity>(16f).apply {
        this.isTileMode = false
    }

    private lateinit var jbumpEntities: ImmutableArray<Entity>

    private fun CollidingComponent.initRect(transform: TransformComponent): Rect {
        if (rect == null) {
            rect = Rect(
                transform.x - transform.width / 2f,
                transform.y - transform.height / 2f,
                transform.width,
                transform.height
            )
        }
        return rect!!
    }

    private val entityListener = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            val transform = entity[TransformComponent.mapper]!!
            val colliding = entity[CollidingComponent.mapper]!!
            val r = colliding.initRect(transform)

            val item = Item(entity)
            jbumpWorld.add(
                item,
                transform.x + r.x,
                transform.y + r.y,
                r.w,
                r.h
            )

            val collidingComponent = entity[CollidingComponent.mapper]!!
            collidingComponent.item = item
        }

        override fun entityRemoved(entity: Entity) {
            val collidingComponent = entity[CollidingComponent.mapper]!!
            jbumpWorld.remove(collidingComponent.item)
        }
    }

    override fun addedToEngine(engine: Engine) {
        jbumpEntities = engine.getEntitiesFor(allOf(CollidingComponent::class, TransformComponent::class).get())
        engine.addEntityListener(
            allOf(CollidingComponent::class, TransformComponent::class).get(), entityListener
        )
    }

    override fun removedFromEngine(engine: Engine) {
        engine.removeEntityListener(entityListener)
        jbumpWorld.reset()
    }

    override fun update(deltaTime: Float) {
        measureTime {
            jbumpEntities.forEach {
                val collidingComponent = it[CollidingComponent.mapper]!!
                if (!collidingComponent.moved) return@forEach
                val item = collidingComponent.item
                val transform = it[TransformComponent.mapper]!!
                val r = collidingComponent.initRect(transform)

                val moved = jbumpWorld.move(
                    item,
                    transform.x + r.x,
                    transform.y + r.y,
                    CollisionFilter.defaultFilter
                )
                transform.x = moved.goalX - r.x
                transform.y = moved.goalY - r.y
                collidingComponent.moved = false
            }
        }.let {
            PerformanceMetrics.physics = it
            PerformanceMetrics.jbumpCells = jbumpWorld.countCells()
        }
    }
}
