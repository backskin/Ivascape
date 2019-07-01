package ivascape.controller;

import ivascape.model.Complex;
import ivascape.model.Graph;
import ivascape.model.GenericGraph;
import javafx.util.Pair;
import java.util.*;

class GraphWorker<K extends Comparable<K>, V extends Complex<V>> {

    private final GenericGraph<K,V> graph;

    private GraphWorker(GenericGraph<K, V> graph) {

        this.graph = graph;
    }

    static <K extends Comparable<K>, V extends Complex<V>> GraphWorker<K,V> factory(GenericGraph<K, V> graph){

        return new GraphWorker<>(graph);
    }

    int getEdgeSize(){
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

    boolean isStrong(){

        if (graph.size() < 1) return false;

        for (int i = 0; i < graph.size(); i++){

            for (int j = 0; j < graph.size(); j++){

                if (!dfs(i, j, new boolean[graph.size()]))

                    return false;
            }
        }

        return true;
    }

    private void buildComponent(int ver, Graph<K,V> original, Graph<K,V> component, boolean[] visited){

        if (visited[ver]) return;
        visited[ver] = true;

        component.addVertex(original.getVertex(ver));

        for (int i = 0; i < original.size(); i ++){

            if (original.getEdge(ver,i) != null ){
                buildComponent(i,original,component,visited);
                component.addEdge(
                        original.getVertex(ver),
                        original.getVertex(i),
                        original.getEdge(ver,i)
                );
            }
        }
    }

    List<GenericGraph<K,V>> getConnectComponents(){

        List<K> viewQueue = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++){

            viewQueue.add(graph.getVertex(i));
        }

        List<GenericGraph<K,V>> components = new ArrayList<>();

        while (!viewQueue.isEmpty()){

            GenericGraph<K,V> newComponent = new GenericGraph<>();

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

    private int findMinInQueue(List<Pair<Pair<K,K>,V>> queue){

        if (queue == null || queue.size() < 1)

            return -1;

        V tempValue = queue.get(0).getValue();

        int index = 0;

        for (Pair<Pair<K,K>,V> i:queue
                ) {

            if (i.getValue().compareTo(tempValue) < 0){

                tempValue = i.getValue();

                index = queue.indexOf(i);
            }
        }
        return index;
    }

    private GenericGraph<K,V> setTreeFromPairs(List<Pair<K,K>> pairs){

        GenericGraph<K,V> tree = new GenericGraph<>();

        for (int i = 0; i < graph.size(); i ++)

            tree.addVertex(graph.getVertex(i));

        for (Pair<K, K> pair : pairs)
            tree.addEdge(
                    pair.getKey(),
                    pair.getValue(),
                    graph.getEdge(pair.getKey(), pair.getValue()));

        return tree;
    }

    GenericGraph<K,V> getPrimResult() {

        if (!isStrong())

            return null;

        int v = (new Random()).nextInt(graph.size()-1);

        List<Pair<Pair<K,K>,V>> queue = new ArrayList<>();

        List<Pair<K,K>> pairs = new ArrayList<>();

        List<K> exList = new ArrayList<>();

        for (int i = 0; i < graph.size(); i++){

            if (graph.getEdge( v, i ) != null)

                queue.add(
                        new Pair<>(
                                new Pair<>(
                                        graph.getVertex(v),
                                        graph.getVertex(i)),
                                        graph.getEdge( v, i )
                        )
                );
        }

        exList.add(graph.getVertex(v));

        while (queue.size()>0){

            int i = findMinInQueue(queue);

            if (exList.indexOf(queue.get(i).getKey().getValue()) < 0){

                exList.add(queue.get(i).getKey().getValue());

                pairs.add(
                        new Pair<>(
                                queue.get(i).getKey().getKey(),
                                queue.get(i).getKey().getValue()
                        )
                );

                v = -1;

                for (int m = 0; m < graph.size(); m++){

                    if (graph.getVertex(m) == queue.get(i).getKey().getValue())

                        v = m;
                }

                for (int k = 0; k < graph.size(); k++){

                    if (graph.getEdge( v, k ) != null
                            && exList.indexOf(graph.getVertex(k)) < 0) {
                        queue.add( new Pair<>( new Pair<>(
                                graph.getVertex(v),
                                graph.getVertex(k)),
                                graph.getEdge( v, k ))
                        );
                    }
                }
            }
            queue.remove(i);
        }
        return setTreeFromPairs(pairs);
    }
}