package dtwvariant;
import java.util.*;

public class generator {
    ArrayList<ArrayList<Integer>> template2 = new ArrayList<>();
    ArrayList<Integer> template1 = new ArrayList<>();
    base object = new base();

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(){
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = object.turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
        }
        return x;
    }

    public void generate_templates() {
        for (int j = 2; j <= max_seq_len(); j++){
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

    public double get_dissimilarity(ArrayList<Integer> warped_string, ArrayList<Integer> actual_string) {
        int differences = 0;
        for (int i = 0; i <= actual_string.size(); i++) {
            if (Objects.equals(warped_string.get(i), actual_string.get(i))) {
                differences++;
            }
        }
        return differences / actual_string.size();
    }
    public ArrayList<Integer> generate_top_template() {
        generate_templates();
        HashMap<Double, ArrayList<Integer>> ratings_templates = new HashMap<>();
        for (ArrayList<Integer> template_to_match: template2) {
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
        double lowest_rating = Collections.min(ratings_templates.keySet());
        return ratings_templates.get(lowest_rating);
    }

    public double get_array_sum(ArrayList<Double> some_array) {
        double sum = 0;
        for (double element : some_array) {
            sum += element;
        }
        return sum;
    }

    public ArrayList<Integer> subArrayList(ArrayList<Integer> original, int start_idx, int end_idx) {
        ArrayList<Integer> modified_array = new ArrayList<>();
        for (int i = start_idx; i < end_idx; i++) {
            modified_array.add(original.get(i));
        }
        return modified_array;
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
        ArrayList<Integer> pattern = generate_top_template();
        ArrayList<Integer> last_few_choices = subArrayList(object.past_player_choices, object.past_player_choices.size() - pattern.size(), object.past_player_choices.size());
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < pattern.size(); k++) {
            ArrayList<Integer> temp = subArrayList(last_few_choices, last_few_choices.size() - k, last_few_choices.size());
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