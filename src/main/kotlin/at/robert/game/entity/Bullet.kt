package at.robert.game.entity

import at.robert.game.component.*
import at.robert.game.render.sprite.DungeonSpriteProvider
import at.robert.game.render.sprite.SimpleSpriteRenderer

fun Bullet(x: Float, y: Float, angle: Float) = EntityPrefab {
    withTransformComponent(x, y, 1f, 1f)
    withMovingComponent(15f, angle)
    withDespawnable()
    withCircularCollider(0.3f)
    withRenderable(SimpleSpriteRenderer(DungeonSpriteProvider("skull")))
    withHitDetector()
}
