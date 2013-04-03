package pl.put.idss.cpg.graph;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.jgrapht.graph.DefaultEdge;

@Data
@EqualsAndHashCode(callSuper = true)
public class DecisionEdge extends DefaultEdge {
    
    private final String value;

}
