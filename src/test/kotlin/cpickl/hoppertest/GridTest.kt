package cpickl.hoppertest

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test
class GridTest {

    fun `When create grid Then start and end is set`() {
        val start = Position(0, 0)
        val end = Position(1, 1)

        val grid = Grid(
            size = GridSize(2, 2),
            start = start,
            end = end,
            obstacles = emptyList()
        )

        assertThat(grid.stateAt(start)).isEqualTo(SquareState.Start)
        assertThat(grid.stateAt(end)).isEqualTo(SquareState.End)
    }

    fun `When pretty string without obstacles Then properly formated`() {
        val grid = Grid(
            size = GridSize(2, 2),
            start = Position(0, 0),
            end = Position(1, 1),
            obstacles = emptyList()
        )

        assertThat(grid.toPrettyString()).isEqualTo("S _\n_ E")
    }

    fun `When create grid with obstacles Then all obstacles set`() {
        val grid = Grid(
            size = GridSize(5, 3),
            start = Position(0, 0),
            end = Position(4, 2),
            obstacles = listOf(Obstacle(Position(1, 1), Position(1, 2)))
        )

        assertThat(grid.toPrettyString()).isEqualTo("S _ _ _ _\n_ o _ _ _\n_ o _ _ E")
    }

    fun `When obstacle above start Then start wins`() {
        val grid = Grid(
            size = GridSize(2, 2),
            start = Position(0, 0),
            end = Position(1, 1),
            obstacles = listOf(Obstacle(Position(0, 0), Position(0, 0)))
        )

        assertThat(grid.stateAt(Position(0, 0))).isEqualTo(SquareState.Start)
    }
}