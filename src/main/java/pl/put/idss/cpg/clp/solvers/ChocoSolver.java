package pl.put.idss.cpg.clp.solvers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pl.put.idss.cpg.clp.model.CombinedLogicalModel;
import pl.put.idss.cpg.clp.model.LogicalModel;
import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;
import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ChocoSolver implements pl.put.idss.cpg.clp.solvers.Solver {

    @Override
    public Solution solve(CombinedLogicalModel clm, Set<VariableValue<?>> patientInformation) {
        Set<Variable<?>> variables = clm.getVariables();
        
        CPModel cpModel = new CPModel();

        HashMap<Variable<?>, IntegerVariable> map = Maps.newHashMap();
        for (Variable<?> variable : variables) {
            IntegerVariable var = Choco.makeIntVar(
                    variable.getName(), 0, variable.getDomain().size() - 1);
            cpModel.addVariable(var);
            map.put(variable, var);
        }
        
        HashMap<IntegerExpressionVariable,Set<Variable<?>>> constraintVariables = Maps.newHashMap();
        
        for (LogicalModel model : clm.getModels()) {
            IntegerVariable var = createDisjunction(model.getDisease(), model.getLogicalExpressions(), cpModel, map);
            constraintVariables.put(Choco.mult(10, var), model.getVariables());
        }
        
        int i = 0;
        for (Set<VariableValue<?>> interaction : clm.getAdverseInteractions()) {
            IntegerVariable var = createConjunction("IO-" + i++, interaction, cpModel, map);
            IntegerVariable io = Choco.makeBooleanVar("not-" + var.getName());
            cpModel.addConstraint(Choco.boolChanneling(io, var, 0));
            constraintVariables.put(Choco.mult(1, io), getVariables(interaction));
        }
        
        IntegerVariable pi = createConjunction("patientInformation", patientInformation, cpModel, map);
        constraintVariables.put(Choco.mult(100, pi), getVariables(patientInformation));
        
        IntegerVariable constraintsSum = Choco.makeIntVar("maxCSP", 0, 1000);
        cpModel.addConstraint(Choco.eq(constraintsSum, 
                Choco.sum(constraintVariables.keySet().toArray(new IntegerExpressionVariable[0]))));
        
        CPSolver cpSolver = new CPSolver();
        cpSolver.read(cpModel);
        
        cpSolver.maximize(cpSolver.getVar(constraintsSum), false);
        
        if (cpSolver.getVar(constraintsSum).getVal() == 100 
                + 10 * clm.getModels().size() 
                + 1 * clm.getAdverseInteractions().size()) {
            Set<VariableValue<?>> values = Sets.newHashSet();
            for (Entry<Variable<?>, IntegerVariable> entry : map.entrySet()) {
                IntDomainVar cpVar = cpSolver.getVar(entry.getValue());
                values.add(createValue(entry.getKey(), cpVar.getVal()));
            }
            return new Solution(true, values, null);
        } else {
            Set<Variable<?>> psi = Sets.newHashSet();
            for (Entry<IntegerExpressionVariable, Set<Variable<?>>> entry : constraintVariables.entrySet()) {
                IntegerExpressionVariable expr = entry.getKey();
                if (cpSolver.getVar((IntegerVariable)expr.getVariable(0)).getVal() == 0) {
                    psi.addAll(entry.getValue());
                }
            }
            return new Solution(false, null, psi);
        }
    }

    private IntegerVariable createDisjunction(String name,
            Set<Set<VariableValue<?>>> expressions, CPModel cpModel,
            Map<Variable<?>, IntegerVariable> variables) {
        IntegerVariable[] literals = new IntegerVariable[expressions.size()];
        Iterator<Set<VariableValue<?>>> iterator = expressions.iterator();
        for (int i = 0; i < literals.length; i++) {
            literals[i] = createConjunction(name + "-path-" + i, iterator.next(), cpModel, variables);
        }
        IntegerVariable var = Choco.makeBooleanVar(name);
        cpModel.addConstraint(Choco.reifiedOr(var, literals));
        return var;
    }
    
    private IntegerVariable createConjunction(String name,
            Set<VariableValue<?>> expression, CPModel cpModel, Map<Variable<?>, IntegerVariable> variables) {
        Iterator<VariableValue<?>> iterator = expression.iterator();
        IntegerVariable[] literals = new IntegerVariable[expression.size()];
        for (int j = 0; j < literals.length; j++) {
            literals[j] = createConstraint(name, iterator.next(), cpModel, variables);
        }
        IntegerVariable var = Choco.makeBooleanVar(name);
        cpModel.addConstraint(Choco.reifiedAnd(var, literals));
        return var;
    }

    private IntegerVariable createConstraint(String name, VariableValue<?> value,
            CPModel cpModel, Map<Variable<?>, IntegerVariable> variables) {
        Variable<?> variable = value.getVariable();
        List<?> domain = value.getVariable().getDomain();
        int index = domain.indexOf(value.getValue());
        IntegerVariable var = Choco.makeBooleanVar(name + "-" + variable.getName());
        cpModel.addConstraint(Choco.boolChanneling(var, variables.get(variable), index));
        return var;
    }

    private static <T> VariableValue<T> createValue(Variable<T> var, int val) {
        return var.assignValue(var.getDomain().get(val));
    }

    private static Set<Variable<?>> getVariables(Set<VariableValue<?>> interaction) {
        Set<Variable<?>> vars = Sets.newHashSet();
        for (VariableValue<?> value : interaction) {
            vars.add(value.getVariable());
        }
        return vars;
    }

}
