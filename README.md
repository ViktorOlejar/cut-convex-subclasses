# cut-convex-subclasses
This repository contains supplementary computations and the source code for these computations for the paper: "State complexity of the cut operation in subclasses of convex languages" submitted to CIAA 2022: 26th International Conference on Implementation and Application of Automata.

### automaton encoding explanation

Let us explain the automaton encoding used in these computations by providing an example. Consider the deterministic finite automaton(DFA) depicted in the figure below.

![alt text](https://brasil.cel.agh.edu.pl/~11sustrojny/wp-content/uploads/2011/05/automat.jpg)

The corresponding automaton encoding for the minimal DFA over a binary alphabet that recognizes the language where the second symbol has an even occurrence in every string is given by:

**0110TF**

Such DFA has two states (based on the length of the encoding). The part of the encoding "TF" indicates that the second state is non-final (False; F) while the first is final (True; T). The part of the encoding "0110" indicates that the transitions are given as:

 - *(0,"first symbol",0)*, 
 - *(0,"second symbol",1)*, 
 - *(1,"first symbol",1)*, 
 - *(1,"second symbol",0)*,
 
where the first and second symbol can be interpreted as *A* and *B* according to the figure. It also assigns nonnegative integers to identify each state, where in the figure S<sub>1</sub> and S<sub>2</sub> correspond to *0* and *1*, respectively. The initial state is always assigned *0* for this encoding.


### `computations` directory structure

```bash
├── cutOperationComputations
│   ├── AcutB
│   └── HomomorphicAcutB
├── structuredAutomataLists
│   ├── numOfStates2
│   ├── numOfStates3
│   ├── numOfStates4
│   └── numOfStates5
└── cutConvexTables.html
```


