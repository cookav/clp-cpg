package pl.put.idss.cpg.clp.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import pl.put.idss.cpg.Examples;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class LogicalModelFactoryTest {

    private LogicalModelFactory factory;
    
    @Before
    public void setUp() throws Exception {
        factory = new LogicalModelFactory();
    }

    @Test
    public void testCreateLogicalModelDVT() {
        LogicalModel model = factory.createLogicalModel(Examples.createDVTGraph());
        
        assertThat(model.getDisease(), equalTo("DVT"));
        
        assertThat(model.getVariables(), hasSize(5));
        DecisionVariable sbt = new DecisionVariable("SBT", Lists.newArrayList("a","p"));
        DecisionVariable hit = new DecisionVariable("HIT", Lists.newArrayList("a","p"));
        ActionVariable ivcf = new ActionVariable("IVCF");
        ActionVariable aca = new ActionVariable("ACA");
        ActionVariable wa = new ActionVariable("WA");
        assertThat(model.getVariables(), hasItem(sbt));
        assertThat(model.getVariables(), hasItem(hit));
        assertThat(model.getVariables(), hasItem(ivcf));
        assertThat(model.getVariables(), hasItem(aca));
        assertThat(model.getVariables(), hasItem(wa));
        
        assertThat(model.getLogicalExpressions(), hasSize(3));
        assertThat(model.getLogicalExpressions(), 
                hasItem(Sets.<VariableValue<?>>newHashSet(
                        sbt.assignValue("p"), 
                        ivcf.assignValue(true), 
                        aca.assignValue(false), 
                        wa.assignValue(false)))
                );
        assertThat(model.getLogicalExpressions(), 
                hasItem(Sets.<VariableValue<?>>newHashSet(
                        sbt.assignValue("a"), 
                        hit.assignValue("p"), 
                        ivcf.assignValue(false), 
                        aca.assignValue(true), 
                        wa.assignValue(false)))
                );
        assertThat(model.getLogicalExpressions(), 
                hasItem(Sets.<VariableValue<?>>newHashSet(
                        sbt.assignValue("a"), 
                        hit.assignValue("a"), 
                        ivcf.assignValue(false), 
                        aca.assignValue(false), 
                        wa.assignValue(true)))
                );
    }
    
    
}
