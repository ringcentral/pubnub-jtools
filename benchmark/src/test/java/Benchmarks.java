import com.github.pubnubjtools.benchmark.Benchmark;
import com.github.pubnubjtools.benchmark.LoadType;
import com.github.pubnubjtools.benchmark.TransportMode;

import java.time.Duration;

public class Benchmarks {

    private static final int RPS = 100;
    private static final int THREAD_COUNT = 100;
    private static final int MESSAGE_SIZE = 1000;
    public static final Duration BENCHMARK_DURATION = Duration.ofMinutes(1);


    public static class Test_Publish_OFFICIAL_PUNUB_DEFAULT {
        public static void main(String[] args) {
            doPublishBenchmark(TransportMode.OFFICIAL_PUNUB_DEFAULT);
        }
    }

    public static class Test_Publish_OFFICIAL_PUNUB_MANY_INSTANCES_WITH_LOCK_STRIPING_WORKAROUND {
        public static void main(String[] args) {
            doPublishBenchmark(TransportMode.OFFICIAL_PUNUB_MANY_INSTANCES_WITH_LOCK_STRIPING_WORKAROUND);
        }
    }

    public static class Test_Publish_OFFICIAL_PUNUB_ONE_INSTANCE_WITH_MANY_THREADS_WORKAROUND {
        public static void main(String[] args) {
            doPublishBenchmark(TransportMode.OFFICIAL_PUNUB_ONE_INSTANCE_WITH_MANY_THREADS_WORKAROUND);
        }
    }

    public static class Test_Publish_PUNUB_JTOOLS_PUSHER {
        public static void main(String[] args) {
            doPublishBenchmark(TransportMode.PUNUB_JTOOLS_PUSHER);
        }
    }

    public static class Test_BindingDeviceTokenToChannel_PUNUB_JTOOLS_PUSHER {
        public static void main(String[] args) {
            String deviceTokenPreffix = "aaaabbbbccccddddaaaabbbbccccddddaaaabbbbccccdddd";
            final LoadType loadType = LoadType.bindChannelToDevice(deviceTokenPreffix);

            Benchmark benchmark = new Benchmark(TransportMode.PUNUB_JTOOLS_PUSHER, RPS, THREAD_COUNT, Duration.ofMinutes(5), TestCredentials.CREDENTIALS, loadType);
            benchmark.execute();
        }
    }

    private static void doPublishBenchmark(TransportMode transportMode) {
        final LoadType loadType = LoadType.publishMessage(MESSAGE_SIZE);

        Benchmark benchmark = new Benchmark(transportMode, RPS, THREAD_COUNT, BENCHMARK_DURATION, TestCredentials.CREDENTIALS, loadType);
        benchmark.execute();
    }

}

