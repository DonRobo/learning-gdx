package at.robert.game.entity

import at.robert.game.component.*
import at.robert.game.render.animation.CharacterAnimator
import at.robert.game.render.sprite.AdvancedSpriteRenderer
import at.robert.game.render.sprite.DungeonSpriteProvider

fun PlayerEntity(
    x: Float = 0f,
    y: Float = 0f,
) = EntityPrefab {
    val walkAnimation = DungeonSpriteProvider("wizard_m_run_anim", 2f)
    val animator = CharacterAnimator(
        walkUp = walkAnimation,
        walkRight = walkAnimation,
        walkDown = walkAnimation,
        walkLeft = walkAnimation,
        idle = DungeonSpriteProvider("wizard_m_idle_anim", .8f),
    )
    withAnimated(animator)
    withTransformComponent(
        x = x,
        y = y,
        width = 1f,
        height = (28f / 16f),
    )
    withPlayer()
    withColliding(
        -0.4f,
        0.4f,
        -.9f,
        -.4f
    )
    withRenderable(AdvancedSpriteRenderer(animator, animator))
    withPushable(100f)
}
