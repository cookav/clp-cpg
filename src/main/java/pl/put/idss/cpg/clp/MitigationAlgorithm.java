package pl.put.idss.cpg.clp;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import lombok.Data;
import pl.put.idss.cpg.clp.model.CombinedLogicalModel;
import pl.put.idss.cpg.clp.model.LogicalModel;
import pl.put.idss.cpg.clp.model.LogicalModelFactory;
import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;
import pl.put.idss.cpg.clp.operators.InteractionOperator;
import pl.put.idss.cpg.clp.operators.OperatorRepository;
import pl.put.idss.cpg.clp.operators.RevisionOperator;
import pl.put.idss.cpg.clp.solvers.Solver;
import pl.put.idss.cpg.clp.solvers.Solver.Solution;
import pl.put.idss.cpg.graph.ActionableGraph;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rits.cloning.Cloner;

@Data
public class MitigationAlgorithm {

    private final LogicalModelFactory lmFactory = new LogicalModelFactory();
    
    private final Solver solver;
    
    private final OperatorRepository<InteractionOperator> interactionOperatorRepository;
    
    private final OperatorRepository<RevisionOperator> revisionOperatorRepository;
    
    public Result mitigate(ActionableGraph ag1, ActionableGraph ag2,
            Set<VariableValue<?>> pi) {
        LogicalModel lm1 = lmFactory.createLogicalModel(ag1);
        LogicalModel lm2 = lmFactory.createLogicalModel(ag2);
        CombinedLogicalModel clm = new CombinedLogicalModel(Sets.newHashSet(lm1, lm2));
        if (!Sets.intersection(lm1.getVariables(), lm2.getVariables()).isEmpty()) {
            Solution solution = solver.solve(clm, pi);
            if (!solution.isSuccess()) {
                Result result = addressPSI(clm, pi, solution.getPotentialSourceOfInfeasibility(), false);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }

        applyInteractionOperators(clm);
        Solution solution = solver.solve(clm, pi);
        if (solution.isSuccess()) {
            return new Result(true, solution.getAssignment(), null, null);
        } else {
            return addressPSI(clm, pi, solution.getPotentialSourceOfInfeasibility(), true);
        }
    }

    private void applyInteractionOperators(CombinedLogicalModel clm) {
        List<InteractionOperator> operators = interactionOperatorRepository.getAll();
        Set<String> diseases = clm.getDiseases();
        Set<Variable<?>> variables = clm.getVariables();
        
        for (InteractionOperator io : operators) {
            if ((io.getDiseases().contains("*") || !Sets.intersection(io.getDiseases(), diseases).isEmpty())
                    && variables.containsAll(io.getVariables())) {
                clm.getAdverseInteractions().add(io.getInteraction());
            }
        }
    }
    
    private Result addressPSI(CombinedLogicalModel clm, Set<VariableValue<?>> pi, Set<Variable<?>> psi, boolean applyIOs) {
        List<RevisionOperator> operators = activateRevisionOperators(clm, psi);
        for (RevisionOperator ro : operators) {
            CombinedLogicalModel copy = new Cloner().deepClone(clm);
            if (reviseCLM(copy, ro, applyIOs)) {
                Solution solution = solver.solve(copy, pi);
                if (solution.isSuccess()) {
                    //TODO update CLM
                    return new Result(true, solution.getAssignment(), psi, ro);
                }
            }
        }
        return new Result(false, null, psi, null);
    }

    private boolean reviseCLM(CombinedLogicalModel clm, RevisionOperator ro, boolean applyIOs) {
        Set<LogicalModel> revised = Sets.newHashSet();
        for (LogicalModel model : clm.getModels()) {
            for (Set<VariableValue<?>> expression : model.getLogicalExpressions()) {
                if (expression.containsAll(ro.getInteraction())) {
                    expression.removeAll(ro.getInteraction());
                    expression.addAll(ro.getRevised());
                    revised.add(model);
                }
            }
        }
        
        boolean addedVariable = false;
        for (LogicalModel model : revised) {
            for (VariableValue<?> value : ro.getRevised()) {
                if (model.getVariables().add(value.getVariable()))  {
                    addedVariable = true;
                }
            }
        }
        if (applyIOs && addedVariable) {
            applyInteractionOperators(clm);
            //TODO revise again?
        }
        return !revised.isEmpty();
    }

    private List<RevisionOperator> activateRevisionOperators(CombinedLogicalModel clm,
            final Set<Variable<?>> psi) {
        List<RevisionOperator> operators = revisionOperatorRepository.getAll();
        Set<String> diseases = clm.getDiseases();
        
        List<RevisionOperator> active = Lists.newArrayList();
        for (RevisionOperator ro : operators) {
            if ((ro.getDiseases().contains("*") || !Sets.intersection(ro.getDiseases(), diseases).isEmpty())
                    && psi.containsAll(ro.getVariables())) {
                active.add(ro);
            }
        }
        
        Collections.sort(active, new Comparator<RevisionOperator>() {

            @Override
            public int compare(RevisionOperator o1, RevisionOperator o2) {
                int scopeDiff = Sets.difference(o1.getRevised(), o1.getInteraction()).size() 
                        - Sets.difference(o2.getRevised(), o2.getInteraction()).size();
                if (scopeDiff != 0) {
                    return scopeDiff;
                }
                return Sets.intersection(psi, o2.getVariables()).size()
                        - Sets.intersection(psi, o1.getVariables()).size();
            }
            
        });
        return active;
    }
    
    @Data
    public static class Result {
        
        private final boolean success;
        
        private final Set<VariableValue<?>> solution;
        
        private final Set<Variable<?>> potentialSourceOfInfeasibility;
        
        private final RevisionOperator revisionOperator;
        
    }
}
