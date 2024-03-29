package threadedregvariant;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

public class base {
    final int[] possible_choices = {1, 0};
    static ArrayList<Integer> past_player_choices = new ArrayList<Integer>();
    int player_wins = 0;
    int ai_wins = 0;
    generator predictor = new generator();
    static int turnnumber = 0;
    int accumulated_ai_losses = 0;
    static SecureRandom random = new SecureRandom();

    static void introduction(){
        System.out.println("This game involves the player keying in a boolean input of either 1 or 0" +
                "/n and the computer choosing between one of the two as well. The computer scores a point when" +
                "/n it chooses an input matching that of the player and the player gains a point when the inputs" +
                "/n differ. A turn is defined as a single move by the player followed by a single move by the " +
                "/n computer. The only information made known to the computer is the choices made by the player " +
                "/n in past moves. The player's choice in the current move is not included. Using the " +
                "/n multi-armed-bandit algorithm, the computer predicts the choice of the player in the " +
                "/n current move.");

    }

    public static int get_input(){
        System.out.println("Please enter 1 or 0.");
        Scanner myObj = new Scanner(System.in);
        int player_choice = myObj.nextInt();
        switch(player_choice){
            case 1:
                System.out.println("Your choice is 1.");
            case 0:
                System.out.println("Your choice is 0.");
            default:
                System.out.println("Your choice is invalid.");
                get_input();
        return player_choice;
        }
    }

    // turn 1 is used for first ten turns
    public void turn(int player_choice, boolean restrat){
        int ai_choice = 0;
        if (restrat) {
            ai_choice = predictor.generate_ai_choice();
        }
        else {
            ai_choice = possible_choices[random.nextInt(possible_choices.length)];
        }
        past_player_choices.add(player_choice);
        if (ai_choice == player_choice){
            ai_wins++;
            accumulated_ai_losses = 0;
            System.out.println("Computer Wins!");
        }
        else{
            player_wins++;
            accumulated_ai_losses++;
            System.out.println("You win!");
        }
    }

    public void main(String[] args){
        introduction();
        while(turnnumber < 10){
            int player_choice = get_input();
            turn(player_choice, false);
            turnnumber++;
        }
        while(turnnumber > 10) {
            int player_choice = get_input();
            if (turnnumber == 10) {
                turn(player_choice, true);
            }
            else {
                if (accumulated_ai_losses >= 3) {
                    turn(player_choice, true);
                }
                else {
                    turn(player_choice, false);
                }
            }
            turnnumber++;
        }
    }

}
