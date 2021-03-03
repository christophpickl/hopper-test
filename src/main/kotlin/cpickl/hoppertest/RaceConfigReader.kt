package cpickl.hoppertest

object RaceConfigReader {

    fun read(input: String): List<RaceConfig> {
        val lines = LineReader(input)
        val countConfigs = lines.next().toInt()
        return 1.rangeTo(countConfigs).map { readConfig(lines) }
    }

    private fun readConfig(lines: LineReader) = RaceConfig(
        size = readGridSize(lines.next()),
        startEnd = readPositions(lines.next()),
        obstacles = readObstacles(lines)
    )

    private fun readGridSize(line: String): GridSize {
        val (gridWith, gridHeight) = line.toInts()
        return GridSize(gridWith, gridHeight)
    }

    private fun readPositions(line: String): Pair<Position, Position> {
        val (startX, startY, endX, endY) = line.toInts()
        return Position(startX, startY) to Position(endX, endY)
    }

    private fun readObstacles(lines: LineReader): List<Obstacle> {
        val countObstacles = lines.next().toInt()
        return 1.rangeTo(countObstacles).map { readObstacle(lines.next()) }
    }

    private fun readObstacle(line: String): Obstacle {
        val (fromX, fromY, toX, toY) = line.toInts()
        return Obstacle(Position(fromX, fromY), Position(toX, toY))
    }

    private fun String.toInts() = split(" ").map { it.toInt() }

}

private class LineReader(
    input: String
) {
    private val lines: List<String> = input.lines()
    private var position = 0
    fun next() = lines[position++]
}