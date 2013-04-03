package pl.put.idss.cpg.clp.model;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;
import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

import lombok.Data;

@Data
public class LogicalModel {

    private final String disease;
    
    private final Set<Variable<?>> variables;
    
    private final Set<Set<VariableValue<?>>> logicalExpressions;
    
    public Variable<?> getVariable(String name) {
        return selectUnique(getVariables(), having(on(Variable.class).getName(), equalTo(name)));
    }
}
