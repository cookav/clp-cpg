set of int: Decision = 1..2;

int: n = 1;
int: p = 2;
array[Decision] of string: neg_pos = ["n","p"];

% CPG1

var bool: CPG1;

var Decision: D1;

var bool: A1;
var bool: A2;

% CPG2

var bool: CPG2;

var Decision: D2;

var bool: A3;
var bool: A4;

constraint CPG1 -> (D1 = n /\ A1 /\ not A2) 
                \/ (D1 = p /\ not A1 /\ A2)
%RO1            
                \/ (D1 = n /\ not A1 /\ not A2 /\ A4)
                ;

constraint CPG2 -> (D2 = n /\ A3 /\ not A4) 
                \/ (D2 = p /\ not A3 /\ A4)
%RO2            
                \/ (D2 = n /\ A3 /\ A4 /\ not A1)
                ;


% IO
constraint (CPG1 /\ CPG2) -> not (A1 /\ A3);

%RO
%RO1: A1 /\ not A2 -> not A1 /\ not A2 /\ A4
%RO2: A3 /\ not A4 -> not A3 /\ A4 /\ not A1

% PI
constraint CPG1;
constraint D1 = n;

constraint CPG2;
constraint D2 = n;

solve satisfy;

output [
"D1 = ", show(neg_pos[fix(D1)]), "\n",
"D2 = ", show(neg_pos[fix(D2)]), "\n",
"A1 = ", show(A1), "\n",
"A2 = ", show(A2), "\n",
"A3 = ", show(A3), "\n",
"A4 = ", show(A4), "\n",
];