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
import choco.kernel.model.constraints.Constraint;
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
        
        for (LogicalModel model : clm.getModels()) {
            cpModel.addConstraint(createDisjunction(model.getLogicalExpressions(), map));
        }
        
        for (Set<VariableValue<?>> interaction : clm.getAdverseInteractions()) {
            cpModel.addConstraint(Choco.not(createConjunction(interaction, map)));
        }
        
        
        cpModel.addConstraint(createConjunction(patientInformation, map));
        
        CPSolver cpSolver = new CPSolver();
        cpSolver.read(cpModel);
        
        if (cpSolver.solve()) {
            Set<VariableValue<?>> values = Sets.newHashSet();
            for (Entry<Variable<?>, IntegerVariable> entry : map.entrySet()) {
                IntDomainVar cpVar = cpSolver.getVar(entry.getValue());
                values.add(createValue(entry.getKey(), cpVar.getVal()));
            }
            return new Solution(true, values, null);
        } else {
            // TODO PSI
            return new Solution(false, null, variables);
        }
    }

    private Constraint createDisjunction(
            Set<Set<VariableValue<?>>> expressions,
            Map<Variable<?>, IntegerVariable> variables) {
        Constraint[] constraints = new Constraint[expressions.size()];
        Iterator<Set<VariableValue<?>>> iterator = expressions.iterator();
        for (int i = 0; i < constraints.length; i++) {
            constraints[i] = createConjunction(iterator.next(), variables);
        }
        return Choco.or(constraints);
    }

    private Constraint createConjunction(Set<VariableValue<?>> expression,
            Map<Variable<?>, IntegerVariable> variables) {
        Iterator<VariableValue<?>> iterator = expression.iterator();
        Constraint[] literals = new Constraint[expression.size()];
        for (int j = 0; j < literals.length; j++) {
            literals[j] = createConstraint(iterator.next(), variables);
        }
        return Choco.and(literals);
    }

    private Constraint createConstraint(VariableValue<?> value,
            Map<Variable<?>, IntegerVariable> variables) {
        Variable<?> variable = value.getVariable();
        List<?> domain = value.getVariable().getDomain();
        int index = domain.indexOf(value.getValue());
        return Choco.eq(variables.get(variable), index);
    }
    
    private static <T> VariableValue<T> createValue(Variable<T> var, int val) {
        return var.assignValue(var.getDomain().get(val));
    }

}
