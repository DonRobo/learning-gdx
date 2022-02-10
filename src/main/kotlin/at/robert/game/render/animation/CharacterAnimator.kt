package at.robert.game.render.animation

import at.robert.game.render.RenderEngine
import at.robert.game.render.sprite.AnimatedSpriteProvider
import at.robert.game.render.sprite.SpriteModifier
import at.robert.game.render.sprite.SpriteProvider
import com.badlogic.gdx.graphics.g2d.TextureRegion

class CharacterAnimator(
    private val idle: SpriteProvider,
    private val walkUp: SpriteProvider,
    private val walkRight: SpriteProvider,
    private val walkDown: SpriteProvider,
    private val walkLeft: SpriteProvider,
) : AnimatedSpriteProvider, SpriteModifier {
    private var lookLeft = false
    override val flipX: Boolean get() = lookLeft
    override val flipY: Boolean get() = false
    override val rotatedBy90: Int get() = 0

    private var currentAnimation: SpriteProvider = idle

    private fun setAnimation(newAnimation: SpriteProvider) {
        if (currentAnimation != newAnimation) {
            val previousProgress = (currentAnimation as? AnimatedSpriteProvider)?.animationProgress
            currentAnimation = newAnimation
            if (previousProgress != null) {
                (currentAnimation as? AnimatedSpriteProvider)?.animationProgress = previousProgress
            } else {
                (currentAnimation as? AnimatedSpriteProvider)?.resetAnimation()
            }
        }
    }

    fun idle() {
        setAnimation(idle)
    }

    fun walkUp() {
        setAnimation(walkUp)
    }

    fun walkRight() {
        setAnimation(walkRight)
        lookLeft = false
    }

    fun walkDown() {
        setAnimation(walkDown)
    }

    fun walkLeft() {
        setAnimation(walkLeft)
        lookLeft = true
    }

    override var animationProgress: Float
        get() = (currentAnimation as? AnimatedSpriteProvider)?.animationProgress ?: 0f
        set(value) {
            (currentAnimation as? AnimatedSpriteProvider)?.animationProgress = value
        }

    override fun resetAnimation() {
        setAnimation(idle)
        animationProgress = 0f
    }

    override fun getSprite(renderEngine: RenderEngine): TextureRegion {
        return currentAnimation.getSprite(renderEngine)
    }

    override fun getSprite(renderEngine: RenderEngine, frame: Int): TextureRegion {
        return (currentAnimation as? AnimatedSpriteProvider)?.getSprite(renderEngine, frame) ?: getSprite(renderEngine)
    }

    override val frameCount: Int
        get() = (currentAnimation as? AnimatedSpriteProvider)?.frameCount ?: 1
}
