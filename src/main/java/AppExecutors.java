import java.util.concurrent.*;

class AppExecutors {
    // static variable single_instance of type Singleton 
    private static AppExecutors single_instance = null;
    private final ScheduledExecutorService crawlerThread;
    private final ScheduledExecutorService indexThread;

    // private constructor restricted to this class itself
    private AppExecutors() {
        crawlerThread = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("crawlerThread");
            return thread;
        });
        indexThread = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("indexThread");
            return thread;
        });
    }

    // static method to create instance of Singleton class 
    public static synchronized AppExecutors getInstance() {
        if (single_instance == null)
            single_instance = new AppExecutors();

        return single_instance;
    }

    public void indexThreadExecute(Runnable runnable) {
        indexThread.execute(runnable);
    }

    public void crawlerThreadExecute(Runnable runnable) {
        crawlerThread.execute(runnable);
    }

    public void crawlerThreadSubmit(BasicWebCrawler callable) throws ExecutionException, InterruptedException {
        crawlerThread.submit(callable).get();
    }

    public boolean isIndexThreadTerminated() {
        return indexThread.isTerminated();
    }

    public boolean isCrawlerThreadTerminated() {
        return crawlerThread.isTerminated();
    }

    public void shutdownThreads() {
        indexThread.shutdown();
        crawlerThread.shutdown();
    }
} 