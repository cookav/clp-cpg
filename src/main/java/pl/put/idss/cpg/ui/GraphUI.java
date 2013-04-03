package pl.put.idss.cpg.ui;

import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

public class GraphUI {
    
    public static <V,E> JGraph createJGraph(DirectedGraph<V, E> graph) {
        AttributeMap vertexAttributes = JGraphModelAdapter.createDefaultVertexAttributes();
        GraphConstants.setBounds(vertexAttributes, new Rectangle2D.Double(50, 50, 200, 40));
        AttributeMap edgeAttributes = JGraphModelAdapter.createDefaultEdgeAttributes(graph);
        JGraph jgraph = new JGraph(
                new JGraphModelAdapter<V, E>(graph, vertexAttributes, edgeAttributes));
        
        JGraphTreeLayout layout = new JGraphTreeLayout();
        layout.setNodeDistance(200);
        layout.setLevelDistance(100);
//        JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
//        layout.setIntraCellSpacing(300);
//        layout.setInterHierarchySpacing(100);
        JGraphFacade graphFacade = new JGraphFacade(jgraph);
        layout.run(graphFacade);
        jgraph.getGraphLayoutCache().edit(graphFacade.createNestedMap(true, true));
        return jgraph;
    }

    public static void showInFrame(JGraph jgraph) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(jgraph));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
