package backsoft.ivascape.handler;

import backsoft.ivascape.model.Company;
import backsoft.ivascape.model.IvascapeGraph;
import backsoft.ivascape.model.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    protected Map<Company, Boolean> newMap() {
        return new HashMap<>();
    }
}
