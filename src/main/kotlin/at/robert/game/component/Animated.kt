package at.robert.game.component

import at.robert.game.render.animation.CharacterAnimator
import com.badlogic.ashley.core.Component
import ktx.ashley.EngineEntity
import ktx.ashley.Mapper
import ktx.ashley.with

class Animated : Component {
    lateinit var animator: CharacterAnimator

    companion object : Mapper<Animated>()
}

fun EngineEntity.withAnimated(animator: CharacterAnimator) {
    with<Animated> {
        this.animator = animator
    }
}
