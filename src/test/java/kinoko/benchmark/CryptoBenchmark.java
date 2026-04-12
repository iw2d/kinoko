package kinoko.benchmark;

import kinoko.util.crypto.IGCipher;
import kinoko.util.crypto.MapleCrypto;
import kinoko.util.crypto.ShandaCrypto;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.Random;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class CryptoBenchmark {
    private byte[] data;
    private byte[] iv;

    @Setup(Level.Iteration)
    public void setup() {
        data = new byte[1024]; // simulate packet size
        iv = new byte[4];

        Random r = new Random(123);
        r.nextBytes(data);
        r.nextBytes(iv);
    }

    // --- Individual benchmarks ---

    @Benchmark
    public void mapleCrypto(Blackhole bh) {
        byte[] localData = data.clone();
        byte[] localIv = iv.clone();

        MapleCrypto.crypt(localData, localIv);

        bh.consume(localData);
        bh.consume(localIv);
    }

    @Benchmark
    public void shandaDecrypt(Blackhole bh) {
        byte[] localData = data.clone();

        ShandaCrypto.decrypt(localData);

        bh.consume(localData);
    }

    @Benchmark
    public void innoHash(Blackhole bh) {
        byte[] localIv = iv.clone();

        IGCipher.innoHash(localIv);

        bh.consume(localIv);
    }

    // --- Combined pipeline (realistic usage) ---

    @Benchmark
    public void fullPipeline(Blackhole bh) {
        byte[] localData = data.clone();
        byte[] localIv = iv.clone();

        MapleCrypto.crypt(localData, localIv);
        ShandaCrypto.decrypt(localData);
        IGCipher.innoHash(localIv);

        bh.consume(localData);
        bh.consume(localIv);
    }

    public static void main(String[] args) throws Exception {
        final Options opt = new OptionsBuilder()
                .include(CryptoBenchmark.class.getSimpleName())
                .forks(2)
                .warmupIterations(5)
                .measurementIterations(10)
                .build();

        new Runner(opt).run();
    }
}