package pl.put.idss.cpg.clp.model;

import static ch.lambdaj.Lambda.exists;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;

import pl.put.idss.cpg.graph.ActionNode;
import pl.put.idss.cpg.graph.ActionableGraph;
import pl.put.idss.cpg.graph.ContextNode;
import pl.put.idss.cpg.graph.DecisionEdge;
import pl.put.idss.cpg.graph.DecisionNode;
import pl.put.idss.cpg.graph.GraphNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LogicalModelFactory {

    public LogicalModel createLogicalModel(final ActionableGraph graph) {
        final ContextNode contextNode = graph.getContextNode();
        String disease = contextNode.getDisease();
        
        final Map<GraphNode, Variable<?>> variables = Maps.newHashMap();
        for (DecisionNode node : graph.getDecisionNodes()) {
            List<String> domain = extract(graph.outgoingEdgesOf(node), on(DecisionEdge.class).getValue());
            Collections.sort(domain);
            variables.put(node, new DecisionVariable(node.getDecision(), domain));
        }
        
        for (ActionNode node : graph.getActionNodes()) {
            variables.put(node, new ActionVariable(node.getAction()));
        }
        
        final HashSet<Set<VariableValue<?>>> expressions = Sets.newHashSet();
        for (List<GraphNode> path : getAllPaths(graph, contextNode)) {
            expressions.add(createExpression(graph, path, variables));
        }
        
        return new LogicalModel(disease, Sets.newHashSet(variables.values()), expressions);
    }

    private List<List<GraphNode>> getAllPaths(ActionableGraph graph, ContextNode contextNode) {
        List<List<GraphNode>> paths = Lists.newArrayList();
        
        List<GraphNode> path = Lists.newArrayListWithCapacity(graph.vertexSet().size());
        path.add(contextNode);
        
        findPaths(graph, path, paths);
        
        return paths;
    }

    private void findPaths(ActionableGraph graph, List<GraphNode> path, List<List<GraphNode>> paths) {
        GraphNode vertex = path.get(path.size() - 1);
        if (graph.outDegreeOf(vertex) == 0) {
            paths.add(Lists.newArrayList(path));
            return;
        }

        for (DefaultEdge edge : graph.outgoingEdgesOf(vertex)) {
            path.add(graph.getEdgeTarget(edge));
            findPaths(graph, path, paths);
            path.remove(path.size() - 1);
        }
    }

    private Set<VariableValue<?>> createExpression(ActionableGraph graph,
            List<GraphNode> path, Map<GraphNode, Variable<?>> variables) {
        Set<VariableValue<?>> expression = Sets.newHashSet();
        for (int i = 0; i < path.size(); i++) {
            Variable<?> variable = variables.get(path.get(i));
            VariableValue<?> variableValue;
            if (variable instanceof ActionVariable) {
                variableValue = ((ActionVariable)variable).assignValue(true);
            } else if (variable instanceof DecisionVariable) {
                DefaultEdge edge = graph.getEdge(path.get(i), path.get(i+1));
                String value = ((DecisionEdge)edge).getValue();
                variableValue = ((DecisionVariable) variable).assignValue(value);
            } else {
                continue;
            }
            expression.add(variableValue);
        }
        
        List<ActionNode> nodes = graph.getActionNodes();
        for (ActionNode actionNode : nodes) {
            ActionVariable action = (ActionVariable) variables.get(actionNode);
            if (!exists(expression, having(on(VariableValue.class).getVariable(), equalTo(action)))) {
                expression.add(action.assignValue(false));
            }
        }
        
        return expression;
    }

}
