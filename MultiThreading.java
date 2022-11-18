package threadedgeneticdtw;

import java.util.*;
import java.util.concurrent.*;

import static threadedgeneticdtw.generator.current_population;

public class MultiThreading {
    static synchronized ArrayList<Integer> updating_process() {
        ArrayList<Integer> template_to_match = current_population.get(base.random.nextInt(current_population.size()));
        current_population.remove(template_to_match);
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
