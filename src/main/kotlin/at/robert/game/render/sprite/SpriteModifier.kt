package at.robert.game.render.sprite

interface SpriteModifier {
    val flipX: Boolean
    val flipY: Boolean
}

data class SimpleSpriteModifier(
    override val flipX: Boolean = false,
    override val flipY: Boolean = false,
) : SpriteModifier
