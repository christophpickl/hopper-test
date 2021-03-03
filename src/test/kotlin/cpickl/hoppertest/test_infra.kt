package cpickl.hoppertest

import org.testng.SkipException

fun skip() {
    throw SkipException("skipped")
}
