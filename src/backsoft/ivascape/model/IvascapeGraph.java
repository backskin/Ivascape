package backsoft.ivascape.model;

public class IvascapeGraph extends GraphOnList<Company, Link> {

    public IvascapeGraph(){ super(); }

    void addEdge(Company start, Company end, double value) {

        super.addEdge(start, end, new Link(start,end,value));
    }

    boolean removeEdge(Link edge){

        return super.removeEdge(edge.one(),edge.two());
    }
}