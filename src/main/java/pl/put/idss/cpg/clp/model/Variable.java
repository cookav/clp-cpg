package pl.put.idss.cpg.clp.model;

import java.util.List;

import lombok.Data;

@Data
public class Variable<T> {

    private final String name;
    
    private final List<T> domain;
    
    public VariableValue<T> assignValue(T value) {
        if (!domain.contains(value)) {
            throw new IllegalArgumentException(String.format("Value '%s' is not in domain: %s", value, domain));
        }
        return new VariableValue<T>(this,value);
    }
}
