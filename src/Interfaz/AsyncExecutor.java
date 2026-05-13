package Interfaz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncExecutor {

	private static final int IO_THREADS = 6;
	private static final ExecutorService IO_POOL = Executors.newFixedThreadPool(IO_THREADS, runnable -> {
		Thread thread = new Thread(runnable);
		thread.setName("renthub-io-" + thread.getId());
		thread.setDaemon(true);
		return thread;
	});

	private AsyncExecutor() {
	}

	public static ExecutorService io() {
		return IO_POOL;
	}
}
