package pl.put.idss.cpg;

import static pl.put.idss.cpg.ui.GraphUI.createJGraph;
import static pl.put.idss.cpg.ui.GraphUI.showInFrame;

public class App {
    
    public static void main(String[] args) {
        showInFrame(createJGraph(Examples.createTIAGraph()));
    }


}
