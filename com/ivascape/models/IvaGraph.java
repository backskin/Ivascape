package ivascape.models;

import ivascape.logic.GenericGraph;

public class IvaGraph extends GenericGraph<Company, Link> {

    IvaGraph(){
        super();
    }

    void addEdge(Company start, Company end, double value) {

        super.addEdge(start, end, new Link(start,end,value));
    }

    void removeEdge(Link edge){

        super.removeEdge(edge.one(),edge.another());
    }
}