package cpickl.hoppertest

data class Grid(
    private val size: GridSize,
    private val start: Position,
    private val end: Position,
    private val obstacles: List<Obstacle>,
) {

    companion object;

    private val squares: List<List<Square>> = 1.rangeTo(size.height).map { y ->
        1.rangeTo(size.width).map { x -> Square(Position(x - 1, y - 1)) }
    }

    init {
        changeAt(start, SquareState.Start)
        changeAt(end, SquareState.End)

        // TODO refactor and outsource to preparer
        obstacles.forEach { obstacle ->
            squares.forEach { row ->
                row.forEach { square ->
                    if (square.position.isWithin(obstacle) && square.position != start) {
                        square.state = SquareState.Occupied
                    }
                }
            }
        }
    }

    private fun Position.isWithin(obstacle: Obstacle) =
        this.x in obstacle.from.x..obstacle.to.x &&
                this.y in obstacle.from.y..obstacle.to.y

    private fun changeAt(position: Position, state: SquareState) {
        squares[position.y][position.x].state = state
    }

    fun stateAt(position: Position): SquareState = squares[position.y][position.x].state

    fun toPrettyString() = squares.joinToString("\n") { rows ->
        rows.joinToString(" ") { it.state.prettyString }
    }

    private val SquareState.prettyString
        get() = when (this) {
            SquareState.Empty -> "_"
            SquareState.Occupied -> "o"
            SquareState.Start -> "S"
            SquareState.End -> "E"
        }
}

