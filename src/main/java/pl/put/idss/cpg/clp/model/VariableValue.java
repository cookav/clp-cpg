package pl.put.idss.cpg.clp.model;

import lombok.Data;

@Data
public class VariableValue<T> {

    private final Variable<T> variable;
    
    private final T value;
    
    @Override
    public String toString() {
        return variable.getName() + "=" + value;
    }
}
