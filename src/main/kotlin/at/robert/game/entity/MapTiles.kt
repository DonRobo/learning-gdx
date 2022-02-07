package at.robert.game.entity

import at.robert.game.component.withColliding
import at.robert.game.component.withRenderable
import at.robert.game.component.withTransformComponent
import at.robert.game.render.sprite.DungeonSpriteProvider
import at.robert.game.render.sprite.SimpleSpriteRenderer

fun FloorTile(x: Int = 0, y: Int = 0) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 1f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("floor_1")), -2)
}

fun ColumnTile(x: Int = 0, y: Int = 0) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 3f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("column_full")))
    withColliding(
        -.5f,
        .5f,
        -1f,
        -0.3f
    )
}
