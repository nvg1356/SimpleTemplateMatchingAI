package threadedregvariant;

import java.util.*;
import java.security.SecureRandom;

public class MultiThreading extends Thread{
    generator generate = new generator();
    SecureRandom random = new SecureRandom();

    public void run(HashMap<Double, ArrayList<Integer>> template_rating_collection) {
        ArrayList<Integer> template_to_match = generator.template2.get(random.nextInt(generator.template2.size()));
        generator.template2.remove(template_to_match);
        double hits = generate.get_hits_and_variance(generate.past_player_choices, template_to_match).get(0);
        double lag_variance = generate.get_hits_and_variance(generate.past_player_choices, template_to_match).get(1);
        double rating = hits + (1 / lag_variance);
        template_rating_collection.put(rating, template_to_match);
    }
}
