package at.robert.game

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
