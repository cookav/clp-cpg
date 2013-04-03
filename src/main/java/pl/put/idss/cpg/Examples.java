package pl.put.idss.cpg;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import pl.put.idss.cpg.clp.model.ActionVariable;
import pl.put.idss.cpg.clp.model.DecisionVariable;
import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;
import pl.put.idss.cpg.clp.operators.InteractionOperator;
import pl.put.idss.cpg.clp.operators.RevisionOperator;
import pl.put.idss.cpg.graph.ActionNode;
import pl.put.idss.cpg.graph.ActionableGraph;
import pl.put.idss.cpg.graph.ContextNode;
import pl.put.idss.cpg.graph.DecisionEdge;
import pl.put.idss.cpg.graph.DecisionNode;

public class Examples {

    public static ActionableGraph createDVTGraph() {
        ActionableGraph graph = new ActionableGraph();
    
        ContextNode context = new ContextNode("DVT");
        DecisionNode sbt = new DecisionNode("SBT");
        DecisionNode hit = new DecisionNode("HIT");
        ActionNode ivcf = new ActionNode("IVCF");
        ActionNode aca = new ActionNode("ACA");
        ActionNode wa = new ActionNode("WA");
    
        graph.addVertex(context);
        graph.addVertex(sbt);
        graph.addVertex(hit);
        graph.addVertex(ivcf);
        graph.addVertex(aca);
        graph.addVertex(wa);
    
        graph.addEdge(context, sbt);
        graph.addEdge(sbt, ivcf, new DecisionEdge("p"));
        graph.addEdge(sbt, hit, new DecisionEdge("a"));
        graph.addEdge(hit, aca, new DecisionEdge("p"));
        graph.addEdge(hit, wa, new DecisionEdge("a"));
        
        return graph;
    }
    
    public static ActionableGraph createUHTNGraph() {
        ActionableGraph graph = new ActionableGraph();
    
        ContextNode context = new ContextNode("UHTN");
        DecisionNode tuhtn = new DecisionNode("TUHTN");
        ActionNode oahta = new ActionNode("OAHTA");
        ActionNode ivahta = new ActionNode("IVAHTA");
    
        graph.addVertex(context);
        graph.addVertex(tuhtn);
        graph.addVertex(oahta);
        graph.addVertex(ivahta);
    
        graph.addEdge(context, tuhtn);
        graph.addEdge(tuhtn, oahta, new DecisionEdge("ur"));
        graph.addEdge(tuhtn, ivahta, new DecisionEdge("em"));
        
        return graph;
    }

    public static InteractionOperator createWAInteractionOperator() {
        DecisionVariable tuhtn = new DecisionVariable("TUHTN", Lists.newArrayList("em","ur"));
        ActionVariable wa = new ActionVariable("WA");
        InteractionOperator operator = new InteractionOperator(
                Sets.newHashSet("*"),
                Sets.<Variable<?>>newHashSet(tuhtn, wa), 
                Sets.<VariableValue<?>>newHashSet(tuhtn.assignValue("ur"), wa.assignValue(true)));
        return operator;
    }

    public static RevisionOperator createWARevisionOperator() {
        DecisionVariable tuhtn = new DecisionVariable("TUHTN", Lists.newArrayList("em","ur"));
        ActionVariable wa = new ActionVariable("WA");
        ActionVariable ivcf = new ActionVariable("IVCF");
        RevisionOperator revisionOperator = new RevisionOperator(
                Sets.newHashSet("*"),
                Sets.<Variable<?>>newHashSet(tuhtn, wa), 
                Sets.<VariableValue<?>>newHashSet(wa.assignValue(true),ivcf.assignValue(false)),
                Sets.<VariableValue<?>>newHashSet(wa.assignValue(false),ivcf.assignValue(true))
                );
        return revisionOperator;
    }
    
    public static ActionableGraph createDUGraph() {
        ActionableGraph graph = new ActionableGraph();
    
        ContextNode context = new ContextNode("DU");
        DecisionNode hp = new DecisionNode("HP");
        DecisionNode zes = new DecisionNode("ZES");
        DecisionNode ue = new DecisionNode("UE");
        ActionNode et = new ActionNode("ET");
        ActionNode ppi = new ActionNode("PPI");
        ActionNode sc = new ActionNode("SC");
        ActionNode rs = new ActionNode("RS");
    
        graph.addVertex(context);
        graph.addVertex(hp);
        graph.addVertex(zes);
        graph.addVertex(ue);
        graph.addVertex(et);
        graph.addVertex(ppi);
        graph.addVertex(sc);
        graph.addVertex(rs);
        
        graph.addEdge(context, hp);
        graph.addEdge(hp, et, new DecisionEdge("p"));
        graph.addEdge(hp, zes, new DecisionEdge("n"));
        graph.addEdge(zes, rs, new DecisionEdge("p"));
        graph.addEdge(zes, ppi, new DecisionEdge("n"));
        graph.addEdge(ue, rs, new DecisionEdge("nh"));
        graph.addEdge(ue, sc, new DecisionEdge("h"));
        graph.addEdge(et, ppi);
        graph.addEdge(ppi, ue);
        
        return graph;
    }
    
    public static ActionableGraph createTIAGraph() {
        ActionableGraph graph = new ActionableGraph();
    
        ContextNode context = new ContextNode("TIA");
        DecisionNode hg = new DecisionNode("HG");
        DecisionNode fast = new DecisionNode("FAST");
        DecisionNode ns = new DecisionNode("NS");
        DecisionNode rst = new DecisionNode("RST");
        ActionNode ec = new ActionNode("EC");
        ActionNode a = new ActionNode("A");
        ActionNode tst = new ActionNode("TST");
        ActionNode pcs = new ActionNode("PCS");
        ActionNode d = new ActionNode("D");
        ActionNode nc = new ActionNode("NC");
    
        graph.addVertex(context);
        graph.addVertex(hg);
        graph.addVertex(fast);
        graph.addVertex(ns);
        graph.addVertex(rst);
        graph.addVertex(ec);
        graph.addVertex(a);
        graph.addVertex(tst);
        graph.addVertex(pcs);
        graph.addVertex(d);
        graph.addVertex(nc);
        
        graph.addEdge(context, hg);
        graph.addEdge(hg, ec, new DecisionEdge("p"));
        graph.addEdge(hg, fast, new DecisionEdge("n"));
        graph.addEdge(fast, ns, new DecisionEdge("p"));
        graph.addEdge(fast, pcs, new DecisionEdge("n"));
        graph.addEdge(ns, a, new DecisionEdge("r"));
        graph.addEdge(ns, tst, new DecisionEdge("nr"));
        graph.addEdge(a, rst);
        graph.addEdge(rst, d, new DecisionEdge("el"));
        graph.addEdge(rst, pcs, new DecisionEdge("ng"));
        graph.addEdge(d, nc);
        graph.addEdge(tst, nc);
        
        return graph;
    }
    
    public static InteractionOperator createAspirinInteractionOperator() {
        ActionVariable a = new ActionVariable("A");
        ActionVariable ppi = new ActionVariable("PPI");
        InteractionOperator operator = new InteractionOperator(
                Sets.newHashSet("DU","TIA"),
                Sets.<Variable<?>>newHashSet(a, ppi), 
                Sets.<VariableValue<?>>newHashSet(a.assignValue(true), ppi.assignValue(false)));
        return operator;
    }
    
    public static RevisionOperator createClopidogrelRevisionOperator() {
        ActionVariable a = new ActionVariable("A");
        ActionVariable d = new ActionVariable("D");
        ActionVariable ppi = new ActionVariable("PPI");
        ActionVariable cl = new ActionVariable("CL");
        RevisionOperator revisionOperator = new RevisionOperator(
                Sets.newHashSet("DU","TIA"),
                Sets.<Variable<?>>newHashSet(a, ppi), 
                Sets.<VariableValue<?>>newHashSet(a.assignValue(true), d.assignValue(false)),
                Sets.<VariableValue<?>>newHashSet(a.assignValue(false), d.assignValue(false), cl.assignValue(true))
                );
        return revisionOperator;
    }
    
    public static RevisionOperator createPPIRevisionOperator() {
        ActionVariable a = new ActionVariable("A");
        ActionVariable d = new ActionVariable("D");
        ActionVariable ppi = new ActionVariable("PPI");
        RevisionOperator revisionOperator = new RevisionOperator(
                Sets.newHashSet("DU","TIA"),
                Sets.<Variable<?>>newHashSet(a, ppi), 
                Sets.<VariableValue<?>>newHashSet(a.assignValue(true), d.assignValue(true)),
                Sets.<VariableValue<?>>newHashSet(a.assignValue(true), d.assignValue(true), ppi.assignValue(true))
                );
        return revisionOperator;
    }
}
