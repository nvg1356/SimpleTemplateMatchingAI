package threadeddtwvariant;

import java.util.*;
import java.util.concurrent.*;

import static threadeddtwvariant.base.random;
import static threadeddtwvariant.generator.template2;

public class MultiThreading {
    static synchronized ArrayList<Integer> updating_process() {
        ArrayList<Integer> template_to_match = template2.get(random.nextInt(template2.size()));
        template2.remove(template_to_match);
        return template_to_match;
    }

    public void run() {
        ExecutorService executorservice = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            Runnable thread_function = new threadfunction();
            executorservice.execute(thread_function);
        }
        executorservice.shutdown();
        while (!executorservice.isTerminated()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Thread has been interrupted.");
                return;
            }
        }
    }
}