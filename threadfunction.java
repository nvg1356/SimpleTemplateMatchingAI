package threadeddtwvariant;
import java.util.*;

import static threadeddtwvariant.generator.template2;
import static threadeddtwvariant.generator.ratings_templates;

public class threadfunction implements Runnable{
    generator generate = new generator();

    public void run() {
        while (template2.size() > 0) {
            ArrayList<Integer> template_to_match = MultiThreading.updating_process();
            generate.template_operations(template_to_match, ratings_templates);
        }
    }
}
