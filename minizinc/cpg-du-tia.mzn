set of int: Decision = 1..2;

int: n = 1;
int: p = 2;
array[Decision] of string: neg_pos = ["n","p"];

% DU

int: h = 1;
int: nh = 2;
array[Decision] of string: h_nh = ["h","nh"];

var bool: DU;

var Decision: HP;
var Decision: ZES;
var Decision: UE;

var bool: ET;
var bool: PPI;
var bool: SC;
var bool: RS;

constraint DU -> (HP = p /\ ET /\ PPI /\ UE = h /\ SC /\ not RS) 
              \/ (HP = p /\ ET /\ PPI /\ UE = nh /\ not SC /\ RS)
              \/ (HP = n /\ ZES = n /\ PPI /\ UE = h /\ SC /\ not RS /\ not ET)
              \/ (HP = n /\ ZES = n /\ PPI /\ UE = nh /\ not SC /\ RS /\ not ET)
              \/ (HP = n /\ ZES = p /\ RS /\ not SC /\  not ET /\ not PPI);


% TIA

int: r = 1;
int: nr = 2;
array[Decision] of string: r_nr = ["r","nr"];

int: ng = 1;
int: el = 2;
array[Decision] of string: ng_el = ["ng","el"];

var bool: TIA;

var Decision: HG;
var Decision: FAST;
var Decision: NS;
var Decision: RST;

var bool: EC;
var bool: TST;
var bool: A;
var bool: PCS;
var bool: D;
var bool: NC;

var bool: CL;

constraint TIA -> ((HG = p) /\ EC /\ not A /\ not TST/\ not PCS /\ not D /\ not NC) 
               \/ ((HG = n) /\ (FAST = p) /\ (NS = nr) /\ TST /\ NC /\ not EC /\ not A /\ not PCS /\ not D ) 
               \/ ((HG = n) /\ (FAST = p) /\ (NS = r) /\ A /\ (RST = el) /\ D /\ NC /\ not TST /\ not EC /\ not PCS) 
               \/ ((HG = n) /\ (FAST = p) /\ (NS = r) /\ A /\ (RST = ng) /\ PCS /\ not D /\ not NC /\ not TST /\ not EC) 
               \/ ((HG = n) /\ (FAST = n) /\ PCS /\ not TST /\ not NC /\ not EC /\ not A /\ not D)
% RO1               
               \/ ((HG = n) /\ (FAST = p) /\ (NS = r) /\ not A /\ (RST = ng) /\ PCS /\ not D /\ not NC /\ not TST /\ not EC /\ CL) 
% RO2               
               \/ ((HG = n) /\ (FAST = p) /\ (NS = r) /\ A /\ (RST = el) /\ D /\ NC /\ not TST /\ not EC /\ not PCS /\ PPI) 
               ;




% IO
constraint DU /\ TIA -> not (A /\ not PPI);

% RO1: A /\ not D -> not A /\ not D /\ CL
% RO2: A /\ D -> A /\ D /\ PPI


constraint DU;
constraint HP = n;
constraint ZES = p;

constraint TIA;
constraint HG == n;
constraint FAST == p;
constraint NS == r;

solve satisfy;

output [
"PPI = ", show(PPI), "\n",
"A = ", show(A), "\n",
"D = ", show(D), "\n",
"CL = ", show(CL), "\n",
"RST = ", show(ng_el[fix(RST)]), "\n",
"UE = ", show(h_nh[fix(UE)]), "\n",
];