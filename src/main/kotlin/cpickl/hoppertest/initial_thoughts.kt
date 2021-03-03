package cpickl.hoppertest

// go from start to finish as quickly as possible (least number of hops)
// given:
// - rectangular grid
// - start point S
// - finish point F
// hopper initial speed 0x0


data class Hopper(
    val speedX: Speed,
    val speedY: Speed,
) {
    fun changeSpeed(xChange: SpeedChange, yChange: SpeedChange) {
        // require (speed within bounds {fastest/slowest})
    }
}

enum class SpeedChange {
    Up, Down, Stay
}
enum class Speed(
    val speedNumber: Int
) {
    Slowest(-3),
    Slower(-2),
    Slow(-1),
    Neutral(0),
    Fast(1),
    Faster(2),
    Fastest(3)
}
