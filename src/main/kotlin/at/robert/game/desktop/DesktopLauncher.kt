package at.robert.game.desktop

import at.robert.game.RacingGame
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

fun main() {
    val config = LwjglApplicationConfiguration()
    config.title = "Robert's Game"
    config.foregroundFPS = 0
    LwjglApplication(RacingGame(), config)
}
