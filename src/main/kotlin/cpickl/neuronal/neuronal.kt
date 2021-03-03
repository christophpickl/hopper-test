package cpickl.neuronal

import org.apache.commons.math3.analysis.function.Sigmoid
/*

USECASE IDEEN:
1) athletische figure? quasi BMI: input=height cm, weight kg; output= 1(true), 0(false)
2) formen erkennen: triangle/square/circle; output 0,1,2 (pixel zerlegen)
3) beliebige single char erkennen, anhand von font (test input selbst generieren); kann neuen erkennen?!

 */
fun main() {
    val network = Network.build(
        inputCount = 2,
        layersCount = 1,
        layersNodeSize = 3,
        outputCount = 2,
        learnConfig = LearnConfig(
            learningRate = 1.0,
            momentum = 1.0, // TODO
        )
    )
    println(network.toGraphString())
//    println(network.dumpString())
    network.learn(2,
        listOf(
            TestSet(listOf(0, 1, 0, 1), 0),
            TestSet(listOf(1, 1, 0, 1), 1),
            TestSet(listOf(1, 0, 1, 1), 1),
            TestSet(listOf(0, 0, 1, 1), 0),
        )
    // ASK? TestSet(listOf(1, 0, 0, 0), 0),
    )
}

data class TestSet(
    val inputs: List<Int>,
    val answerPosition: Int, // 0-base indexed
)
data class LearnConfig(
    val learningRate: Double,
    val momentum: Double,
)
data class Network(
    val inputs: List<Node>,
    val hiddenLayers: List<List<Node>>,
    val outputs: List<Node>,
    val learnConfig: LearnConfig,
) {
    companion object {
        fun build(
            inputCount: Int,
            layersCount: Int,
            layersNodeSize: Int, // < 2x in; betweein in/out (if wide apart) -OR- 2/3 in + output
            outputCount: Int,
            learnConfig: LearnConfig,

        ) = Network(
            inputs = 1.rangeTo(inputCount).map { Node("I$it") },
            hiddenLayers = 1.rangeTo(layersCount).map { layer ->
                1.rangeTo(layersNodeSize).map { node -> Node("H$layer-$node") }
            },
            outputs = 1.rangeTo(outputCount).map { Node("O$it") },
            learnConfig = learnConfig,
        ).wireReferences()

        private fun Network.wireReferences() = apply {
            inputs.forEach { input ->
                hiddenLayers.first().forEach { hidden ->
                    val ref = Ref(input, hidden)
                    input.refs += ref
                    hidden.refs += ref
                }
            }
            hiddenLayers.zipWithNext { xs, ys ->
                xs.forEach { x ->
                    ys.forEach { y ->
                        val ref = Ref(x, y)
                        x.refs += ref
                        y.refs += ref
                    }
                }
            }
            outputs.forEach { output ->
                hiddenLayers.last().forEach { hidden ->
                    val ref = Ref(hidden, output)
                    output.refs += ref
                    hidden.refs += ref
                }
            }
        }
    }

    fun learn(iterations: Int, data: List<TestSet>) { // desiredErrorLevel: Double
        require(data.all { it.inputs.size == inputs.size })
        require(data.all { it.answerPosition in 0..(outputs.size - 1) })


        1.rangeTo(iterations).forEach { iteration ->
            println("Iteration $iteration:")
            data.forEach { test ->
                println("\tLearning ... $test")

                hiddenLayers.forEach { nodes ->
                    nodes.forEach { node ->
                        // sum of { InputNode.value * Reference.value } + HiddenNode.value
                        val e = node.incomingRefs.map { ref -> ref.from.value * ref.value }.sum() + node.value
                        val e2 = activationFunction(e) // TODO what to do?
                    }
                }
                outputs.forEachIndexed { index,  output ->
                    val e = output.incomingRefs.map { ref -> ref.from.value * ref.value }.sum() + output.value
                    val currentGuess = activationFunction(e)

                    val correctAnswer = if(index == test.answerPosition) 1.0 else 0.0
                    val delta = correctAnswer - currentGuess // "delta" equals error rate

                    // adjust weights+biases a "bit"!
                    output.incomingRefs.forEach { ref ->
                        val pastChange = 1.0 // TODO simply from previous "change" down below?
                        val change =(learnConfig.learningRate * delta * ref.value) + (learnConfig.momentum * pastChange)
                        // change = (learningRate * delta * value) + (momentum * pastChange)
                        // FIXME propagate back
//                        ref.value += change
                    }
                }
            }
        }
    }
    private val sig = Sigmoid() // sigmoid, tanh
    private fun activationFunction(e: Double): Double {
        return sig.value(e) // 1 / (1 + exp(-x))
    }

    fun dumpString() = "${inputs.joinToString { "${it.id}/${it.valueInt}" }}\n" +
            "${hiddenLayers.joinToString("\n") { it.joinToString { "${it.id}/${it.valueInt}" } }}\n" +
            "${outputs.joinToString { "${it.id}/${it.valueInt}" }}\n"


    fun toGraphString() =
        "\tInput:\n" +
                inputs.joinToString("\n") { "\t\t$it" } + "\n" +
                "\tHidden:\n" +
                hiddenLayers.joinToString() { layer -> layer.joinToString("\n") { "\t\t$it" } + "\n" } +
                "\tOutput:\n" +
                outputs.joinToString("\n") { "\t\t$it" }
}

data class Node(
    val id: String,
    val refs: MutableList<Ref> = mutableListOf(),
    override var value: Double = Math.random(), // bias
) : Valueable {
    val incomingRefs: List<Ref> by lazy { refs.filter { it.to == this@Node } }
    val incoming: List<Node> by lazy { incomingRefs.map { it.from } }
    val outgoingRefs: List<Ref> by lazy { refs.filter { it.from == this@Node } }
    val outgoing: List<Node> by lazy { outgoingRefs.map { it.to } }

    override fun toString() =
        "$id:$valueString -> " +
                "{ in => ${incomingRefs.joinToString() { "${it.from.id}:${it.valueString}" }} } - " +
                "{ out => ${outgoingRefs.joinToString() { "${it.to.id}:${it.valueString}" }} }"
}

data class Ref(
    val from: Node,
    val to: Node,
    override var value: Double = Math.random(), // weight
) : Valueable

interface Valueable {
    var value: Double
    val valueInt get() = (value * 100).toInt()
    val valueString get() = "[${(value * 100).toInt()}]"
}