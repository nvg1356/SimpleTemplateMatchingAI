package threadedregvariant;
import java.util.ArrayList;

import static threadedregvariant.MultiThreading.updating_process;
import static threadedregvariant.base.past_player_choices;
import static threadedregvariant.generator.get_hits_and_variance;
import static threadedregvariant.generator.template_ratings_collection;

public class threadfunction implements Runnable{

    @Override
    public void run() {
        ArrayList<Integer> template_to_match = updating_process();
        double hits = get_hits_and_variance(past_player_choices, template_to_match).get(0);
        double lag_variance = get_hits_and_variance(past_player_choices, template_to_match).get(1);
        double rating = hits + (1 / lag_variance);
        template_ratings_collection.put(rating, template_to_match);
    }

}
