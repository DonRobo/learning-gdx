package at.robert.game.component

import com.badlogic.ashley.core.Component
import ktx.ashley.Mapper

class Rotating(var speed: Float = 0f) : Component {
    companion object : Mapper<Rotating>()
}
