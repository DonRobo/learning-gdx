package at.robert.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import ktx.app.KtxGame

class TestGame : KtxGame<AshleyGameScreen>() {

    override fun create() {
        addScreen(AshleyGameScreen())
        setScreen<AshleyGameScreen>()
    }

    override fun render() {
        super.render()

        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) {
            dispose()
            create()
        }
    }

    override fun dispose() {
        super.dispose()
        removeScreen<AshleyGameScreen>()
    }
}
