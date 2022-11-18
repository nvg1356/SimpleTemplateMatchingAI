# SimpleTemplateMatchingAI
Simple 2-by-2 matrix game without perfect information involving a dynamic time warping algorithm and an evolutionary algorithm. 

## Details
### Instructions
In a single turn, players will choose a move (0 or 1) which is followed by the computer choosing a move (0 or 1).
If the player's move corresponds with the computer's move, the computer wins the turn. If the player's move differs 
from the computer's move, the player wins the turn. 
### How the algorithms works
Before the algorithm starts, it knows about the moves of the player in turns which have past. 
1. Based on the number of turns already past, the algorithm generates a small number of patterns that the player could have used. 
2. For each pattern, a dissimilarity metric which corresponds to the suitability of the pattern is calculated. (This step is computationally expensive so multithreading was implemented to reduce time spent.)
3. The most suitable patterns in the initial population are utilised to generate more patterns which could be more fitting.
4. Repeat step 2 and 3 for a certain number of times.
5. The most suitable pattern, among all which have been generated, will be utilised to base predictions of the player's moves.
6. If the current turn is in the middle of an iteration of the pattern, the player's move is predicted to be the next uniterated element of the pattern. Otherwise, assume that the pattern will begin next turn. 
### Reason for usage of template-matching algorithm
In the cognitive sciences, template matching is a eminent theory that describes how one identify patterns. The theory is as follows: when one
looks at a sequence, the sequence is compared against every pattern that one can think of. A better explanation can be found [here](https://cdn.intechopen.com/pdfs/5795/InTech-Theory_of_cognitive_pattern_recognition.pdf). Furthermore, template matching is used in other machine 
pattern recognition fields such as signal processing. A paper which expounds on this concept can be found [here](https://www.hindawi.com/journals/jam/2014/528071/).
### Reason for usage of evolutionary algorithm as a metaheuristic
I had previously used a template-matching algorithm by itself and it involved the generation and evaluation of all possible patterns without discrimination. The high computational cost and long processing times motivated me utilise a method that reduced the number of tested patterns while maintaining effectiveness of the algorithm.
### Limitations of algorithm
Success of the algorithm hinges on there being patterns to the player's moves.

