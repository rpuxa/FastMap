package benchmark

import MyFastMap
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import java.util.concurrent.TimeUnit
import kotlin.random.Random

abstract class AbstractBenchmark( private val keys: IntArray) {

    private val hashMap = HashMap(keys.map { it to Any() }.toMap())
    private val myFastMap = MyFastMap(hashMap)

    init {
        keys.forEach {
            require(hashMap[it] == myFastMap[it]) {
                "MyFastMap works incorrectly!"
            }
        }
        (0..1000).map { Random.nextInt(500_000) + (1 shl 21) }
            .forEach {
                require(myFastMap[it] == null) {
                    "MyFastMap works incorrectly!"
                }
            }
    }

    fun hashmap(blackhole: Blackhole) {
        keys.forEach {
            blackhole.consume(hashMap[it])
        }
    }

    fun myfastmap(blackhole: Blackhole) {
        keys.forEach {
            blackhole.consume(myFastMap[it])
        }
    }
}

@Fork(1, warmups = 1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
open class BigNumbersBenchmark : AbstractBenchmark(
    keys = (0..Random.nextInt(1 shl 13)).map {
        Random.nextInt(1 shl 20)
    }.toIntArray()
) {
    @Benchmark
    fun hashMap(blackhole: Blackhole) {
        hashmap(blackhole)
    }

    @Benchmark
    fun myFastMap(blackhole: Blackhole) {
        myfastmap(blackhole)
    }
}

@Fork(1, warmups = 1)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
open class SmallNumbersBenchmark : AbstractBenchmark(
    keys = (0..Random.nextInt(127)).map {
        Random.nextInt(1 shl 20)
    }.toIntArray()
) {
    @Benchmark
    fun hashMap(blackhole: Blackhole) {
        hashmap(blackhole)
    }

    @Benchmark
    fun myFastMap(blackhole: Blackhole) {
        myfastmap(blackhole)
    }
}

fun main() {
    Runner(
        OptionsBuilder()
        .include(BigNumbersBenchmark::class.java.simpleName)
        .include(SmallNumbersBenchmark::class.java.simpleName)
        .build()
    ).run()
}