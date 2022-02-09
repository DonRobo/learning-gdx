package at.robert.game.render.sprite

interface SpriteModifier {
    val flipX: Boolean
    val flipY: Boolean
    val rotatedBy90: Int
}

data class SimpleSpriteModifier(
    override val flipX: Boolean = false,
    override val flipY: Boolean = false,
    override val rotatedBy90: Int = 0
) : SpriteModifier
