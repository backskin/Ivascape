package ivascape.model;

import ivascape.logic.GenericGraph;

import java.util.Iterator;

public class IvaGraph extends GenericGraph<Company, Link> {

    IvaGraph(){ super(); }

    private IvaGraph(GenericGraph<Company, Link> generic){

        super();
        for (Iterator<Company> vertexIterator = generic.getVertexIterator(); vertexIterator.hasNext();){
            this.addVertex(vertexIterator.next());
        }
        for (Iterator<Link> linkIterator = generic.getEdgeIterator(); linkIterator.hasNext();){
            Link link = linkIterator.next();
            this.addEdge(link.one(),link.another(),link);
        }
    }

    void addEdge(Company start, Company end, double value) {

        super.addEdge(start, end, new Link(start,end,value));
    }

    void removeEdge(Link edge){

        super.removeEdge(edge.one(),edge.another());
    }

    static IvaGraph cast(GenericGraph <Company, Link> generic){

        return new IvaGraph(generic);
    }
}