package cpickl.hoppertest

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.testng.annotations.Test

@Test
class DemoTest {
    fun `foo bar`() {
        assertThat(2 + 2).isEqualTo(4)
    }
}
