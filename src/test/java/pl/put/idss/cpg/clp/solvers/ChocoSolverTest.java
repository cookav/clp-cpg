package pl.put.idss.cpg.clp.solvers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import pl.put.idss.cpg.Examples;
import pl.put.idss.cpg.clp.model.ActionVariable;
import pl.put.idss.cpg.clp.model.CombinedLogicalModel;
import pl.put.idss.cpg.clp.model.DecisionVariable;
import pl.put.idss.cpg.clp.model.LogicalModel;
import pl.put.idss.cpg.clp.model.LogicalModelFactory;
import pl.put.idss.cpg.clp.model.VariableValue;
import pl.put.idss.cpg.clp.solvers.Solver.Solution;
import pl.put.idss.cpg.graph.ActionableGraph;

import com.google.common.collect.Sets;

public class ChocoSolverTest {

    private ChocoSolver solver;
    
    @Before
    public void setUp() throws Exception {
        solver = new ChocoSolver();
    }

    @Test
    public void testSolveDVT() {
        ActionableGraph graph = Examples.createDVTGraph();
        LogicalModel lm = new LogicalModelFactory().createLogicalModel(graph);
        CombinedLogicalModel clm = new CombinedLogicalModel(Sets.newHashSet(lm));
        
        DecisionVariable sbt = (DecisionVariable) lm.getVariable("SBT");
        HashSet<VariableValue<?>> patientInformation = Sets.<VariableValue<?>>newHashSet(sbt.assignValue("p"));
        Solution solution = solver.solve(clm, patientInformation);
        assertThat(solution.isSuccess(), equalTo(true));
        assertThat(solution.getAssignment(), hasItem(sbt.assignValue("p")));
        assertThat(solution.getAssignment(), hasItem(new ActionVariable("IVCF").assignValue(true)));
        assertThat(solution.getAssignment(), hasItem(new ActionVariable("ACA").assignValue(false)));
        assertThat(solution.getAssignment(), hasItem(new ActionVariable("WA").assignValue(false)));
    }

}
