package threadedregvariant;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.State.TERMINATED;

public class generator {
    // template2 refers to all pattern templates
    static ArrayList<ArrayList<Integer>> template2 = new ArrayList<>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<>();
    base object = new base();
    public ArrayList<Integer> past_player_choices = object.past_player_choices;
    final int turnnumber = object.turnnumber;

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(int turnnumber){ // Time Complexity: O(1)
        int x = 11;
        for (int i = 2; i < 11; i++) {
            double quotient = (double) turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
        }

    public void generate_templates() { // Time Complexity: O()
        for (int j = 2; j <= max_seq_len(turnnumber); j++){
            template1.clear();
            int templen = 1;
            subiteration(j, templen);
            }
        }

    public void subiteration(int seq_len, int templen) {
        for (int a = 1; a <= 2; a++) {
            if (a == 2){
                template1.remove(-1);
            }
            template1.add(a);
            if (templen == seq_len){
                template2.add(template1);
                if (a == 2){
                    break;
                }
                continue;
            }
            templen++;
            subiteration(seq_len, templen);
            }
        }

    public int occurrence_of_segment (ArrayList<Integer> arraylist, ArrayList<Integer> segment, int start_idx) {
        int hits = 0;
        int occurrence = -1;
        for (int i = start_idx; i < arraylist.size(); i++) {
            if (arraylist.get(i).equals(segment.get(i - start_idx))) {
                hits++;
                if (hits == segment.size()) {
                    occurrence = i - segment.size();
                    break;
                }
            }
            else {
                hits = 0;
            }
        }
        return occurrence;
    }

    public HashMap<Integer, Double> get_hits_and_variance(ArrayList<Integer> past_player_choices, ArrayList<Integer> template_to_match) {
        double number_of_hits = 0;
        int last_known_idx = -2;
        ArrayList<Integer> variance_list = new ArrayList<>();
        HashMap<Integer, Double> map = new HashMap<>();
        for (int idx = 0; idx < past_player_choices.size(); idx++) {
            int occurrence = occurrence_of_segment(past_player_choices, template_to_match, idx);
            boolean check = ((occurrence >= last_known_idx) && (occurrence != -1));
            if (check) {
                last_known_idx = occurrence;
                number_of_hits++;
                variance_list.add(occurrence + template_to_match.size() - last_known_idx);
            }
        }
        double variance = get_array_variance(variance_list);
        map.put(0, number_of_hits);
        map.put(1, variance);
        return map;
    }

    public double get_array_variance(ArrayList<Integer> array) {
        double dev_from_avg = 0, sum_of_elements = 0, mean = (sum_of_elements) / array.size();
        for (Integer integer : array) {
            sum_of_elements += integer;
        }
        for (Integer integer : array) {
            dev_from_avg += Math.pow((integer - mean), 2);
        }
        return (dev_from_avg / (array.size() - 1));
    }

    public ArrayList<Integer> generate_best_template(ArrayList<Integer> past_player_choices) {
        generate_templates();

        HashMap<Double, ArrayList<Integer>> template_ratings_collection = new HashMap<>();
        //4-threads
        MultiThreading t1 = new MultiThreading();
        MultiThreading t2 = new MultiThreading();
        MultiThreading t3 = new MultiThreading();
        MultiThreading t4 = new MultiThreading();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        while (template2.size() > 0) {
            if (t1.getState() == TERMINATED) {
                t1.run();
            }
            if (t2.getState() == TERMINATED) {
                t2.run();
            }
            if (t3.getState() == TERMINATED) {
                t3.run();
            }
            if (t4.getState() == TERMINATED) {
                t4.run();
            }
        }
        ArrayList<Double> ratings_array = new ArrayList<>();
        template_ratings_collection.keySet().forEach((n) -> ratings_array.add(n));
        double best_rating = 0;
        for (double rating: ratings_array) {
            if (rating > best_rating) {
                best_rating = rating;
            }
        }
        return template_ratings_collection.get(best_rating);
    }

    public int generate_ai_choice() {
        ArrayList<Integer> pattern = generate_best_template(past_player_choices);
        ArrayList<Integer> last_few_choices = new ArrayList<>();
        last_few_choices.addAll(past_player_choices.subList(past_player_choices.size() - pattern.size(),
                past_player_choices.size()));
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < pattern.size(); k++) {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.addAll(last_few_choices.subList(last_few_choices.size() - k, last_few_choices.size()));
            if (occurrence_of_segment(pattern, temp, 0) == 0) {
                choice = pattern.get(temp.size());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = pattern.get(0);
        }
        return choice;
    }
}
