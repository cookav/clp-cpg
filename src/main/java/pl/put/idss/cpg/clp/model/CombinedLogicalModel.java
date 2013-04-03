package pl.put.idss.cpg.clp.model;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import com.google.common.collect.Sets;

@Data
public class CombinedLogicalModel {

    private final Set<LogicalModel> models;
    
    private final Set<Set<VariableValue<?>>> adverseInteractions;
    
    public CombinedLogicalModel(Set<LogicalModel> models,
            Set<Set<VariableValue<?>>> adverseInteractions) {
        super();
        this.models = models;
        this.adverseInteractions = adverseInteractions;
    }
    
    public CombinedLogicalModel(Set<LogicalModel> models) {
        this(models, new HashSet<Set<VariableValue<?>>>());
    }
    
    public Set<String> getDiseases() {
        return Sets.newHashSet(extract(getModels(), on(LogicalModel.class).getDisease()));
    }

    public Set<Variable<?>> getVariables() {
        Set<Variable<?>> variables = Sets.newHashSet();
        for (LogicalModel model : getModels()) {
            variables.addAll(model.getVariables());
        }
        return variables;
    }
    
}
