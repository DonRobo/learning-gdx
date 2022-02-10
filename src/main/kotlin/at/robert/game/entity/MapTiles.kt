package at.robert.game.entity

import at.robert.game.component.withColliding
import at.robert.game.component.withRenderable
import at.robert.game.component.withTransformComponent
import at.robert.game.render.sprite.AdvancedSpriteRenderer
import at.robert.game.render.sprite.DungeonSpriteProvider
import at.robert.game.render.sprite.SimpleSpriteModifier
import at.robert.game.render.sprite.SimpleSpriteRenderer

fun FloorTile(x: Int = 0, y: Int = 0) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 1f)
    withRenderable(
        AdvancedSpriteRenderer(
            DungeonSpriteProvider("floor_1"),
            SimpleSpriteModifier(
//                flipX = MathUtils.randomBoolean(),
//                flipY = MathUtils.randomBoolean(),
//                rotatedBy90 = MathUtils.random(0, 3)
            )
        ), -2
    )
}

fun ColumnObstacle(x: Float = 0f, y: Float = 0f) = EntityPrefab {
    withTransformComponent(x, y, 1f, 3f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("column_full")))
    withColliding(
        -.5f,
        .5f,
        -1f,
        -0.3f
    )
}

fun WallTop(x: Int, y: Int) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 1f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("wall_mid")))
    withColliding(
        -.5f,
        .5f,
        -.5f,
        .5f
    )
}

fun WallEndSouth(x: Int, y: Int) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 1f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("wall_corner_right")))
    withColliding(
        -.5f,
        .5f,
        -.5f,
        .5f
    )
}
