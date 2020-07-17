package org.eclipse.jetty.tests.jmh;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.uncommons.maths.random.AESCounterRNG;

@State(Scope.Benchmark)
@Threads(4000)
@Fork(1)
@Warmup(iterations = 4, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 4, time = 1, timeUnit = TimeUnit.SECONDS)
public class RandomImplBenchmark
{
    @Param({"NativePRNG", "NativePRNGNonBlocking", "SHA1PRNG", "AESCounterRNG"})
    String algorithm;

    Random randomImpl;

    @Setup(Level.Trial)
    public void setupTrial() throws Exception
    {
        if (algorithm.equalsIgnoreCase("AESCounterRNG"))
        {
            randomImpl = new AESCounterRNG();
        }
        else
        {
            randomImpl = SecureRandom.getInstance(algorithm);
        }
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void testWebSocketMask() throws Exception
    {
        byte[] mask = new byte[4];
        randomImpl.nextBytes(mask);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput})
    public void testClientNonce() throws Exception
    {
        byte[] mask = new byte[8];
        randomImpl.nextBytes(mask);
    }

    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
            .include(RandomImplBenchmark.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }
}
