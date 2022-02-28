# cut-convex-subclasses
This repository contains supplementary computations and the source code for these computations for the publication: *"State complexity of the cut operation in subclasses of convex languages"* submitted to CIAA 2022: 26th International Conference on Implementation and Application of Automata. In order to be be familiar with the definitions and terminology used here, we recommend the reader to read the publication before proceeding.

## automaton encoding explanation

Let us explain the automaton encoding used in these computations by providing an example. Consider the deterministic finite automaton (DFA) depicted in the figure below:

![alt text](https://brasil.cel.agh.edu.pl/~11sustrojny/wp-content/uploads/2011/05/automat.jpg)

(Source: *brasil.cel.agh.edu.pl/~11sustrojny/wp-content/uploads/2011/05/automat.jpg*)

The corresponding automaton encoding for the minimal DFA over a binary alphabet that recognizes the language where the second symbol has an even occurrence in every string is given by:

**0110TF**

Such DFA has two states (based on the length of the encoding). The part of the encoding "TF" indicates that the second state is non-final (False; F) while the first is final (True; T). The part of the encoding "0110" indicates that the transitions are given as:

 - *(0,"first symbol",0)*, 
 - *(0,"second symbol",1)*, 
 - *(1,"first symbol",1)*, 
 - *(1,"second symbol",0)*,
 
where the first and second symbol can be interpreted as *A* and *B* according to the figure. It also assigns nonnegative integers to identify each state, where in the figure S<sub>1</sub> and S<sub>2</sub> correspond to *0* and *1*, respectively. The initial state is always assigned *0* for this encoding.


## `computations` directory structure

The `computations` folder contains files related to the performed computations. The structure is shown below as a tree:

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

The considered convex subclasses are abbreviated as follows:

| Abbreviation |   Subclass       |
| ------------ | ---------------- |
| LID          |  left ideal      |
| RID          |  right ideal     |
| TSID         |  two-sided ideal |
| ASID         |  all-sided ideal |
| PF           |  prefix-free     |
| SF           |  suffix-free     |
| FF           |  factor-free     |
| SwF          |  subword free    |
| PC           |  prefix-closed   |
| SC           |  suffix-closed   |
| FC           |  factor-closed   |
| SwC          |  subword-closed  |

### directory `structuredAutomataLists`
	
Contains lists of all minimal DFAs up to isomorphism over binary alphabets organized according to number of states and accepted language subclass membership. Automata are given in the encoding format presented above. 


### directory `cutOperationComputations`

Contains cut operation computations organized according to the corresponding subclasses tested and according to the number of states for tested automata. Each appropriate subdirectory contains histograms for the corresponding cut automaton, *.csv* files containing the data for histograms and *"hardestWitnesses.txt"* files containing automata combinations resulting in the highest state complexity for the corresponding subclasses and number of states. For completeness of computations, directory `HomomorphicAcutB` contains computations where we swap the alphabet symbols of the first automaton and subsequently apply the cut operation.

### HTML file `cutConvexTables`

Contains a succinct overview of the results obtained in `cutOperationComputations` in HTML table format. It can be previewed at the following [link]( http://htmlpreview.github.io/?https://github.com/ViktorOlejar/cut-convex-subclasses/blob/main/computations/cutConvexTables.html).

## LICENSE

