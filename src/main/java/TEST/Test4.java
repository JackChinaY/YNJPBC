package TEST;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * 需求： 实现一个售票程序
 * <p>
 * 创建线程的第二种方式：实现Runnable接口
 * <p>
 * 步骤：<p>
 * 1，定义类实现Runnable接口<p>
 * 2，覆盖Runnable接口中的run方法<p>
 * 将线程要运行的代码存放在该run方法中<p>
 * 3，通过Thread 类建立线程对象<p>
 * 4，将Runnable接口的子类对象作为实际参数传递给Thread类的构造函数
 * 为什么要将Runnable接口中的子类对象传递给Thread的构造函数
 * 因为，自定义的run方法所属的对象是Runnable接口的子类对象
 * 所以要让线程去指定指定对象的run方法，就必须明确该run方法所属对象。
 * <p>
 * 5，调用Thread类的start方法 开启线程并调用Runnable接口子类的run方法
 * <p>
 * 实现方式和继承方式的区别：
 * <p>
 * 实现方式好处：避免了单继承的局限性
 * 在定义建议使用实现方式
 * <p>
 * 两种方式区别：
 * 继承Thread：线程代码存放在Thread子类run方法中。
 * 实现Runnable：线程代码存放在接口的子类的run方法中。
 */

/**
 * 线程
 */
public class Test4 {
    public static void main(String[] args) {
//        线程，继承Thread类
//        TicketThread1 station1 = new TicketThread1("窗口1");
//        TicketThread1 station2 = new TicketThread1("窗口2");
//        TicketThread1 station3 = new TicketThread1("窗口3");
//        station1.start();
//        station2.start();
//        station3.start();

        //线程，实现Runnable接口
//        TicketThread2 ticketRunnable = new TicketThread2();
//        new Thread(ticketRunnable, "线程1").start();
//        new Thread(ticketRunnable, "线程2").start();
//        new Thread(ticketRunnable, "线程3").start();
        //线程池
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 200, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(5));
//
//        for (int i = 0; i < 15; i++) {
//            MyTask myTask = new MyTask(i);
//            executor.execute(myTask);
//            System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
//                    executor.getQueue().size() + "，已执行完的任务数目：" + executor.getCompletedTaskCount());
//        }
//        executor.shutdown();

        // 创建Callable对象
        ThirdThread rt = new ThirdThread();
        // 使用FutureTask来包装Callable对象
        FutureTask<Integer> task = new FutureTask<>(rt);
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName()
                    + " 的循环变量i的值：" + i);
            if (i == 5) {
                // 实质还是以Callable对象来创建、并启动线程
                new Thread(task, "有返回值的线程").start();
            }
        }
        try {
            // 获取线程返回值
            System.out.println("子线程的返回值：" + task.get());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

/**
 * 线程，继承Thread类
 */
class TicketThread1 extends Thread {

    // 通过构造方法给线程名字赋值
    public TicketThread1(String name) {
        super(name);// 给线程名字赋值
    }

    // 为了保持票数的一致，票数要静态
    static int tick = 20;
    // 创建一个静态钥匙
    static Object ob = "aa";//值是任意的

    // 重写run方法，实现买票操作
    @Override
    public void run() {
        while (tick > 0) {
            synchronized (ob) {// 这个很重要，必须使用一个锁
                // 进去的人会把钥匙拿在手上，出来后才把钥匙拿让出来
                if (tick > 0) {
//                    System.out.println(System.nanoTime());
//                    System.out.println(System.currentTimeMillis());
                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + getName() + "卖出了第" + (21 - tick) + "张票");
                    tick--;
                } else {
                    System.out.println("票卖完了");
                }
            }
            try {
                sleep(500);//线程休息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 线程，实现Runnable接口
 */
class TicketThread2 implements Runnable {
    static int ticket = 20;

    @Override
    public void run() {
        while (ticket > 0) {
            if (ticket > 0) {
                //同步代码块，确保当前只有一个线程在操作此代码块
                synchronized (this) {
                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + Thread.currentThread().getName() + "卖出了第" + (21 - ticket) + "张票");
                    ticket--;
                }
            }
            try {
                Thread.sleep(400);//线程休息
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

//    // 为了保持票数的一致，票数要静态
//    static int tick = 20;
//    // 创建一个静态钥匙
//    static Object ob = "aa";//值是任意的
//
//    // 重写run方法，实现买票操作
//    @Override
//    public void run() {
//        while (tick > 0) {
//            synchronized (ob) {// 这个很重要，必须使用一个锁
//                // 进去的人会把钥匙拿在手上，出来后才把钥匙拿让出来
//                if (tick > 0) {
////                    System.out.println(System.nanoTime());
////                    System.out.println(System.currentTimeMillis());
//                    System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + " " + Thread.currentThread().getName() + "卖出了第" + (21 - tick) + "张票");
//                    tick--;
//                } else {
//                    System.out.println("票卖完了");
//                }
//            }
//            try {
//                Thread.sleep(500);//休息一秒
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}

/**
 * 线程池，实现Runnable接口
 */
class MyTask implements Runnable {
    private int taskNum;

    public MyTask(int num) {
        this.taskNum = num;
    }

    @Override
    public void run() {
        System.out.println("正在执行task " + taskNum);
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("task " + taskNum + " 执行完毕！");
    }
}

/**
 * 实现Callable接口来实现线程
 */
class ThirdThread implements Callable<Integer> {
    // 实现call方法，作为线程执行体
    @Override
    public Integer call() {
        int i = 0;
        for (; i < 10; i++) {
            System.out.println(Thread.currentThread().getName()
                    + " 的循环变量i的值：" + i);
        }
        // call()方法可以有返回值
        return i;
    }
}