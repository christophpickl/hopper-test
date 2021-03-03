package cpickl.hoppertest

object HopperTestApp {
    @JvmStatic
    fun main(args: Array<String>) {
        println("HopperTestApp running...")
        require(args.size == 1) { "Exactly one argument must be passed (but was: ${args.size})!" }
        RaceConfigReader.read(args[0]).map { config ->
            when (val result = GamePreparer.prepare(config).play()) {
                is RaceResult.NoSolution -> println("No solution")
                is RaceResult.FoundSolution -> println("Optimal solution takes ${result.numberHopsNeeded} hops.")
            }
        }
    }
}
