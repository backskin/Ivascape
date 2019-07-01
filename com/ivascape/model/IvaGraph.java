package ivascape.model;

import java.util.List;

public class IvaGraph extends Graph<Company, Link> {

    public IvaGraph(){
        super();
    }

    private IvaGraph(final List<Company> vers, List<List<Link>> edges){

        super(vers, edges);
    }

    public static IvaGraph cast(Graph<Company, Link> graph){

        return new IvaGraph(graph.vers,graph.edges);
    }
}
