package at.robert.game

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

data class RenderObject(
    val width: Float,
    val height: Float,
    val texture: TextureRegion
) {
    constructor(width: Float, texture: TextureRegion) : this(
        width,
        width * (texture.regionHeight.toFloat() / texture.regionWidth),
        texture
    )
}

fun SpriteBatch.renderObject(x: Float, y: Float, angle: Float, obj: RenderObject) {
    draw(
        obj.texture,
        x,
        y,
        obj.width / 2,
        obj.height / 2,
        obj.width,
        obj.height,
        1f,
        1f,
        angle
    )
}
