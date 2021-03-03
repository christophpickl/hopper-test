package cpickl.hoppertest

object RaceConfigWriter {
    @JvmStatic
    fun main(args: Array<String>) {
        println(write(RaceConfig.any))
    }

    fun write(vararg configs: RaceConfig) = "${configs.size}\n" + configs.joinToString("\n") { config ->
        listOf(
            config.size.write(),
            "${config.start.write()} ${config.end.write()}",
            "${config.obstacles.size}",
            *config.obstacles.map { "${it.from.write()} ${it.to.write()}" }.toTypedArray()
        ).joinToString("\n")
    }


    private fun GridSize.write() = "$width $height"
    private fun Position.write() = "$x $y"
}