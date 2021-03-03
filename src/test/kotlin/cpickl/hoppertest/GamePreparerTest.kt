package cpickl.hoppertest

import assertk.assertThat
import assertk.assertions.isFailure
import org.testng.annotations.Test

@Test
class GamePreparerTest {
    fun `When size is not rectangular Then fail`() {
        assertThat {
            GamePreparer.prepare(RaceConfig.any.copy(size = GridSize(1, 2)))
        }.isFailure()
    }
}