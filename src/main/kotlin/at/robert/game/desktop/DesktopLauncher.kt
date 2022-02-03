package at.robert.game.desktop

import at.robert.game.TestGame
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    Lwjgl3Application(TestGame(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Robert's Game")
        setWindowedMode(800, 600)
        useVsync(false)
        setForegroundFPS(240)
        setIdleFPS(30)
    })
}
