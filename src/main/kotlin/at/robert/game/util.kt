package at.robert.game

import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AssetLoader
import com.badlogic.gdx.math.MathUtils

fun secondsFromNanos(nanos: Long): Float {
    return nanos / 1000000000.0f
}

fun PooledEngine(
    entityPoolInitialSize: Int = 10,
    entityPoolMaxSize: Int = 100,
    componentPoolInitialSize: Int = 10,
    componentPoolMaxSize: Int = 100
) = com.badlogic.ashley.core.PooledEngine(
    entityPoolInitialSize,
    entityPoolMaxSize,
    componentPoolInitialSize,
    componentPoolMaxSize
)

fun Float.toRadians() = this * MathUtils.degreesToRadians
fun Float.toDegrees() = this * MathUtils.radiansToDegrees

fun <T> ImmutableArray<T>.isEmpty(): Boolean = this.size() == 0

inline fun <reified T> AssetManager.getOrLoadAsset(filename: String): T? {
    return this.get(filename, T::class.java, false)
}

inline fun <reified T, P : AssetLoaderParameters<T>> AssetManager.setLoader(loader: AssetLoader<T, P>) {
    setLoader(T::class.java, loader)
}
