package pl.put.idss.cpg.graph;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DecisionNode extends GraphNode {

    private final String decision;

}
