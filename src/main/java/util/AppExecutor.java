package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);

    private AppExecutor() {
        // Prevent creating AppExecutor objects.
    }

    public static void runInBackground(Runnable task) {
        EXECUTOR.submit(task);
    }

    public static void shutdown() {
        EXECUTOR.shutdown();
    }
}