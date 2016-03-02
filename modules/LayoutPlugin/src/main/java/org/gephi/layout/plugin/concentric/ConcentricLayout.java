package org.gephi.layout.plugin.concentric;


import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AbstractLayout;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;
import org.openide.util.NbBundle;

/**
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public class ConcentricLayout extends AbstractLayout implements Layout {

    private Random random;
    private Graph graph;
    private boolean converged;

    //Properties
    private float speed;

    private float dist;
    private String root;
    private float coverage;

    public ConcentricLayout(LayoutBuilder layoutBuilder, float speed, float dist, String root, float coverage) {
        super(layoutBuilder);

        this.speed = speed;
        this.dist = dist;
        this.root = root;
        this.coverage = coverage;

        random = new Random();
    }

    @Override
    public void initAlgo() {
        converged = false;
    }

    @Override
    public void goAlgo() {
        Graph graph = graphModel.getGraphVisible();

        int circleNumber = 1;
        double theta;
        double sectorAngle;

        float xDestn;
        float yDestn;
        float x;
        float y;

        ArrayList<Node> nextCircle;
        ArrayList<Node> neigh;
        int len;
        float m;
        float n;

        Node rootNode;

        graph.readLock();

        ArrayList<Node> nodes = new ArrayList( Arrays.asList( graph.getNodes().toArray() ) );

        if ( nodes.size() == 0 )
        {
            endAlgo();
        }
        else
        {
            root = getRoot();
            rootNode = graph.getNode(root);
            if (rootNode == null)
            {
                root = graph.getNodes().toArray()[0].getId().toString();
                rootNode = graph.getNodes().toArray()[0];
            }
            try {
                x = rootNode.x();
                y = rootNode.y();
                m = getCoverage() * (speed / 10000f);
                n = 1 - m;
                rootNode.setX(n*x);
                rootNode.setY(n*y);
                nodes.remove(rootNode);
            } catch (NullPointerException ex) {
                Logger.getLogger(Concentric.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }

            if (graphModel.isDirected())
            {
                DirectedGraph Dgraph = (DirectedGraph) graph;
                neigh = new ArrayList<Node>( Arrays.asList( Dgraph.getSuccessors(rootNode).toArray() ) );
            }
            else
            {
                neigh = new ArrayList<Node>( Arrays.asList( graph.getNeighbors(rootNode).toArray() ) );
            }

            ArrayList<Node> currentCircle = neigh;
            while ( !nodes.isEmpty() )
            {
                if(currentCircle.isEmpty())
                {
                    currentCircle = new ArrayList<Node>(nodes);
                }

                theta = 0;
                sectorAngle = 2 * Math.PI / currentCircle.size();
                nextCircle = new ArrayList<Node>();

                len = currentCircle.size();
                for(int j=0; j<len; j++)
                {
                    x = currentCircle.get(j).x();
                    y = currentCircle.get(j).y();

                    xDestn = (float) (circleNumber * dist * Math.cos(theta));
                    yDestn = (float) (circleNumber * dist * Math.sin(theta));

                    m = getCoverage() * (speed / 10000f);
                    n = 1 - m;

                    currentCircle.get(j).setX( m*xDestn + n*x );
                    currentCircle.get(j).setY( m*yDestn + n*y );

                    nodes.remove(currentCircle.get(j));

                    if (graphModel.isDirected())
                    {
                        DirectedGraph Dgraph = (DirectedGraph) graph;
                        neigh = new ArrayList<Node>( Arrays.asList( Dgraph.getSuccessors(currentCircle.get(j)).toArray() ) );
                    }
                    else
                    {
                        neigh = new ArrayList<Node>( Arrays.asList( graph.getNeighbors(currentCircle.get(j)).toArray() ) );
                    }

                    for (int k=0; k<neigh.size(); k++)
                    {
                        nextCircle.add(neigh.get(k));
                    }
                    theta += sectorAngle;
                }

                Set<Node> currentCircleSet = new HashSet<Node>();
                len = nextCircle.size();
                for (int j=0; j<len; j++)
                {
                    for (int k=0; k<nodes.size(); k++)
                    {
                        if (nodes.get(k).equals(nextCircle.get(j)))
                        {
                            currentCircleSet.add(nextCircle.get(j));
                            break;
                        }
                    }
                }

                currentCircle = new ArrayList<Node>();
                currentCircle.addAll(currentCircleSet);

                circleNumber += 1;
            }
        }
        graph.readUnlock();
    }

    @Override
    public boolean canAlgo() {
        return !converged;
    }

    @Override
    public void endAlgo() {
        graph = null;
    }

    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<>();
        try {
            properties.add(LayoutProperty.createProperty(
                    this, Float.class,
                    "Concentric.speed.name",
                    "Concentric",
                    "Concentric.speed.name",
                    "Concentric.speed.desc",
                    "getSpeed", "setSpeed"));

            properties.add(LayoutProperty.createProperty(
                    this, Float.class,
                    "Concentric.dist.name",
                    "Concentric",
                    "Concentric.dist.name",
                    "Concentric.dist.desc",
                    "getDist", "setDist"));

            properties.add(LayoutProperty.createProperty(
                    this, Float.class,
                    "Concentric.coverage.name",
                    "Concentric",
                    "Concentric.coverage.name",
                    "Concentric.coverage.desc",
                    "getCoverage", "setCoverage"));

            properties.add(LayoutProperty.createProperty(
                    this, String.class,
                    "Concentric.root.name",
                    "Concentric",
                    "Concentric.root.name",
                    "Concentric.root.desc",
                    "getRoot", "setRoot"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        speed = 10.0f;
        root = "0.0";
        dist = 100.0f;
        coverage = 0.6f;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public void setDist(Float dist) {
        this.dist = dist;
    }

    public Float getDist() {
        return dist;
    }

    public void setCoverage(Float coverage) {
        this.coverage = coverage;
    }

    public Float getCoverage() {
        return coverage;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getRoot() {
        return root;
    }

}
