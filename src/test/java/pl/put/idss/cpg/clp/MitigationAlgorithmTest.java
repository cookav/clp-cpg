package pl.put.idss.cpg.clp;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.put.idss.cpg.Examples;
import pl.put.idss.cpg.clp.MitigationAlgorithm.Result;
import pl.put.idss.cpg.clp.model.ActionVariable;
import pl.put.idss.cpg.clp.model.DecisionVariable;
import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;
import pl.put.idss.cpg.clp.operators.InteractionOperator;
import pl.put.idss.cpg.clp.operators.OperatorRepository;
import pl.put.idss.cpg.clp.operators.RevisionOperator;
import pl.put.idss.cpg.clp.solvers.ChocoSolver;
import pl.put.idss.cpg.graph.ActionableGraph;

import com.google.common.collect.Sets;

public class MitigationAlgorithmTest {

    private MitigationAlgorithm algorithm;
    
    @Mock
    private OperatorRepository<InteractionOperator> interactionRepository;
    
    @Mock
    private OperatorRepository<RevisionOperator> revisionRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        algorithm = new MitigationAlgorithm(new ChocoSolver(), interactionRepository, revisionRepository);
    }
    
    @Test
    public void testMitigate() {
        Result result = mitigateDVTandUHTN();
        
        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSolution(), not(empty()));
    }
    
    @Test
    public void testMitigateIO() {
        when(interactionRepository.getAll()).thenReturn(Arrays.asList(Examples.createWAInteractionOperator()));
       
        Result result = mitigateDVTandUHTN();
        
        assertThat(result.isSuccess(), equalTo(false));
        assertThat(result.getSolution(), nullValue());
        assertThat(result.getPotentialSourceOfInfeasibility(), not(empty()));
    }

    @Test
    public void testMitigateIOandRO() {
        when(interactionRepository.getAll()).thenReturn(Arrays.asList(Examples.createWAInteractionOperator()));
        when(revisionRepository.getAll()).thenReturn(Arrays.asList(Examples.createWARevisionOperator()));
       
        Result result = mitigateDVTandUHTN();
        
        assertThat(result.isSuccess(), equalTo(true));
        assertThat(result.getSolution(), not(empty()));
        assertThat(result.getRevisionOperator(), equalTo(Examples.createWARevisionOperator()));
    }

    private Result mitigateDVTandUHTN() {
        ActionableGraph dvt = Examples.createDVTGraph();
        ActionableGraph uhtn = Examples.createUHTNGraph();
        
        DecisionVariable tuhtn = new DecisionVariable("TUHTN", Arrays.asList("em","ur"));
        DecisionVariable sbt = new DecisionVariable("SBT", Arrays.asList("a","p"));
        DecisionVariable hit = new DecisionVariable("HIT", Arrays.asList("a","p"));
        HashSet<VariableValue<?>> patient = Sets.<VariableValue<?>>newHashSet(
                tuhtn.assignValue("ur"), sbt.assignValue("a"), hit.assignValue("a"));
        
        return algorithm.mitigate(dvt, uhtn, patient);
    }
    
    @Test
    public void testClinicalScenario1() {
        ActionableGraph du = Examples.createDUGraph();
        ActionableGraph tia = Examples.createTIAGraph();
        
        when(interactionRepository.getAll()).thenReturn(Arrays.asList(Examples.createAspirinInteractionOperator()));
        when(revisionRepository.getAll()).thenReturn(Arrays.asList(
                Examples.createClopidogrelRevisionOperator(),
                Examples.createPPIRevisionOperator()
                ));
        
        DecisionVariable hp = new DecisionVariable("HP", Arrays.asList("n","p"));
        ActionVariable et = new ActionVariable("ET");
        DecisionVariable hg = new DecisionVariable("HG", Arrays.asList("n","p"));
        DecisionVariable fast = new DecisionVariable("FAST", Arrays.asList("n","p"));
        HashSet<VariableValue<?>> patient = Sets.<VariableValue<?>>newHashSet(
                hp.assignValue("p"), 
                et.assignValue(true), 
                hg.assignValue("n"), 
                fast.assignValue("n")
                );
        Result result = algorithm.mitigate(du, tia, patient);
        
        assertThat(result.isSuccess(), equalTo(true));
        
        Set<VariableValue<?>> solution = result.getSolution();
        assertThat(solution, hasSize(8));
        assertThat(solution, hasItems(patient.toArray(new VariableValue<?>[0])));
        assertThat(solution, hasItem(new ActionVariable("PPI").assignValue(true)));
        assertThat(solution, hasItem(new ActionVariable("SC").assignValue(true)));
        assertThat(solution, hasItem(new ActionVariable("PCS").assignValue(true)));
        assertThat(solution, hasItem(new DecisionVariable("UE",Arrays.asList("h","nh")).assignValue("h")));
    }
    
    @Test
    public void testClinicalScenario2() {
        ActionableGraph du = Examples.createDUGraph();
        ActionableGraph tia = Examples.createTIAGraph();
        
        when(interactionRepository.getAll()).thenReturn(Arrays.asList(Examples.createAspirinInteractionOperator()));
        when(revisionRepository.getAll()).thenReturn(Arrays.asList(
                Examples.createClopidogrelRevisionOperator(),
                Examples.createPPIRevisionOperator()
                ));
       
        DecisionVariable hp = new DecisionVariable("HP", Arrays.asList("n","p"));
        DecisionVariable zes = new DecisionVariable("ZES", Arrays.asList("n","p"));
        DecisionVariable hg = new DecisionVariable("HG", Arrays.asList("n","p"));
        DecisionVariable fast = new DecisionVariable("FAST", Arrays.asList("n","p"));
        DecisionVariable ns = new DecisionVariable("NS", Arrays.asList("nr","r"));
        HashSet<VariableValue<?>> patient = Sets.<VariableValue<?>>newHashSet(
                hp.assignValue("n"), 
                zes.assignValue("p"), 
                hg.assignValue("n"), 
                fast.assignValue("p"),
                ns.assignValue("r")
                );
        
        Result result = algorithm.mitigate(du, tia, patient);

        assertThat(result.isSuccess(), equalTo(true));
        
        Set<VariableValue<?>> solution = result.getSolution();
        assertThat(solution, hasSize(9));
        assertThat(solution, hasItems(patient.toArray(new VariableValue<?>[0])));
        assertThat(solution, hasItem(new ActionVariable("RS").assignValue(true)));
        assertThat(solution, hasItem(new ActionVariable("CL").assignValue(true)));
        assertThat(solution, hasItem(new ActionVariable("PCS").assignValue(true)));
        assertThat(solution, hasItem(new DecisionVariable("RST",Arrays.asList("el","ng")).assignValue("ng")));
        
        Set<Variable<?>> psi = result.getPotentialSourceOfInfeasibility();
        assertThat(psi, hasSize(2));
        assertThat(psi, hasItem(new ActionVariable("A")));
        assertThat(psi, hasItem(new ActionVariable("PPI")));
        
        assertThat(result.getRevisionOperator(), equalTo(Examples.createClopidogrelRevisionOperator()));
    }

}
