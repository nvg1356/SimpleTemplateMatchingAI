package threadedgeneticdtw;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import static threadedgeneticdtw.base.initial_pop_size;
import static threadedgeneticdtw.base.turnnumber;

public class generator {
    // template3 refers to a collection of all pattern templates available at the current turn_number
    // template3[k] refers to the group of pattern templates with a pattern length of k + 2
    ArrayList<ArrayList<ArrayList<Integer>>> template3 = new ArrayList<>();
    // template2 refers to a group of pattern templates for a particular pattern length
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<>();
    base object = new base();
    ArrayList<Integer> best_template = new ArrayList<>();
    SecureRandom random = new SecureRandom();
    static ArrayList<ArrayList<Integer>> current_population = new ArrayList<>();
    static HashMap<Double, ArrayList<Integer>> ratings_templates = new HashMap<>();

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(){ // Time Complexity: O(1)
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
    }

    public void generate_templates() { // Time Complexity: O()
        for (int j = 2; j <= max_seq_len(); j++){
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

    public void get_initial_population(int number_of_samples) {
        ArrayList<ArrayList<Integer>> initial_population = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> samples_for_each_patt_length = new ArrayList<Integer>();
        for (int i = 0; i < template3.size(); i++) {
            float x = number_of_samples / template3.size();
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
                initial_population.add(template3.get(i).get(random.nextInt(0, template3.get(i).size())));
            }
        }
        current_population = initial_population;
    }

    public double get_dissimilarity(ArrayList<Integer> warped_string, ArrayList<Integer> actual_string) {
        int differences = 0;
        for (int i = 0; i <= actual_string.size(); i++) {
            if (warped_string.get(i) == actual_string.get(i)) {
                differences++;
            }
        }
        return differences / actual_string.size();
    }

    public void generate_best_template() {
        generate_templates();
        get_initial_population(initial_pop_size);
        int current_mutation = 0;
        sub_generation(current_population, current_mutation);
    }

    public void sub_generation(ArrayList<ArrayList<Integer>> initial_population, int current_mutation) {
        ArrayList<ArrayList<Integer>> templates_for_mutation = new ArrayList<>();
        MultiThreading multithreading = new MultiThreading();
        multithreading.run();
        for (ArrayList<Integer> template_to_match : initial_population){
            template_operations(template_to_match, ratings_templates);
        }
        if (current_mutation == object.mutation_limit) {
            double lowest_rating = Collections.min(ratings_templates.keySet());
            best_template = ratings_templates.get(lowest_rating);
        }
        else {
            //criteria to get top 20% of initial_population into templates_for_mutation
            for (int i = 0; i < Math.round(initial_population.size() * 0.4); i++) {
                double lowest_rating = Collections.min(ratings_templates.keySet());
                templates_for_mutation.add(ratings_templates.get(lowest_rating));
                ratings_templates.remove(lowest_rating);
            }
            ratings_templates.clear();
            get_mutated_population(templates_for_mutation, current_mutation);
        }
    }

    public void get_mutated_population (ArrayList<ArrayList<Integer>> templates_for_mutation, int current_mutation) { //Reverse Sequence Mutation
        ArrayList<ArrayList<Integer>> mutated_population = new ArrayList<>();
        for (ArrayList<Integer> template: templates_for_mutation) {
            mutated_population.add(template);
            ArrayList<Integer> mutant = template;
            int inv_start = random.nextInt(1, (int) Math.round(mutant.size() * 0.5 - 0.5));
            for (int i = inv_start; i < (mutant.size() - inv_start); i++) {
                mutant.set(i, mutant.get(mutant.size() - inv_start - i));
            }
            mutated_population.add(mutant);
        }
        current_mutation++;
        current_population = mutated_population;
        sub_generation(current_population, current_mutation);
    }

    public void template_operations(ArrayList<Integer> template_to_match, HashMap<Double, ArrayList<Integer>> ratings_templates) {
        if (object.past_player_choices.size() > template_to_match.size()) {
            ArrayList<ArrayList<Integer>> sub_samples = new ArrayList<>();
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                try {
                    sub_samples.add(subArrayList(object.past_player_choices, i * template_to_match.size(), (i + 1) * template_to_match.size()));
                } catch (IndexOutOfBoundsException e){
                    sub_samples.add(subArrayList(object.past_player_choices, i * template_to_match.size(), object.past_player_choices.size()));
                    break;
                }
            }
            HashMap<Double, ArrayList<Integer>> sub_ratings_templates = new HashMap<>();
            for (ArrayList<Integer> sub_sample : sub_samples) {
                double distance_counter = 0;
                ArrayList<Integer> warped_string = new ArrayList<>();
                int[] current_position = {0, 0};
                update_t_d_d(current_position, warped_string, distance_counter, template_to_match, sub_sample, sub_ratings_templates, true);
                double unsuitability = 0;
                for (Double sub_unsuitability: sub_ratings_templates.keySet()) {
                    unsuitability += sub_unsuitability;
                }
                ratings_templates.put(unsuitability, template_to_match);
                sub_ratings_templates.clear();
            }
        }
        else {
            double distance_counter = 0;
            ArrayList<Integer> warped_string = new ArrayList<>();
            int[] current_position = {0, 0};
            update_t_d_d(current_position, warped_string, distance_counter, template_to_match, object.past_player_choices, ratings_templates, false);
        }
    }

    public void update_t_d_d(int[] current_position, ArrayList<Integer> warped_string, double distance_counter, ArrayList<Integer> template, ArrayList<Integer> sample, HashMap<Double, ArrayList<Integer>> ratings_templates, boolean special) {
        if ((current_position[0] == sample.size()) && (current_position[1] == template.size())) {
            double unsuitability;
            if (special) {
                double number_of_segments = Math.round(0.5 + (object.past_player_choices.size() / template.size()));
                unsuitability = distance_counter + (get_dissimilarity(warped_string, sample) / number_of_segments);
            }
            else {
                unsuitability = distance_counter + get_dissimilarity(warped_string, sample);
            } ratings_templates.put(unsuitability, template);
        }
        else {
            if (current_position[0] == sample.size() - 1) {
                distance_counter += template.size() - 1 - current_position[1];
                current_position[1] = template.size() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, ratings_templates, special);
            }
            if (current_position[1] == template.size() - 1) {
                distance_counter += sample.size() - 1 - current_position[0];
                for (int i = current_position[0] + 1; i <= sample.size() - 1; i++) {
                    warped_string.add(template.get(current_position[1]));
                }
                current_position[0] = sample.size() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, ratings_templates, special);
            }
            else {
                int potential_paths = 0;
                for (int i = current_position[0]; i <= sample.size() - 1; i++) {
                    if (Objects.equals(sample.get(i), template.get(current_position[1] + 1))) {
                        if (i == current_position[0]) {
                            try {
                                subArrayList(warped_string, 0, warped_string.size() - 2).add(template.get(current_position[1] + 1));
                                distance_counter++;
                            } catch (Exception StringIndexOutOfBounds) {
                                warped_string.clear();
                                warped_string.add(template.get(current_position[1] + 1));
                                distance_counter++;
                            }
                        }
                        else {
                            for (int a = current_position[0] + 1; a < i; a++) {
                                warped_string.add(template.get(current_position[1]));
                                distance_counter++;
                            }
                            warped_string.add(template.get(current_position[1] + 1));
                            distance_counter += Math.sqrt(2);
                        }
                        current_position[0] = i;
                        current_position[1]++;
                        potential_paths++;
                        update_t_d_d(current_position, warped_string, distance_counter, template, sample, ratings_templates, special);
                    }
                }
                if (potential_paths == 0) {
                    distance_counter += Math.sqrt(2);
                    warped_string.add(template.get(current_position[1] + 1));
                    current_position[0]++;
                    current_position[1]++;
                    update_t_d_d(current_position, warped_string, distance_counter, template, sample, ratings_templates, special);
                }
            }
        }
    }

    public ArrayList<Integer> subArrayList(ArrayList<Integer> original, int start_idx, int end_idx) {
        ArrayList<Integer> modified_array = new ArrayList<>();
        for (int i = start_idx; i < end_idx; i++) {
            modified_array.add(original.get(i));
        }
        return modified_array;
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

    public int generate_ai_choice() {
        generate_best_template();
        ArrayList<Integer> last_few_choices = subArrayList(object.past_player_choices, object.past_player_choices.size() - best_template.size(), object.past_player_choices.size());
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < best_template.size(); k++) {
            ArrayList<Integer> temp = subArrayList(last_few_choices, last_few_choices.size() - k, last_few_choices.size());
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