package at.robert.game.system

import at.robert.game.component.*
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.get
import kotlin.math.roundToInt

class PlayerControlSystem : IteratingSystem(
    allOf(
        RigidBody::class,
        PlayerControlled::class,
    ).get()
) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val kinetic = entity[RigidBody.mapper]!!

        var xV = 0f
        var yV = 0f

        val speed = 3f

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xV += speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xV -= speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yV += speed
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yV -= speed
        }

        kinetic.body.setLinearVelocity(xV, yV)

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            val distance = 1f
            val angle = 360f * MathUtils.random()
            val x = distance * MathUtils.cosDeg(angle) + kinetic.body.position.x
            val y = distance * MathUtils.sinDeg(angle) + kinetic.body.position.y

            engine.entity {
                withRenderPlaceholder()
                withTransformComponent(
                    x = x,
                    y = y,
                    width = 0.05f + MathUtils.random() * 0.5f,
                    height = 0.05f + MathUtils.random() * 0.5f,
                )
                withRigidBody(BodyDef.BodyType.DynamicBody, 40f, 0.1f, 1f)
                withMoveTowardsPlayer(3f)
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            for (i in 0 until (5000 * deltaTime).roundToInt()) {
                val angle = 360f * MathUtils.random()
                engine.entity {
                    withRenderPlaceholder()
                    withTransformComponent(
                        x = kinetic.body.position.x,
                        y = kinetic.body.position.y,
                        width = 0.05f,
                        height = 0.05f,
                        rotationDeg = angle
                    )
                    withMovingComponent(1f, angle)
//                    withRigidBody(BodyDef.BodyType.KinematicBody, 40f, 0.1f, 1f).apply {
//                        initialVelocity = vec2(5f * MathUtils.cosDeg(angle), 5f * MathUtils.sinDeg(angle))
//                    }
                }
            }
        }
    }
}
