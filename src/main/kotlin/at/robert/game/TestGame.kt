package at.robert.game

import ktx.app.KtxGame

class TestGame : KtxGame<GameScreen>() {

    override fun create() {
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
