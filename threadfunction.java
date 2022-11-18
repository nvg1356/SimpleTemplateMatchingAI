package threadedgeneticreg;
import java.util.ArrayList;

import static threadedregvariant.MultiThreading.updating_process;
import static threadedgeneticreg.generator.current_population;

public class threadfunction implements Runnable{
    generator generate = new generator();

    @Override
    public void run() {
        while (current_population.size() > 0) {
            ArrayList<Integer> template_to_match = updating_process();
            generate.template_operations(template_to_match);
        }
    }

}
