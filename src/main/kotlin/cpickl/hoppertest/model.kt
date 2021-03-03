package cpickl.hoppertest

data class RaceConfig(
    val size: GridSize,
    val start: Position,
    val end: Position,
    val obstacles: List<Obstacle>,
) {
    companion object;

    constructor(size: GridSize, startEnd: Pair<Position, Position>, obstacles: List<Obstacle>) :
            this(size, startEnd.first, startEnd.second, obstacles)
}

data class Square(
    val position: Position,
    var state: SquareState = SquareState.Empty
)

enum class SquareState {
    Empty,
    Occupied,
    /** Position of Hopper. */
    Start,
    End
}

data class GridSize(
    val width: Int,
    val height: Int
) {
    companion object;

    val isRectangular = (width == height)
}

data class Position(
    val x: Int,
    val y: Int,
) {
    companion object
}

data class Obstacle(
    val from: Position,
    val to: Position,
) {
    companion object;
}