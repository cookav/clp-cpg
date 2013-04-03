package pl.put.idss.cpg.clp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.google.common.collect.Lists;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
public class ActionVariable extends Variable<Boolean> {
    
    public ActionVariable(String name) {
        super(name, Lists.newArrayList(false,true));
    }

    
}
