package cpickl.visualizer

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.media.AudioSpectrumListener
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import java.io.File

val MUTE_ON_START = true
val DO_PRINT_DEBUG = false

/**
 * see: http://www.java2s.com/ref/java/javafx-audiospectrumlistener-handle-spectrum-data-update.html
 */
class VisualizerApp : AudioSpectrumListener, Application() {

    private val mp3 = File("/Users/wu/Desktop/audio.mp3")
    private val media = Media(mp3.toURI().toString())
    private val player = MediaPlayer(media)
    private val visualizers: Set<Visualizer> = setOf(
        BalkenVisualizer(),
        CircleVisualizer(),
    )

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(VisualizerApp::class.java, *args)
        }
    }

    init {
        player.audioSpectrumListener = this
        player.audioSpectrumThreshold = -100
        player.setOnReady { println("Player ready") }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Visualizer"
        if (MUTE_ON_START) {
            player.isMute = true
        }

        val btnPanel = StackPane().apply {
            children.add(Button("toggle pause").apply {
                setOnAction {
                    if (player.status == MediaPlayer.Status.PAUSED) {
                        player.play()
                    } else {
                        player.pause()
                    }
                }
            })
        }

        val root = VBox()
        root.children.add(btnPanel)
        visualizers.forEach {
            root.children.add(it.view)
        }
        primaryStage.scene = Scene(root, 1200.0, 500.0)
        primaryStage.setOnShown { player.play() }
        primaryStage.show()
    }

    override fun spectrumDataUpdate(
        timestamp: Double, // current location in seconds
        duration: Double, // always 0.1
        rawMagnitudes: FloatArray, // the data in DB; size=128
        phases: FloatArray // ignore so far
    ) {
        val magnitudes = rawMagnitudes.map { (it - player.audioSpectrumThreshold) / 100 } // [ 0,0 .. 1,0 ]
        if (DO_PRINT_DEBUG) {
            val p = "%.2f"
            System.out.printf(
                "sec: %.2f => $p / $p / $p / $p / $p%n",
                timestamp,
                magnitudes[0],
                magnitudes[1],
                magnitudes[2],
                magnitudes[3],
                magnitudes[4]
            )
        }
        // https://www.tutorialspoint.com/javafx/javafx_2d_shapes.htm
        visualizers.forEach {
            it.onData(magnitudes)
        }

    }
}

interface Visualizer {
    val view: Node
    fun onData(magnitudes: List<Float>)
}

class CircleVisualizer : Visualizer, Group() {
    override val view = this
    private val maxRadius = 50.0
    private val gap = 10.0

    init {
        resize(100.0, 100.0)
    }

    override fun onData(magnitudes: List<Float>) {
        children.clear()
        listOf(10, 60, 110).forEachIndexed { i, magnPos ->
            val magn: Float = magnitudes[magnPos].let {
                when(magnPos) {
                    10 -> it
                    60 -> reconvert(it, 0.6F)
                    110 -> reconvert(it, 0.2F)
                    else -> error("unhandled magnitude position: $magnPos")
                }
            }
            children.add(Circle(i * (maxRadius * 2 + gap), 10.0 + maxRadius, maxRadius * magn, when(i) {
                0 -> Color.RED
                1 -> Color.GREEN
                else -> Color.BLUE
            }))
        }
    }

    private fun reconvert(magnitude: Float, upper: Float): Float {
        if(magnitude == 0.0F) return 0.0F
        return (magnitude / upper).coerceAtMost(1.0F)
    }
}

class BalkenVisualizer : Visualizer, Group() {
    override val view = this
    private val rectWidth = 5.0
    private val horizontalGap = 0.0
    private val rectMaxHeight = 300.0

    override fun onData(magnitudes: List<Float>) {
        children.clear()

        children.add(Rectangle(0.0, 0.0, 128.0 * (rectWidth + horizontalGap), rectMaxHeight).apply {
            fill = Color.color(0.6, 0.8, magnitudes[10].toDouble())
        })

        magnitudes.forEachIndexed { i, magnitude ->
            val posX = i * (rectWidth + horizontalGap)
            val height = rectMaxHeight * magnitude
            val posY = rectMaxHeight - height
            children.add(Rectangle(posX, posY, rectWidth, height).apply {
                fill = colorFor(magnitude)
            })
        }
        magnitudes.forEachIndexed { i, magnitude ->
            if (i % 10 == 0) {
                val posX = i * (rectWidth + horizontalGap)
                val height = rectMaxHeight * magnitude
                val posY = rectMaxHeight - height
                children.add(Text(posX, posY, "${(magnitude * 100).toInt()}"))
            }
        }

        val avg = magnitudes.map { (it * 100) }.average()
        val max = magnitudes.maxOrNull()!!
        println("avg: ${avg.toInt()}, max: $max")
    }

    // Color.color(1.0, 1.0, 1.0)
    private fun colorFor(magnitude: Float) = when (magnitude) {
        in 0.0F..0.1F -> Color.GREEN
        in 0.1F..0.5F -> Color.YELLOW
        in 0.3F..0.5F -> Color.ORANGE
        in 0.5F..1.0F -> Color.RED
        else -> Color.BLACK
    }
}
