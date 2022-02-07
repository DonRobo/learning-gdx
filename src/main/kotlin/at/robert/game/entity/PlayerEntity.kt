package at.robert.game.entity

import at.robert.game.component.*
import at.robert.game.render.sprite.DungeonSpriteProvider
import at.robert.game.render.sprite.SimpleSpriteRenderer

fun PlayerEntity(
    x: Float = 0f,
    y: Float = 0f,
) = EntityPrefab {
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
        -1f,
        -.4f
    )
    withDontDespawn()
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("wizard_m_run_anim", 2f)))
}
