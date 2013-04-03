package pl.put.idss.cpg.clp.operators;

import java.util.Set;

import pl.put.idss.cpg.clp.model.Variable;
import pl.put.idss.cpg.clp.model.VariableValue;

import lombok.Data;

@Data
public class RevisionOperator implements Operator {

    private final Set<String> diseases;
    
    private final Set<Variable<?>> variables;
    
    private final Set<VariableValue<?>> interaction;
    
    private final Set<VariableValue<?>> revised;
}
