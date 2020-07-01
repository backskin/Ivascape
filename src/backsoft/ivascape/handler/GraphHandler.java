package backsoft.ivascape.handler;

import backsoft.ivascape.logic.Complex;
import backsoft.ivascape.model.Graph;
import backsoft.ivascape.logic.Pair;

import java.util.*;

public abstract class GraphHandler<Ver extends Comparable<Ver>, Edge extends Complex<Ver, Edge>, G extends Graph<Ver, Edge>> {

    protected abstract G newGraph();
    protected abstract <T> List<T> newList();
    protected abstract Map<Ver, Boolean> newMap();
    private final G graph;

    GraphHandler(G graph) { this.graph = graph; }

    private boolean dfs(Ver start, Ver end, Map<Ver, Boolean> visited){

        if (start == end) return true;
        visited.put(start, true);
        for (Iterator<Ver> iterator = graph.getVertexIterator(); iterator.hasNext();){
            Ver iVer = iterator.next();
            if (graph.getEdge(start,iVer) != null && visited.get(iVer) == null && dfs(iVer,end,visited)) return true;
        }
        return false;
    }

    private boolean dfs_opti(int start, int end, boolean[] visited){
        if (start == end) return true;
        visited[start] = true;
        for (int i = 0; i < graph.size(); i++) {
            if (graph.getEdge(start, i) != null && !visited[i] && dfs_opti(i, end, visited))
                return true;
        }
        return false;
    }

    public boolean isStrong_opti(){

        int size = graph.size();
        if (size < 1) return false;
        for (int i = 0; i < size; i++) {
           if (!dfs_opti(0,i, new boolean[graph.size()]))
               return false;
        }
        return true;
    }

    @Deprecated
    public boolean isStrong_old() {

        if (graph.size() < 1) return false;
        Ver one = graph.getVertex(0);
        for (Iterator<Ver> iterTwo = graph.getVertexIterator(); iterTwo.hasNext(); ) {
            Ver j = iterTwo.next();
            if (!dfs(one, j, newMap()))
                return false;
        }
        return true;
    }

    public List<G> getComponents_opti(){

        boolean[] visited = new boolean[graph.size()];
        List<G> components = newList();
        for (int i = 0; i < visited.length; i++) {
            if (visited[i]) continue;
            G newComponent = newGraph();
            buildComponent_opti(i, newComponent, visited);
            components.add(newComponent);
        }
        return components;
    }

    private void buildComponent_opti(int start, G component, boolean[] visited){
        Ver ver = graph.getVertex(start);
        component.addVertex(graph.getVertex(start));
        visited[start] = true;

        for (int i = 0; i < visited.length; i++) {
            if (i == start) continue;
            Edge e = graph.getEdge(start, i);
            if (e == null) continue;
            if (visited[i]) component.addEdge(ver, graph.getVertex(i), e);
            else buildComponent_opti(i, component, visited);
        }
    }

    @Deprecated
    private void buildComponent(Ver ver, G component, Map<Ver, Boolean> visited){

        if (visited.get(ver) != null && visited.get(ver)) return;
        visited.put(ver, true);

        component.addVertex(ver);
        
        for (Iterator<Ver> verIterator = graph.getVertexIterator(); verIterator.hasNext();){
            Ver nextVer = verIterator.next();
            Edge edge = graph.getEdge(ver, nextVer);
            if (edge != null ){
                buildComponent(nextVer, component, visited);
                component.addEdge(ver, nextVer, edge);
            }
        }
    }

    @Deprecated
    public List<G> getConnectComponents(){

        List<Ver> viewQueue = newList();

        for (Iterator<Ver> it = graph.getVertexIterator(); it.hasNext();)
            viewQueue.add(it.next());

        List<G> components = newList();

        while (!viewQueue.isEmpty()){

            G newComponent = newGraph();
            buildComponent(viewQueue.get(0), newComponent, newMap());
            for (Iterator<Ver> it = newComponent.getVertexIterator(); it.hasNext();)
                viewQueue.remove(it.next());
            components.add(newComponent);
        }

        return components;
    }

    private Edge findMinInQueue(List<Edge> queue){

        if (queue == null || queue.size() < 1)
            return null;

        Edge lowCostEdge = queue.get(0);

        for (Edge edge : queue) {

            if (edge.compareTo(lowCostEdge) < 0){
                lowCostEdge = edge;
            }
        }

        return lowCostEdge;
    }

    public G getPrimResult() {

        if (!isStrong_opti()) return null;

        Ver inspectVer = graph.getVertexIterator().next();

        List<Edge> queue = newList();
        List<Pair<Ver, Ver>> pairs = newList();
        List<Ver> blacklist = newList();

        for (Iterator<Edge> edgeIt = graph.getEdgeIteratorForVertex(inspectVer); edgeIt.hasNext();){

            queue.add(edgeIt.next());
        }

        blacklist.add(inspectVer);

        while (queue.size()>0){

            Edge minInQueue = findMinInQueue(queue);

            if (!blacklist.contains(minInQueue.two())){
                blacklist.add(minInQueue.two());

                pairs.add(new Pair<>(minInQueue.one(), minInQueue.two()));

                inspectVer = null;

                for (Iterator<Ver> verIt = graph.getVertexIterator(); verIt.hasNext();){

                    Ver next = verIt.next();

                    if (next.equals(minInQueue.two())) {

                        inspectVer = next;
                        break;
                    }
                }

                for (Iterator<Edge> loopEdgeIt = graph.getEdgeIteratorForVertex(inspectVer); loopEdgeIt.hasNext();){

                    Edge nextEdge = loopEdgeIt.next();

                    if (!blacklist.contains(nextEdge.two())){
                        queue.add(nextEdge);
                    }
                }
            }

            queue.remove(minInQueue);
        }

        G tree = newGraph();

        for (Iterator<Ver> it = graph.getVertexIterator(); it.hasNext(); ) {

            tree.addVertex(it.next());
        }

        for (Pair<Ver, Ver> pair : pairs)
            tree.addEdge(
                    pair.getOne(),
                    pair.getTwo(),
                    graph.getEdge(pair.getOne(), pair.getTwo()));

        return tree;
    }
}