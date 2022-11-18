package threadedregvariant;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static threadedregvariant.base.random;

public class MultiThreading {
    public static synchronized ArrayList<Integer> updating_process() {
        ArrayList<Integer> template_to_match = generator.template2.get(random.nextInt(generator.template2.size()));
        generator.template2.remove(template_to_match);
        return template_to_match;
    }

    public void run() {
        ExecutorService executorservice = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            executorservice.execute(new threadfunction());
        }
        executorservice.shutdown();
        while (!executorservice.isTerminated()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
