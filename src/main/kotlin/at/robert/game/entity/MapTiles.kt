package at.robert.game.entity

import at.robert.game.component.withColliding
import at.robert.game.component.withDungeonTileSprite
import at.robert.game.component.withRenderable
import at.robert.game.component.withTransformComponent

fun FloorTile(x: Int = 0, y: Int = 0) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 1f)
    withDungeonTileSprite(
        "floor_1"
    )
    withRenderable(-2)
}

fun ColumnTile(x: Int = 0, y: Int = 0) = EntityPrefab {
    withTransformComponent(x.toFloat(), y.toFloat(), 1f, 3f)
    withDungeonTileSprite(
        "column_full"
    )
    withRenderable()
    withColliding(
        -.5f,
        .5f,
        -1f,
        -0.3f
    )
}
