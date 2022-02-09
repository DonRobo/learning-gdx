package at.robert.game.render.animation

import at.robert.game.render.RenderEngine
import at.robert.game.render.sprite.AnimatedSpriteProvider
import at.robert.game.render.sprite.SpriteModifier
import at.robert.game.render.sprite.SpriteProvider
import com.badlogic.gdx.graphics.g2d.TextureRegion

class CharacterAnimator(
    private val idle: SpriteProvider,
    private val walkNorth: SpriteProvider,
    private val walkEast: SpriteProvider,
    private val walkSouth: SpriteProvider,
    private val walkWest: SpriteProvider,
) : AnimatedSpriteProvider, SpriteModifier {
    private var lookLeft = false
    override val flipX: Boolean get() = lookLeft
    override val flipY: Boolean get() = false

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

    fun walkNorth() {
        setAnimation(walkNorth)
    }

    fun walkEast() {
        setAnimation(walkEast)
        lookLeft = false
    }

    fun walkSouth() {
        setAnimation(walkSouth)
    }

    fun walkWest() {
        setAnimation(walkWest)
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
