package threadeddtwvariant;
import java.util.*;
import java.security.SecureRandom;

public class MultiThreading extends Thread{
    generator generate = new generator();
    SecureRandom random = new SecureRandom();

    public void run() {
        while (generate.template2.size() > 0) {
            ArrayList<Integer> template_to_match = generate.template2.get(random.nextInt(0, generate.template2.size()));
            generate.template2.remove(template_to_match);
            generate.template_operations(template_to_match, generate.ratings_templates);
        }
    }
}
