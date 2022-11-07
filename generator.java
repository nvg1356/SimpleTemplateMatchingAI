package twochoicegame;
import java.util.*;

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

    //Assumptions are that patterns lasting more than ten moves are impossible,

    public int max_seq_len(int turnnumber){
        int x = 11;
        for (int i = 2; i < 11; i++) {
            float quotient = turnnumber / i;
            if (quotient < 3) {
                x = i;
            }
            }
        return x;
        }

    public void generate_templates() {
        for (int j = 2; j <= max_seq_len(turnnumber); j++){
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

    public int get_hits(ArrayList<Integer> past_player_choices, ArrayList<Integer> template_to_match) {
        String past_player_choices_string = past_player_choices.toString();
        String template_to_match_string = template_to_match.toString();
        int number_of_hits = 0, last_known_idx = -2;
        for (int idx = 0; idx < past_player_choices_string.length(); idx++) {
            if ((last_known_idx != past_player_choices_string.indexOf(template_to_match_string, idx)) && (last_known_idx != -1)) {
                number_of_hits++;
                last_known_idx = past_player_choices_string.indexOf(template_to_match_string, idx);
            }
        }
        return number_of_hits;
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

    public ArrayList<Integer> generate_best_template(ArrayList<Integer> past_player_choices) {
        generate_templates();
        float best_rating = 0 + (1 / Integer.MAX_VALUE);
        ArrayList<Integer> best_template = new ArrayList<Integer>();
        for (int b = 0; b < template3.size(); b++){
            for (int c = 0; c < (template3.get(b)).size(); c++){
                ArrayList<Integer> template_to_match = template3.get(b).get(c);
                float hits = get_hits_and_variance(past_player_choices, template_to_match).get(0);
                float lag_variance = get_hits_and_variance(past_player_choices, template_to_match).get(1);
                float rating = hits + (1 / lag_variance);
                if (best_rating < rating) {
                    best_rating = rating;
                    best_template = template_to_match;
                }
            }
        }
        return best_template;
    }

    // Possible improvements to generate_best_template(): use variance of lags between pattern occurrences as criteria

    public int generate_ai_choice() {
        String pattern = generate_best_template(past_player_choices).toString();
        String last_few_choices = (past_player_choices.toString()).substring(((past_player_choices.toString()).length() - pattern.length()),
                ((past_player_choices.toString()).length()));
        int choice = 0;
        // if player is currently engaged in a pattern, assume that player will continue with the pattern this turn
        for (int k = 2; k < pattern.length(); k++) {
            String temp = last_few_choices.substring(last_few_choices.length() - k, last_few_choices.length());
            if ((pattern.indexOf(temp)) == 0) {
                choice = generate_best_template(past_player_choices).get(temp.length());
            }
        }
        //if player is not currently engaged in a pattern, assume that the pattern begins this turn.
        if (choice == 0) {
            choice = generate_best_template(past_player_choices).get(0);
        }
        return choice;
    }
}
