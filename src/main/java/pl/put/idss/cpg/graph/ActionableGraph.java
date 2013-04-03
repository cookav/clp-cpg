package pl.put.idss.cpg.graph;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsInstanceOf.*;

import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

public class ActionableGraph extends SimpleDirectedGraph<GraphNode, DefaultEdge> {

    public ActionableGraph() {
        super(DefaultEdge.class);
    }

    public ContextNode getContextNode() {
        return selectUnique(vertexSet(), instanceOf(ContextNode.class));
    }
    
    @SuppressWarnings("unchecked")
    public List<ActionNode> getActionNodes() {
        return (List<ActionNode>) (List<?>) select(vertexSet(), instanceOf(ActionNode.class));
    }
    
    @SuppressWarnings("unchecked")
    public List<DecisionNode> getDecisionNodes() {
        return (List<DecisionNode>) (List<?>) select(vertexSet(), instanceOf(DecisionNode.class));
    }
    
    @SuppressWarnings("unchecked")
    public Set<DecisionEdge> outgoingEdgesOf(DecisionNode node) {
        return (Set<DecisionEdge>) (Set<?>)  outgoingEdgesOf((GraphNode)node);
    }
    
}
