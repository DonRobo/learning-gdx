package at.robert.game.system

import at.robert.game.component.CollidingComponent
import at.robert.game.component.Pushable
import at.robert.game.component.TransformComponent
import at.robert.game.iterator
import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.MathUtils
import com.dongbat.jbump.Item
import com.dongbat.jbump.Rect
import com.dongbat.jbump.Response
import com.dongbat.jbump.World
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class PhysicsSystem : EntitySystem(6) {

    val jbumpWorld = World<Entity>(6f).apply {
        this.isTileMode = false
    }

    private lateinit var jbumpEntities: ImmutableArray<Entity>
    private lateinit var pushableEntities: ImmutableArray<Entity>

    private val entityListener = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            val transform = entity[TransformComponent.mapper]!!
            val colliding = entity[CollidingComponent.mapper]!!
            val r = colliding.rect!!

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
        pushableEntities =
            engine.getEntitiesFor(allOf(Pushable::class, TransformComponent::class, CollidingComponent::class).get())
        engine.addEntityListener(
            allOf(CollidingComponent::class, TransformComponent::class).get(), entityListener
        )
    }

    override fun removedFromEngine(engine: Engine) {
        engine.removeEntityListener(entityListener)
        jbumpWorld.reset()
    }

    private fun moveObject(
        item: Item<Entity>,
        alreadyMovedTransform: TransformComponent,
        collisionRect: Rect,
        collidingComponent: CollidingComponent
    ) {
        val moved = jbumpWorld.move(
            item,
            alreadyMovedTransform.x + collisionRect.x,
            alreadyMovedTransform.y + collisionRect.y
        ) { collidingItem, other ->
            @Suppress("UNCHECKED_CAST")
            collidingItem as Item<Entity>
            @Suppress("UNCHECKED_CAST")
            other as Item<Entity>

            val itemPushable = collidingItem[Pushable.mapper]
            val otherPushable = other[Pushable.mapper]

            if (itemPushable != null && otherPushable != null) {
                null
            } else {
                Response.slide
            }
        }
        alreadyMovedTransform.x = moved.goalX - collisionRect.x
        alreadyMovedTransform.y = moved.goalY - collisionRect.y

        collidingComponent.lastPositionX = alreadyMovedTransform.x
        collidingComponent.lastPositionY = alreadyMovedTransform.y
    }

    override fun update(deltaTime: Float) {
        measureTime {
            jbumpEntities.forEach {
                val collidingComponent = it[CollidingComponent.mapper]!!
                val transform = it[TransformComponent.mapper]!!
                if (transform.x == collidingComponent.lastPositionX && transform.y == collidingComponent.lastPositionY) {
                    return@forEach
                }
                val item = collidingComponent.item ?: return@forEach
                val r = collidingComponent.rect

                moveObject(item, transform, r, collidingComponent)
            }
            pushableEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val pushable = it[Pushable.mapper]!!
                val collidingComponent = it[CollidingComponent.mapper]!!

                val response = jbumpWorld.check(
                    collidingComponent.item,
                    transform.x,
                    collidingComponent.rect.x
                ) { collidingItem, other ->
                    @Suppress("UNCHECKED_CAST")
                    collidingItem as Item<Entity>
                    @Suppress("UNCHECKED_CAST")
                    other as Item<Entity>

                    return@check if (collidingItem != collidingComponent.item || other == collidingComponent.item) {
                        null
                    } else if (collidingItem[Pushable.mapper] != null && other[Pushable.mapper] != null)
                        Response.touch
                    else
                        null
                }
                var pushedX = 0f
                var pushedY = 0f

                val itemArea = collidingComponent.rect.w * collidingComponent.rect.h
                response.projectedCollisions.iterator().forEach { c ->
                    val itemRect = c.itemRect!!
                    val otherRect = c.otherRect!!

                    val intersectionFromX = max(itemRect.x, otherRect.x)
                    val intersectionToX = min(itemRect.x + itemRect.w, otherRect.x + otherRect.w)
                    val intersectionWidth = intersectionToX - intersectionFromX

                    val intersectionFromY = max(itemRect.y, otherRect.y)
                    val intersectionToY = min(itemRect.y + itemRect.h, otherRect.y + otherRect.h)
                    val intersectionHeight = intersectionToY - intersectionFromY

                    if (intersectionWidth > 0f && intersectionHeight > 0f) {
                        val intersectionArea = intersectionWidth * intersectionHeight
                        val power = (itemArea / intersectionArea) * 0.1f
                        val intersectionCenterX = intersectionFromX + intersectionWidth / 2f
                        val intersectionCenterY = intersectionFromY + intersectionHeight / 2f
                        val otherCenterX = otherRect.x + otherRect.w / 2f
                        val otherCenterY = otherRect.y + otherRect.h / 2f
                        val pushAngle =
                            MathUtils.atan2(intersectionCenterY - otherCenterY, intersectionCenterX - otherCenterX)
                        pushedX += power * MathUtils.cos(pushAngle)
                        pushedY += power * MathUtils.sin(pushAngle)
                    }
                }

                transform.x += pushedX * deltaTime
                transform.y += pushedY * deltaTime

                moveObject(collidingComponent.item!!, transform, collidingComponent.rect, collidingComponent)
            }
        }.let {
            PerformanceMetrics.physics = it
            PerformanceMetrics.jbumpCells = jbumpWorld.countCells()
        }
    }
}

private operator fun <T : Component> Item<Entity>.get(mapper: ComponentMapper<T>): T? = this.userData[mapper]
