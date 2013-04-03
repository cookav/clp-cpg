package pl.put.idss.cpg.clp.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class DecisionVariable extends Variable<String> {
    
    public DecisionVariable(String name, List<String> domain) {
        super(name, domain);
    }
    
}
