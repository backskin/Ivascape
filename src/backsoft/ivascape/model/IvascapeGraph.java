package backsoft.ivascape.model;

import backsoft.ivascape.logic.GraphOnList;

public class IvascapeGraph extends GraphOnList<Company, Link> {

    public IvascapeGraph(){ super(); }

    void addEdge(Company start, Company end, double value) {

        super.addEdge(start, end, new Link(start,end,value));
    }

    void removeEdge(Link edge){

        super.removeEdge(edge.one(),edge.two());
    }
}