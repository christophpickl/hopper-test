package cpickl.hoppertest

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

@Test
class RaceConfigReaderTest {

    @DataProvider
    fun provideMalformedInput() = arrayOf(*listOf("", "X").map { arrayOf(it) }.toTypedArray())

    @Test(dataProvider = "provideMalformedInput")
    fun `When read malformed input Then fail`(malformedInput: String) {
        assertThat {
            RaceConfigReader.read(malformedInput)
        }.isFailure()
    }

    fun `When read single config Then return config size`() {
        assertThat(RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any))).hasSize(1)
    }

    fun `When read two configs Then return config size`() {
        assertThat(RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any, RaceConfig.any))).hasSize(2)
    }

    fun `Given grid size When read Then return it`() {
        val size = GridSize(1, 2)

        val actual = RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any.copy(size = size)))[0]

        assertThat(actual.size).isEqualTo(size)
    }

    fun `Given start position When read Then return it`() {
        val start = Position(1, 2)

        val actual = RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any.copy(start = start)))[0]

        assertThat(actual.start).isEqualTo(start)
    }

    fun `Given end position When read Then return it`() {
        val end = Position(1, 2)

        val actual = RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any.copy(end = end)))[0]

        assertThat(actual.end).isEqualTo(end)
    }

    fun `Given obstacles When read Then return them`() {
        val obstacles = listOf(Obstacle.any)

        val actual = RaceConfigReader.read(RaceConfigWriter.write(RaceConfig.any.copy(obstacles = obstacles)))[0]

        assertThat(actual.obstacles).isEqualTo(obstacles)
    }

    fun `When read sample input Then return complete config`() {
        val config = RaceConfig(
            size = GridSize(5, 6),
            start = Position(4, 0),
            end = Position(3, 4),
            obstacles = listOf(Obstacle(Position(1, 4), Position(2, 3)))
        )

        assertThat(RaceConfigReader.read(RaceConfigWriter.write(config))).isEqualTo(listOf(config))
    }
}
