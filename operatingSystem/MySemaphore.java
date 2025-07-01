package operatingSystem;

public class MySemaphore {
    private boolean signal = true;

    public synchronized void acquire() throws InterruptedException {
        while (!signal) {
            wait();
        }
        signal = false;
    }

    public synchronized void release() {
        signal = true;
        notify();
    }
}
