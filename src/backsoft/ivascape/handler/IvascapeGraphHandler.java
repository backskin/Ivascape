package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Link;

import java.util.ArrayList;
import java.util.List;

public class IvascapeGraphHandler extends GraphHandler<Company, Link, IvascapeGraph> {

    public IvascapeGraphHandler(IvascapeGraph graph) {
        super(graph);
    }

    @Override
    protected IvascapeGraph newGraph() {
        return new IvascapeGraph();
    }

    @Override
    protected <T> List<T> newList() {
        return new ArrayList<>();
    }
}
