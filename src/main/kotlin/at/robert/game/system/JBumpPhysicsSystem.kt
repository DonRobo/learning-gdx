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
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
class JBumpPhysicsSystem : EntitySystem(6) {

    val jbumpWorld = World<Entity>(6f).apply {
        this.isTileMode = false
    }

    private lateinit var jbumpEntities: ImmutableArray<Entity>
    private lateinit var pushableEntities: ImmutableArray<Entity>

    private val entityListener = object : EntityListener {
        override fun entityAdded(entity: Entity) {
            val transform = entity[TransformComponent.mapper]!!
            val colliding = entity[CollidingComponent.mapper]!!
            val r = colliding.rect

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
        PerformanceMetrics.jbumpMoves++
        val moved = jbumpWorld.move(
            item,
            alreadyMovedTransform.x + collisionRect.x,
            alreadyMovedTransform.y + collisionRect.y
        ) { collidingItem, other ->
            @Suppress("UNCHECKED_CAST")
            collidingItem as Item<Entity>
            @Suppress("UNCHECKED_CAST")
            other as Item<Entity>

            val otherPushable = other[Pushable.mapper]

            if (otherPushable != null) {
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
            PerformanceMetrics.pushes = 0
            PerformanceMetrics.jbumpMoves = 0
            jbumpEntities.forEach {
                val collidingComponent = it[CollidingComponent.mapper]!!
                val transform = it[TransformComponent.mapper]!!
                if (transform.x == collidingComponent.lastPositionX && transform.y == collidingComponent.lastPositionY) {
                    it[Pushable.mapper]?.awake = false
                    return@forEach
                }
                it[Pushable.mapper]?.awake = true
                val item = collidingComponent.item ?: return@forEach
                val r = collidingComponent.rect

                moveObject(item, transform, r, collidingComponent)
            }
            pushableEntities.forEach {
                val transform = it[TransformComponent.mapper]!!
                val pushable = it[Pushable.mapper]!!
                if (!pushable.awake) return@forEach
                val collidingComponent = it[CollidingComponent.mapper]!!
                PerformanceMetrics.jbumpMoves++
                val response = jbumpWorld.check(
                    collidingComponent.item,
                    transform.x + collidingComponent.rect.x,
                    transform.y + collidingComponent.rect.y,
                ) { collidingItem, other ->
                    @Suppress("UNCHECKED_CAST")
                    collidingItem as Item<Entity>
                    @Suppress("UNCHECKED_CAST")
                    other as Item<Entity>

                    return@check if (collidingItem != collidingComponent.item || other == collidingComponent.item) {
                        null
                    } else if (collidingItem[Pushable.mapper] != null && other[Pushable.mapper] != null)
                        Response.cross
                    else
                        null
                }
                var pushedX = 0f
                var pushedY = 0f

                val itemArea = collidingComponent.rect.w * collidingComponent.rect.h
                val minX = collidingComponent.rect.w / -2f
                val maxX = collidingComponent.rect.w / 2f
                val minY = collidingComponent.rect.h / -2f
                val maxY = collidingComponent.rect.h / 2f
                response.projectedCollisions.iterator().forEach { c ->
                    val itemRect = c.itemRect!!
                    val otherRect = c.otherRect!!

                    @Suppress("UNCHECKED_CAST")
                    (c.other as Item<Entity>)[Pushable.mapper]!!.awake = true

                    val intersectionFromX = max(itemRect.x, otherRect.x)
                    val intersectionToX = min(itemRect.x + itemRect.w, otherRect.x + otherRect.w)
                    val intersectionWidth = intersectionToX - intersectionFromX

                    val intersectionFromY = max(itemRect.y, otherRect.y)
                    val intersectionToY = min(itemRect.y + itemRect.h, otherRect.y + otherRect.h)
                    val intersectionHeight = intersectionToY - intersectionFromY

                    PerformanceMetrics.pushes = PerformanceMetrics.pushes!! + 1

                    if (intersectionWidth > 0f && intersectionHeight > 0f) {
                        val intersectionArea = intersectionWidth * intersectionHeight
                        val power = max(1f, (intersectionArea / itemArea) * 10f)
                        val intersectionCenterX = intersectionFromX + intersectionWidth / 2f
                        val intersectionCenterY = intersectionFromY + intersectionHeight / 2f
                        val otherCenterX = otherRect.x + otherRect.w / 2f
                        val otherCenterY = otherRect.y + otherRect.h / 2f
                        val pushAngle =
                            if (
                                (intersectionCenterY - otherCenterY).absoluteValue <= 0.0001f &&
                                (intersectionCenterX - otherCenterX).absoluteValue <= 0.0001f
                            ) {
                                MathUtils.random(0f, 2 * MathUtils.PI)
                            } else {
                                MathUtils.atan2(intersectionCenterY - otherCenterY, intersectionCenterX - otherCenterX)
                            }

                        val xPush = MathUtils.cos(pushAngle)
                        val yPush = MathUtils.sin(pushAngle)

                        pushedX += power * xPush
                        pushedY += power * yPush
                    }
                }

                pushedX = MathUtils.clamp(pushedX * deltaTime, minX, maxX)
                pushedY = MathUtils.clamp(pushedY * deltaTime, minY, maxY)

                transform.x += pushedX
                transform.y += pushedY
            }
        }.let {
            PerformanceMetrics.physics = it
            PerformanceMetrics.jbumpCells = jbumpWorld.countCells()
        }
    }
}

private operator fun <T : Component> Item<Entity>.get(mapper: ComponentMapper<T>): T? = this.userData[mapper]
