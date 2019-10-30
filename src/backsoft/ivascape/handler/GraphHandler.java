package backsoft.ivascape.handler;

import backsoft.ivascape.logic.Complex;
import backsoft.ivascape.logic.Graph;
import backsoft.ivascape.logic.Pair;

import java.util.List;
import java.util.Iterator;
import java.util.Random;

public abstract class GraphHandler<Ver extends Comparable<Ver>, Edge extends Complex<Ver, Edge>, G extends Graph<Ver, Edge>> {

    protected abstract G newGraph();
    protected abstract <T> List<T> newList();
    
    private final G graph;

    GraphHandler(G graph) { this.graph = graph; }

    public int getEdgeSize(){
        int result = 0;
        for (int i = 0; i < graph.size(); i++){
            for (int j = i+1; j < graph.size(); j++){

                if (graph.getEdge(i,j) != null)
                    result += 1;
            }
        }
        return result;
    }

    private boolean dfs(int start, int end, boolean[] visited){

        if (start == end)
            return true;

        visited[start] = true;

        for (int i = 0; i < graph.size(); i++){

            if (graph.getEdge(start,i) != null)

                if (!visited[i])

                    if (dfs(i,end,visited))

                        return true;
        }

        return false;
    }

    public boolean isStrong(){

        if (graph.size() < 1) return false;

        for (int i = 0; i < graph.size(); i++){

            for (int j = 0; j < graph.size(); j++){

                if (!dfs(i, j, new boolean[graph.size()]))

                    return false;
            }
        }

        return true;
    }

    private void buildComponent(int ver, G original, G component, boolean[] visited){

        if (visited[ver]) return;
        visited[ver] = true;

        component.addVertex(original.getVertex(ver));
        
        for (int i = 0; i < original.size(); i ++){

            if (original.getEdge(ver,i) != null ){
                buildComponent(i, original, component, visited);
                component.addEdge(
                        original.getVertex(ver),
                        original.getVertex(i),
                        original.getEdge(ver,i)
                );
            }
        }
    }

    public List<G> getConnectComponents(){

        List<Ver> viewQueue = newList();

        for (int i = 0; i < graph.size(); i++){

            viewQueue.add(graph.getVertex(i));
        }

        List<G> components = newList();

        while (!viewQueue.isEmpty()){

            G newComponent = newGraph();

            components.add(newComponent);

            buildComponent(
                    graph.indexOf(viewQueue.get(0)),
                    graph,
                    newComponent,
                    new boolean[graph.size()]
            );

            for (int i = 0; i < newComponent.size(); i ++){

                viewQueue.remove(newComponent.getVertex(i));
            }
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

        if (!isStrong()) return null;

        Ver inspectVer = graph.getVertex((new Random()).nextInt(graph.size()));

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