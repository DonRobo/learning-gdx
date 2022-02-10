package at.robert.game.util

import at.robert.game.entity.*
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.GridPoint2
import kotlin.math.max
import kotlin.math.min

private sealed interface LevelEntity {
    val x: Int
    val y: Int
}

private data class Column(override val x: Int, override val y: Int) : LevelEntity
private data class Player(override val x: Int, override val y: Int) : LevelEntity

private sealed interface LevelTile
private object Floor : LevelTile
private object Wall : LevelTile

private class Level {
    private val tiles = mutableMapOf<GridPoint2, LevelTile>()
    private val entities = mutableListOf<LevelEntity>()

    fun setTile(x: Int, y: Int, c: Char) {
        when (c) {
            '#' -> tiles[GridPoint2(x, y)] = Wall
            '.' -> tiles[GridPoint2(x, y)] = Floor
            'C' -> {
                entities.add(Column(x, y))
                tiles[GridPoint2(x, y)] = Floor
            }
            'P' -> {
                entities.add(Player(x, y))
                tiles[GridPoint2(x, y)] = Floor
            }
            else -> {
                println("ERROR: Missing support for '$c' in level loader")
            }
        }
    }

    fun spawnLevel(engine: Engine) {
        val minX = min(tiles.keys.minOf { it.x }, entities.minOf { it.x })
        val minY = min(tiles.keys.minOf { it.y }, entities.minOf { it.y })
        val maxX = max(tiles.keys.maxOf { it.x }, entities.maxOf { it.x })
        val maxY = max(tiles.keys.maxOf { it.y }, entities.maxOf { it.y })

        val centerX = (minX + maxX) / 2
        val centerY = (minY + maxY) / 2

        for ((p, t) in tiles) {
            val useX = p.x - centerX
            val useY = p.y * -1 + centerY
            when (t) {
                is Floor -> engine.addEntity(FloorTile(p.x - centerX, p.y * -1 + centerY))
                Wall -> {
                    val north = tiles[GridPoint2(p.x, p.y - 1)]
                    val south = tiles[GridPoint2(p.x, p.y + 1)]
                    val east = tiles[GridPoint2(p.x + 1, p.y)]
                    val west = tiles[GridPoint2(p.x - 1, p.y)]

                    when {
                        south is Floor && east is Wall && west is Wall -> {
                            engine.addEntity(WallTop(useX, useY))
                        }
                        south is Floor && east is Floor && west is Floor && north is Wall -> {
                            engine.addEntity(WallEndSouth(useX, useY))
                        }
                    }
                }
            }
        }
        for (e in entities) {
            val useX = e.x.toFloat() - centerX
            val useY = e.y.toFloat() * -1 + centerY
            when (e) {
                is Column -> engine.addEntity(ColumnObstacle(useX, useY))
                is Player -> engine.addEntity(PlayerEntity(useX, useY))
            }
        }
    }
}

fun Engine.loadLevel(levelFile: String) {
    val file = Gdx.files.internal(levelFile)
    val lines = file.reader().readLines()
    val level = Level()

    for (y in lines.indices) {
        for (x in 0 until lines[y].length) {
            level.setTile(x, y, lines[y][x])
        }
    }

    level.spawnLevel(this)
}
