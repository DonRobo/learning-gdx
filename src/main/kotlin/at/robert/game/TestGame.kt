package at.robert.game

import ktx.app.KtxGame

class TestGame : KtxGame<AshleyGameScreen>() {

    override fun create() {
        addScreen(AshleyGameScreen())
        setScreen<AshleyGameScreen>()
    }
}
