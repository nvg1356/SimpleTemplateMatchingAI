package dtwvariant;
import java.util.*;

public class generator {
    // template3 refers to a collection of all pattern templates available at the current turn_number
    // template3[k] refers to the group of pattern templates with a pattern length of k + 2
    ArrayList<ArrayList<ArrayList<Integer>>> template3 = new ArrayList<ArrayList<ArrayList<Integer>>>();
    // template2 refers to a group of pattern templates for a particular pattern length
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<ArrayList<Integer>>();
    // template1 refers to an ordinary pattern template
    ArrayList<Integer> template1 = new ArrayList<Integer>();
    base object = new base();
    ArrayList<Integer> past_player_choices = object.past_player_choices;
    int turn_number = object.turnnumber;

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

    public double get_dissimilarity(String warped_string, String actual_string) {
        int differences = 0;
        for (int i = 0; i <= actual_string.length(); i++) {
            if (warped_string.charAt(i) == actual_string.charAt(i)) {
                differences++;
            }
        }
        double dissimilarity = differences / actual_string.length();
        return dissimilarity;
    }

    public String generate_top_template() {
        generate_templates();
        ArrayList<String> templates = new ArrayList<String>();
        ArrayList<Double> dissimilarities = new ArrayList<Double>();
        ArrayList<Double> distances = new ArrayList<Double>();

        for (int b = 0; b < template3.size(); b++) {
            for (int c = 0; c < (template3.get(b)).size(); c++) {
                String template_to_match = template3.get(b).get(c).toString();
                String sample = past_player_choices.toString();
                if (sample.length() > template_to_match.length()) {
                    ArrayList<String> sub_samples = new ArrayList<String>();
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        try {
                            sub_samples.add(sample.substring(i * template_to_match.length(), (i + 1) * template_to_match.length()));
                        } catch (Exception StringIndexOutOfBounds){
                            sub_samples.add(sample.substring(i * template_to_match.length(), sample.length()));
                            break;
                        }
                    }
                    ArrayList<Double> sub_dissimilarities = new ArrayList<Double>();
                    ArrayList<Double> sub_distances = new ArrayList<Double>();
                    for (int i = 0; i < sub_samples.size(); i++) {
                        double distance_counter = 0;
                        String warped_string = "";
                        int[] current_position = {0, 0};
                        update_t_d_d(current_position, warped_string, distance_counter, template_to_match, sub_samples.get(i), templates, sub_dissimilarities, sub_distances);
                    }
                    dissimilarities.add(get_array_sum(sub_dissimilarities) / sub_dissimilarities.size());
                    distances.add(get_array_sum(sub_distances));
                }
                else {
                    double distance_counter = 0;
                    String warped_string = "";
                    int[] current_position = {0, 0};
                    update_t_d_d(current_position, warped_string, distance_counter, template_to_match, sample, templates, dissimilarities, distances);
                }
            }
        }
        String best_template = "";
        double highest_rating = 1 / Integer.MAX_VALUE;
        for (int i = 0; i < templates.size(); i++) {
            double current_rating = 1 / (distances.get(i) + dissimilarities.get(i));
            if (current_rating > highest_rating) {
                highest_rating = current_rating;
                best_template = templates.get(i);
            }
        }
        return best_template;
    }

    public double get_array_sum(ArrayList<Double> some_array) {
        double sum = 0;
        for (int i = 0; i < some_array.size(); i++) {
            sum += some_array.get(i);
        }
        return sum;
    }

    public void update_t_d_d(int[] current_position, String warped_string, double distance_counter, String template, String sample, ArrayList<String> templates, ArrayList<Double> dissimilarities, ArrayList<Double> distances) {
        if ((current_position[0] == sample.length()) && (current_position[1] == template.length())) {
            templates.add(template);
            dissimilarities.add(get_dissimilarity(warped_string, sample));
            distances.add(distance_counter);
        }
        else {
            if (current_position[0] == sample.length() - 1) {
                distance_counter += template.length() - 1 - current_position[1];
                current_position[1] = template.length() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, templates, dissimilarities, distances);
            }
            if (current_position[1] == template.length() - 1) {
                distance_counter += sample.length() - 1 - current_position[0];
                for (int i = current_position[0] + 1; i <= sample.length() - 1; i++) {
                    warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1])));
                }
                current_position[0] = sample.length() - 1;
                update_t_d_d(current_position, warped_string, distance_counter, template, sample, templates, dissimilarities, distances);
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
                        update_t_d_d(current_position, warped_string, distance_counter, template, sample, templates, dissimilarities, distances);
                    }
                }
                if (potential_paths == 0) {
                    distance_counter += Math.sqrt(2);
                    warped_string = warped_string.concat(String.format("%x", template.charAt(current_position[1] + 1)));
                    current_position[0]++;
                    current_position[1]++;
                    update_t_d_d(current_position, warped_string, distance_counter, template, sample, templates, dissimilarities, distances);
                }
            }
        }
    }

    public int generate_ai_choice() {
        String pattern = generate_top_template();
        String last_few_choices = (past_player_choices.toString()).substring(((past_player_choices.toString()).length() - pattern.length()), ((past_player_choices.toString()).length()));
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < pattern.length(); k++) {
            String temp = last_few_choices.substring(last_few_choices.length() - k, last_few_choices.length());
            if ((pattern.indexOf(temp)) == 0) {
                choice = generate_top_template().charAt(temp.length());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = generate_top_template().charAt(0);
        }
        return choice;
    }
}