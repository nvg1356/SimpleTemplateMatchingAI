package threadedgeneticdtw;
import java.util.*;

import static threadedgeneticdtw.generator.current_population;
import static threadedgeneticdtw.generator.ratings_templates;

public class threadfunction implements Runnable{
    generator generate = new generator();

    public void run() {
        while (current_population.size() > 0) {
            ArrayList<Integer> template_to_match = MultiThreading.updating_process();
            generate.template_operations(template_to_match, ratings_templates);
        }
    }
}
