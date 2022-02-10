package at.robert.game.entity

import at.robert.game.component.withColliding
import at.robert.game.component.withPushable
import at.robert.game.component.withRenderable
import at.robert.game.component.withTransformComponent
import at.robert.game.render.sprite.DungeonSpriteProvider
import at.robert.game.render.sprite.SimpleSpriteRenderer

fun Crate(
    x: Float = 0f,
    y: Float = 0f,
) = EntityPrefab {
    withTransformComponent(
        x = x,
        y = y,
        width = 1f,
        height = 22f / 16f,
    )
    withColliding(
        negativeXOffset = -0.5f,
        positiveXOffset = 0.5f,
        negativeYOffset = -0.5f,
        positiveYOffset = 0.3f,
    )
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("crate")))
    withPushable()
}
