package threadedgeneticreg;

import threadedregvariant.generator;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static threadedgeneticreg.base.random;
import static threadedgeneticreg.generator.current_population;

public class MultiThreading {
    public static synchronized ArrayList<Integer> updating_process() {
        ArrayList<Integer> template_to_match = current_population.get(random.nextInt(current_population.size()));
        current_population.remove(template_to_match);
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
