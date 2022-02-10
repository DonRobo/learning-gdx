package at.robert.game.entity

import at.robert.game.component.*
import at.robert.game.render.animation.CharacterAnimator
import at.robert.game.render.sprite.AdvancedSpriteRenderer
import at.robert.game.render.sprite.DungeonSpriteProvider

fun OrcEnemy(
    x: Float = 0f,
    y: Float = 0f,
) = EntityPrefab {
    val walkAnimation = DungeonSpriteProvider("orc_warrior_run_anim", 2f)
    val animator = CharacterAnimator(
        walkUp = walkAnimation,
        walkRight = walkAnimation,
        walkDown = walkAnimation,
        walkLeft = walkAnimation,
        idle = DungeonSpriteProvider("orc_warrior_idle_anim", .8f),
    )
    withAnimated(animator)
    withTransformComponent(
        x = x,
        y = y,
        width = 1f,
        height = (20f / 16f),
    )
    withColliding(
        -0.4f,
        0.4f,
        -.7f,
        0f
    )
    withDontDespawn()
    withRenderable(AdvancedSpriteRenderer(animator, animator))
    withEnemyComponent(3f)
    withPushable()
}
