package pl.put.idss.cpg.clp.solvers;

import java.util.Set;

import lombok.Data;

import pl.put.idss.cpg.clp.model.CombinedLogicalModel;
import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;

public interface Solver {

    Solution solve(CombinedLogicalModel clm, Set<VariableValue<?>> patientInformation);

    @Data
    public static class Solution {
        
        private final boolean success;
        
        private final Set<VariableValue<?>> assignment;
        
        private final Set<Variable<?>> potentialSourceOfInfeasibility;
    }
}
