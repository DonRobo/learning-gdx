package at.robert.game.entity

import at.robert.game.component.*

fun PlayerEntity(
    x: Float = 0f,
    y: Float = 0f,
): EntityPrefab = EntityPrefab {
    withTransformComponent(
        x = x,
        y = y,
        width = 1f * 0.3f,
        height = (28f / 16f) * 0.3f,
    )
    withPlayerControlled()
    withSimpleRigidBody()
    withDungeonTileSprite(
        "wizzard_m_run_anim",
        animationFrames = 4,
        animationSpeed = 2f,
    )
    withDontDespawn()
}
