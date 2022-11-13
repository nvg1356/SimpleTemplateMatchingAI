package geneticreg;
import java.util.*;
import java.security.SecureRandom;

public class generator {
    ArrayList<Integer> hits = new ArrayList<Integer>();
    // template3 refers to a collection of all pattern templates available at the current turnnumber
    // template3[k] refers to the group of pattern templates with a pattern length of k + 2
    ArrayList<ArrayList<ArrayList<Integer>>> template3 = new ArrayList<ArrayList<ArrayList<Integer>>>();
    // template2 refers to a group of pattern templates for a particular pattern length
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<ArrayList<Integer>>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<Integer>();
    base object = new base();
    ArrayList<Integer> past_player_choices = object.past_player_choices;
    int turnnumber = object.turnnumber;
    ArrayList<Integer> pattern_lags = new ArrayList<Integer>();
    ArrayList<Integer> best_template = new ArrayList<Integer>();

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(int turnnumber){ // Time Complexity: O(1)
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
    }

    public void generate_all_templates() { // Time Complexity: O()
        for (int j = 2; j <= max_seq_len(turnnumber); j++) {
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
        SecureRandom random = new SecureRandom();
        ArrayList<Integer> samples_for_each_patt_length = new ArrayList<Integer>();
        for (int i = 0; i < template3.size(); i++) {
            float x = number_of_samples / template3.size();
            if ((i % 2) == 0) {
               x += 0.5 ;
            }
            samples_for_each_patt_length.add(Math.round(x));
        }
        for (ArrayList<ArrayList<Integer>> arrayLists : template3) {
            int i = 0;
            if (samples_for_each_patt_length.get(i) > arrayLists.size()) {
                samples_for_each_patt_length.set(i + 1, samples_for_each_patt_length.get(i + 1) + samples_for_each_patt_length.get(i) - arrayLists.size());
                samples_for_each_patt_length.set(i + 1, arrayLists.size());
            }
            for (int k = 0; k < samples_for_each_patt_length.get(i); k++) {
                initial_population.add(arrayLists.get(random.nextInt(0, arrayLists.size())));
            }
            i++;
        }
        return initial_population;
    }

    public HashMap<Integer, Float> get_hits_and_variance(ArrayList<Integer> past_player_choices, ArrayList<Integer> template_to_match) {
        String past_player_choices_string = past_player_choices.toString();
        String template_to_match_string = template_to_match.toString();
        float number_of_hits = 0;
        int last_known_idx = -2;
        ArrayList<Integer> variance_list = new ArrayList<Integer>();
        HashMap<Integer, Float> map = new HashMap<Integer, Float>();
        for (int idx = 0; idx < past_player_choices_string.length(); idx++) {
            boolean check = ((past_player_choices_string.indexOf(template_to_match_string, idx) >= last_known_idx) && (past_player_choices_string.indexOf(template_to_match_string, idx) != -1));
            if (check) {
                last_known_idx = past_player_choices_string.indexOf(template_to_match_string, idx);
                number_of_hits++;
                variance_list.add(past_player_choices_string.indexOf(template_to_match_string, idx) + template_to_match_string.length() - last_known_idx);
            }
        }
        float variance = get_array_variance(variance_list);
        map.put(0, number_of_hits);
        map.put(1, variance);
        return map;
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

    public void generate_best_template(ArrayList<Integer> past_player_choices, int initial_pop_size) {
        generate_all_templates();
        ArrayList<ArrayList<Integer>> initial_population = get_initial_population(initial_pop_size);
        sub_generation(initial_population);
    }

    public void sub_generation(ArrayList<ArrayList<Integer>> initial_population) {
        if (initial_population.size() == 1) {
            best_template = initial_population.get(0);
        }
        else {
            ArrayList<ArrayList<Integer>> templates_for_mutation = new ArrayList<ArrayList<Integer>>();
            ArrayList<Float> ratings_array = new ArrayList<Float>();
            for (ArrayList<Integer> template_to_match : initial_population){
                float hits = get_hits_and_variance(past_player_choices, template_to_match).get(0);
                float lag_variance = get_hits_and_variance(past_player_choices, template_to_match).get(1);
                float rating = hits + (1 / lag_variance);
                ratings_array.add(rating);
            }
            //criteria to get top 20% of initial_population into templates_for_mutation
            get_mutated_population(templates_for_mutation);
        }
    }

    public void get_mutated_population (ArrayList<ArrayList<Integer>> templates_for_mutation) {
        //some algorithm to generate mutated population
        sub_generation(mutated_population);
    }

    public int generate_ai_choice(int initial_pop_size) { //needs cleaning
        String pattern = best_template.toString();
        String last_few_choices = (past_player_choices.toString()).substring(((past_player_choices.toString()).length() - pattern.length()),
                ((past_player_choices.toString()).length()));
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < best_template.size(); k++) {
            String temp = last_few_choices.substring(last_few_choices.length() - k, last_few_choices.length());
            if ((pattern.indexOf(temp)) == 0) {
                choice = best_template.get(temp.length());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = best_template.get(0);
        }
        return choice;
    }
}
