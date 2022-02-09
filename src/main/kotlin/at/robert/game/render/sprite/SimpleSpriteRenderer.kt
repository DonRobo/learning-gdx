package at.robert.game.render.sprite

import at.robert.game.render.Renderer

private val simpleSpriteModifier = SimpleSpriteModifier()

fun SimpleSpriteRenderer(
    spriteProvider: SpriteProvider,
): Renderer {
    return AdvancedSpriteRenderer(spriteProvider, simpleSpriteModifier)
}
