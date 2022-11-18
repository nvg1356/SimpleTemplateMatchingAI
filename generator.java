package threadedgeneticreg;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static threadedgeneticreg.base.*;

public class generator {
    ArrayList<ArrayList<ArrayList<Integer>>> template3 = new ArrayList<>();
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<>();
    ArrayList<Integer> best_template = new ArrayList<>();
    SecureRandom random = new SecureRandom();
    static ArrayList<ArrayList<Integer>> current_population = new ArrayList<>();
    static HashMap<Double, ArrayList<Integer>> ratings_templates = new HashMap<>();
    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(){
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = base.turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
    }

    public void generate_all_templates() { // Time Complexity: O()
        for (int j = 2; j <= max_seq_len(); j++) {
            template1.clear();
            int templen = 1;
            subiteration(j, templen);
            template3.add(template2);
            template2.clear();
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

    public void get_initial_population() {
        ArrayList<Integer> samples_for_each_patt_length = new ArrayList<>();
        for (int i = 0; i < template3.size(); i++) {
            float x = initial_pop_size / template3.size();
            if ((i % 2) == 0) {
               x += 0.5 ;
            }
            samples_for_each_patt_length.add(Math.round(x));
        }
        for (int i = 0; i < template3.size(); i++) {
            if (samples_for_each_patt_length.get(i) > template3.get(i).size()) {
                samples_for_each_patt_length.set(i + 1, samples_for_each_patt_length.get(i + 1) + samples_for_each_patt_length.get(i) - template3.get(i).size());
                samples_for_each_patt_length.set(i , template3.get(i).size());
            }
            for (int k = 0; k < samples_for_each_patt_length.get(i); k++) {
                current_population.add(template3.get(i).get(random.nextInt(0, template3.get(i).size())));
            }
        }
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

    public float get_array_variance(ArrayList<Integer> array) {
        float dev_from_avg = 0, sum_of_elements = 0, mean = (sum_of_elements) / array.size();
        for (int i = 0; i < array.size(); i++) {
            sum_of_elements += array.get(i);
        }
        for (int i = 0; i < array.size(); i++) {
            dev_from_avg += Math.pow((array.get(i) - mean), 2);
        }
        return (dev_from_avg / (array.size() - 1));
    }

    public void generate_best_template() {
        generate_all_templates();
        get_initial_population();
        int current_mutation = 0;
        sub_generation(current_mutation);
    }

    public void sub_generation(int current_mutation) {
        ArrayList<ArrayList<Integer>> templates_for_mutation = new ArrayList<>();

        if (current_mutation == mutation_limit) {
            double best_rating = Collections.max(ratings_templates.keySet());
            best_template = ratings_templates.get(best_rating);
        }
        else {
            //criteria to get top 20% of initial_population into templates_for_mutation
            for (int i = 0; i < Math.round(current_population.size() * 0.4); i++) {
                double max = Collections.max(ratings_templates.keySet());
                templates_for_mutation.add(ratings_templates.get(max));
                ratings_templates.remove(max);
            }
            ratings_templates.clear();
            current_population.clear();
            get_mutated_population(templates_for_mutation, current_mutation);
        }
    }

    public void template_operations(ArrayList<Integer> template_to_match) {
        double hits = get_hits_and_variance(past_player_choices, template_to_match).get(0);
        double lag_variance = get_hits_and_variance(past_player_choices, template_to_match).get(1);
        double rating = hits + (1 / lag_variance);
        ratings_templates.put(rating, template_to_match);
    }

    public void get_mutated_population (ArrayList<ArrayList<Integer>> templates_for_mutation, int current_mutation) { //Reverse Sequence Mutation
        for (ArrayList<Integer> template: templates_for_mutation) {
            current_population.add(template);
            ArrayList<Integer> mutant = template;
            int inv_start = random.nextInt(1, (int) Math.round(mutant.size() * 0.5 - 0.5));
            for (int i = inv_start; i < (mutant.size() - inv_start); i++) {
                mutant.set(i, mutant.get(mutant.size() - inv_start - i));
            }
            current_population.add(mutant);
        }
        current_mutation++;
        sub_generation(current_mutation);
    }

    public int generate_ai_choice() {
        generate_best_template();
        ArrayList<Integer> last_few_choices = new ArrayList<>();
        last_few_choices.addAll(past_player_choices.subList(past_player_choices.size() - best_template.size(),
                past_player_choices.size()));
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < best_template.size(); k++) {
            ArrayList<Integer> temp = new ArrayList<>();
            temp.addAll(last_few_choices.subList(last_few_choices.size() - k, last_few_choices.size()));
            if (occurrence_of_segment(best_template, temp, 0) == 0) {
                choice = best_template.get(temp.size());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = best_template.get(0);
        }
        return choice;
    }
}
