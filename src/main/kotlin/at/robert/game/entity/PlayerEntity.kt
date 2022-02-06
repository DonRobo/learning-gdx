package at.robert.game.entity

import at.robert.game.component.*

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
    withDungeonTileSprite(
        "wizard_m_run_anim",
        animationFrames = 4,
        animationSpeed = 2f,
    )
    withDontDespawn()
    withRenderable()
}
