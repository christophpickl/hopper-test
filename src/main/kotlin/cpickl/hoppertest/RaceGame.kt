package cpickl.hoppertest

object GamePreparer {
    fun prepare(config: RaceConfig): RaceGame {
        config.size.validate()
        return RaceGame(
            grid = Grid(
                size = config.size,
                start = config.start,
                end = config.end,
                obstacles = config.obstacles
            )
        )
    }

    private fun GridSize.validate() {
        require(width in 1..30)
        require(height in 1..30)
        require(isRectangular)
    }
}

class RaceGame(
    private val grid: Grid
) {
    fun play(): RaceResult {
        return RaceResult.NoSolution
    }
}

sealed class RaceResult {
    class FoundSolution(val numberHopsNeeded: Int) : RaceResult()
    object NoSolution : RaceResult()
}
