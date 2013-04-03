package pl.put.idss.cpg.clp.operators;

import java.util.List;

public interface OperatorRepository<T extends Operator> {

    List<T> getAll();
    
}
