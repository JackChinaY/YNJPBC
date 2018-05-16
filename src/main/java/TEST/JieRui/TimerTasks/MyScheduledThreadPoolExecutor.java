package TEST.JieRui.TimerTasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务
 */
public class MyScheduledThreadPoolExecutor {
    private void start() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.schedule(new ClientHandler(), 1, TimeUnit.SECONDS);
            BlockingQueue<Runnable> executorQueue = executor.getQueue();
            for (int j = 0; j < executorQueue.size(); j++) {

            }
        }
        executor.shutdown();
    }

    public static void main(String[] args) {
        new MyScheduledThreadPoolExecutor().start();
    }
}

/**
 *
 */
class ClientHandler implements Runnable {

    @Override
    public void run() {
//        System.out.println("客户端启动");
        System.out.println(Thread.currentThread().getName() + " run ");
    }
}