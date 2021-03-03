package cpickl.hoppertest

val RaceConfig.Companion.any
    get() = RaceConfig(
        size = GridSize.any,
        startEnd = Position(3, 4) to Position(5, 6),
        obstacles = listOf(Obstacle.any),
    )

val Obstacle.Companion.any get() = Obstacle(Position(7, 8), Position(9, 10))

val Position.Companion.any get() = Position(0, 0)

val Grid.Companion.any
    get() = Grid(
        size = GridSize.any,
        start = Position.any,
        end = Position.any,
        obstacles = emptyList(),
    )

val GridSize.Companion.any get() = GridSize(42, 42)
