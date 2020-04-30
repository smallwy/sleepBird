package jmh;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class Mybenchmark {
    static int x = 0;

    @Benchmark
    public void a() {
        x++;
    }

    @Benchmark
    public void b() {
        Object object = new Object();
        synchronized (object) {
            x++;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Mybenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

}