# SimpleTemplateMatchingAI
Simple 2-by-2 matrix game without perfect information involving a dyanmic time warping algorithm. **IMPLEMENTED MULTITHREADING**

## Details
### Instructions
In a single turn, players will choose a move (0 or 1) which is followed by the computer choosing a move (0 or 1).
If the player's move corresponds with the computer's move, the computer wins the turn. If the player's move differs 
from the computer's move, the player wins the turn. 
### How the algorithm works
Before the algorithm starts, it knows about the moves of the player in turns which have past. 
1. Based on the number of turns already past, the algorithm generates a list of every possible pattern that the player can use. 
2. For every possible pattern, a dissimilarity metric which corresponds to the suitability of the pattern is calculated. 
3. The pattern with the lowest metric value is utilised to base predictions of the player's move
4. If the current turn is in the middle of an iteration of the pattern, the player's move is predicted to be the next uniterated element of the pattern. Otherwise, assume that the pattern will begin next turn. 
### Reason for algorithm choice
In the cognitive sciences, template matching is a eminent theory that describes how one identify patterns. The theory is as follows: when one
looks at a sequence, the sequence is compared against every pattern that one can think of. A better explanation can be found [here](https://cdn.intechopen.com/pdfs/5795/InTech-Theory_of_cognitive_pattern_recognition.pdf). Furthermore, template matching is used in other machine 
pattern recognition fields such as signal processing. A paper which expounds on this concept can be found [here](https://www.hindawi.com/journals/jam/2014/528071/).
### Limitations of algorithm
Success of the algorithm hinges on there being patterns to the player's moves. The algorithm is undeniably a brute force approach,
resulting in a high computational cost. 

