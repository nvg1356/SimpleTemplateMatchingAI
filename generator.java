package geneticdtw;

import java.security.SecureRandom;
import java.util.*;

public class generator {
    // template3 refers to a collection of all pattern templates available at the current turn_number
    // template3[k] refers to the group of pattern templates with a pattern length of k + 2
    ArrayList<ArrayList<ArrayList<Integer>>> template3 = new ArrayList<ArrayList<ArrayList<Integer>>>();
    // template2 refers to a group of pattern templates for a particular pattern length
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<ArrayList<Integer>>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<Integer>();
    geneticdtw.base object = new base();
    ArrayList<Integer> past_player_choices = object.past_player_choices;
    final int turn_number = object.turnnumber;
    final int mutation_limit = object.mutation_limit;
    ArrayList<Integer> best_template = new ArrayList<Integer>();
    SecureRandom random = new SecureRandom();

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(int turn_number){ // Time Complexity: O(1)
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = turn_number / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
    }

    public void generate_templates() { // Time Complexity: O()
        for (int j = 2; j <= max_seq_len(turn_number); j++){
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

    public ArrayList<ArrayList<Integer>> get_initial_population(int number_of_samples) {
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
        return initial_population;
    }

    public float get_dissimilarity(String warped_string, String actual_string) {
        int differences = 0;
        for (int i = 0; i <= actual_string.length(); i++) {
            if (warped_string.charAt(i) == actual_string.charAt(i)) {
                differences++;
            }
        }
        float dissimilarity = differences / actual_string.length();
        return dissimilarity;
    }

    public void generate_best_template(int initial_pop_size) {
        generate_templates();
        ArrayList<ArrayList<Integer>> initial_population = get_initial_population(initial_pop_size);
        int current_mutation = 0;
        sub_generation(initial_population, current_mutation);
    }

    public void sub_generation(ArrayList<ArrayList<Integer>> initial_population, int current_mutation) {
        ArrayList<ArrayList<Integer>> templates_for_mutation = new ArrayList<>();
        ArrayList<Float> ratings_array = new ArrayList<>();
        ArrayList<Float> dissimilarities = new ArrayList<>();
        ArrayList<Float> distances = new ArrayList<>();
        for (ArrayList<Integer> template_to_match : initial_population){
            String template_to_match_string = template_to_match.toString();
            String sample = past_player_choices.toString();
            if (sample.length() > template_to_match_string.length()) {
                ArrayList<String> sub_samples = new ArrayList<String>();
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    try {
                        sub_samples.add(sample.substring(i * template_to_match_string.length(), (i + 1) * template_to_match_string.length()));
                    } catch (Exception StringIndexOutOfBounds){
                        sub_samples.add(sample.substring(i * template_to_match_string.length()));
                        break;
                    }
                }
                ArrayList<Float> sub_dissimilarities = new ArrayList<>();
                ArrayList<Float> sub_distances = new ArrayList<>();
                for (int i = 0; i < sub_samples.size(); i++) {
                    float distance_counter = 0;
                    String warped_string = "";
                    int[] current_position = {0, 0};
                    update_t_d_d(current_position, warped_string, distance_counter, template_to_match_string, sub_samples.get(i), sub_dissimilarities, sub_distances);
                }
                dissimilarities.add(get_array_sum(sub_dissimilarities) / sub_dissimilarities.size());
                distances.add(get_array_sum(sub_distances));
            }
            else {
                float distance_counter = 0;
                String warped_string = "";
                int[] current_position = {0, 0};
                update_t_d_d(current_position, warped_string, distance_counter, template_to_match_string, sample, dissimilarities, distances);
            }
        }
        for (int i =0; i < distances.size(); i++) {
            ratings_array.add(1 / (dissimilarities.get(i) + distances.get(i)));
        }
        if (current_mutation == mutation_limit) {
            float best_rating = 0;
            for (float element: ratings_array) {
                if (best_rating < element) {
                    best_rating = element;
                }
            }
            best_template = initial_population.get(ratings_array.indexOf(best_rating));
        }
        else {
            //criteria to get top 20% of initial_population into templates_for_mutation
            for (int i = 0; i < Math.round(initial_population.size() * 0.4); i++) {
                float max = ratings_array.get(0);
                for (float element: ratings_array) {
                    if (element > max) {
                        max = element;
                    }
                }
                templates_for_mutation.add(initial_population.get(ratings_array.indexOf(max)));
                initial_population.remove(ratings_array.indexOf(max));
                ratings_array.remove(max);
            }
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
        sub_generation(mutated_population, current_mutation);
    }

    public float get_array_sum(ArrayList<Float> some_array) {
        float sum = 0;
        for (int i = 0; i < some_array.size(); i++) {
            sum += some_array.get(i);
        }
        return sum;
    }

    public void update_t_d_d(int[] current_position, String warped_string, float distance_counter, String template, String sample, ArrayList<Float> dissimilarities, ArrayList<Float> distances) {
        if ((current_position[0] == sample.length()) && (current_position[1] == template.length())) {
            dissimilarities.add(get_dissimilarity(warped_string, sample));
            distances.add(distance_counter);
        }
        else {
            if (current_position[0] == sample.length() - 1) {
                distance_counter += template.length() - 1 - current_position[1];
                current_position[1] = template.length() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, dissimilarities, distances);
            }
            if (current_position[1] == template.length() - 1) {
                distance_counter += sample.length() - 1 - current_position[0];
                for (int i = current_position[0] + 1; i <= sample.length() - 1; i++) {
                    warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1])));
                }
                current_position[0] = sample.length() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, dissimilarities, distances);
            }
            else {
                int potential_paths = 0;
                for (int i = current_position[0]; i <= sample.length() - 1; i++) {
                    if (sample.charAt(i) == template.charAt(current_position[1] + 1)) {
                        if (i == current_position[0]) {
                            try {
                                warped_string = warped_string.substring(0, warped_string.length() - 2) + String.format("%x", template.charAt(current_position[1] + 1));
                                distance_counter++;
                            } catch (Exception StringIndexOutOfBounds) {
                                warped_string = String.format("%x", template.charAt(current_position[1] + 1));
                                distance_counter++;
                            }
                        }
                        else {
                            for (int a = current_position[0] + 1; a < i; a++) {
                                warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1])));
                                distance_counter++;
                            }
                            warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1] + 1)));
                            distance_counter += Math.sqrt(2);
                        }
                        current_position[0] = i;
                        current_position[1]++;
                        potential_paths++;
                        update_t_d_d(current_position, warped_string, distance_counter, template, sample, dissimilarities, distances);
                    }
                }
                if (potential_paths == 0) {
                    distance_counter += Math.sqrt(2);
                    warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1] + 1)));
                    current_position[0]++;
                    current_position[1]++;
                    update_t_d_d(current_position, warped_string, distance_counter, template, sample, dissimilarities, distances);
                }
            }
        }
    }

    public int generate_ai_choice() {
        String pattern = best_template.toString();
        String last_few_choices = (past_player_choices.toString()).substring((past_player_choices.toString()).length() - pattern.length());
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < pattern.length(); k++) {
            String temp = last_few_choices.substring(last_few_choices.length() - k);
            if ((pattern.indexOf(temp)) == 0) {
                choice = pattern.charAt(temp.length());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = pattern.charAt(0);
        }
        return choice;
    }
}