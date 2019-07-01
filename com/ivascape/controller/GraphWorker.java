package ivascape.controller;

import ivascape.model.Complex;
import ivascape.model.Graph;

import javafx.util.Pair;
import java.util.*;

public class GraphWorker<K,V extends Complex<V>> {

    private final Graph<K,V> graph;

    private GraphWorker(Graph<K, V> graph) {

        this.graph = graph;
    }

    public static <K,V extends Complex<V>> GraphWorker init(Graph<K,V> graph){

        return new GraphWorker<>(graph);
    }

    private int indexOf(K ver){

        for (int i = 0; i < graph.getVerSize(); i ++){

            if (graph.getVer(i) == ver)
                return i;
        }

        return -1;
    }

    public Iterator<K> getSortedVerIterator(Comparator<K> comparator){

        List<K> sortedList = new ArrayList<>();

        Iterator<K> verIter = getVerIterator();

        while (verIter.hasNext())

            sortedList.add(verIter.next());

        sortedList.sort(comparator);

        return new Iterator<K>() {

            final List<K> list = sortedList;

            int it = 0;

            @Override
            public boolean hasNext() {

                return it < list.size();
            }

            @Override
            public K next() {

                if (hasNext())
                    return list.get(it++);

                return null;
            }
        };
    }

    public Iterator<K> getVerIterator(){

        return new Iterator<K>() {

            int it = 0;

            @Override
            public boolean hasNext() {

                return it < graph.getVerSize();
            }

            @Override
            public K next() {

                if (hasNext())
                    return graph.getVer(it++);

                return null;
            }
        };
    }

    public int getEdgeSize(){
        int result = 0;
        for (int i = 0; i < graph.getVerSize(); i++){
            for (int j = i+1; j < graph.getVerSize(); j++){

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

        for (int i = 0; i < graph.getVerSize(); i++){

            if (graph.getEdge(start,i) != null)

                if (!visited[i])

                    if (dfs(i,end,visited))

                        return true;
        }
        return false;
    }

    boolean isStrong(){

        if (graph.getVerSize() < 1)

            return false;

        for (int i = 0; i < graph.getVerSize(); i++){

            for (int j = 0; j < graph.getVerSize(); j++){

                if (!dfs(i,j,new boolean[graph.getVerSize()]))

                    return false;
            }
        }

        return true;
    }

    private void buildComponent(int ver, Graph<K,V> original, Graph<K,V> component, boolean[] visited){

        if (visited[ver]) return;
        visited[ver] = true;
        component.addVer(original.getVer(ver));

        for (int i = 0; i < original.getVerSize(); i ++){

            if (original.getEdge(ver,i) != null ){
                buildComponent(i,original,component,visited);
                component.addEdge(
                        original.getVer(ver),
                        original.getVer(i),
                        original.getEdge(ver,i)
                );
            }
        }

    }

    public List<Graph> getConnectComponents(){

        List<K> viewQueue = new ArrayList<>();

        for (int i = 0; i < graph.getVerSize(); i++){

            viewQueue.add(graph.getVer(i));
        }

        List<Graph> components = new ArrayList<>();

        while (!viewQueue.isEmpty()){

            Graph<K,V> newComponent = new Graph<>();

            components.add(newComponent);

            buildComponent(
                    indexOf(viewQueue.get(0)),
                    graph,
                    newComponent,
                    new boolean[graph.getVerSize()]
            );

            for (int i = 0; i < newComponent.getVerSize(); i ++){

                viewQueue.remove(newComponent.getVer(i));
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

    private Graph<K,V> setTreeFromPairs(List<Pair<K,K>> pairs){

        Graph<K,V> tree = new Graph<>();

        for (int i = 0; i < graph.getVerSize(); i ++)

            tree.addVer(graph.getVer(i));

        for (Pair<K, K> pair : pairs)
            tree.addEdge(
                    pair.getKey(),
                    pair.getValue(),
                    graph.getEdge(pair.getKey(), pair.getValue()));

        return tree;
    }

    Graph<K,V> getPrimResult() {

        if (!isStrong())

            return null;

        int v = (new Random()).nextInt(graph.getVerSize()-1);

        List<Pair<Pair<K,K>,V>> queue = new ArrayList<>();

        List<Pair<K,K>> pairs = new ArrayList<>();

        List<K> exList = new ArrayList<>();

        for (int i = 0; i < graph.getVerSize(); i++){

            if (graph.getEdge( v, i ) != null)

                queue.add(
                        new Pair<>(
                                new Pair<>(
                                        graph.getVer(v),
                                        graph.getVer(i)),
                                        graph.getEdge( v, i )
                        )
                );
        }

        exList.add(graph.getVer(v));

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

                for (int m = 0; m < graph.getVerSize(); m++){

                    if (graph.getVer(m) == queue.get(i).getKey().getValue())

                        v = m;
                }

                for (int k = 0; k < graph.getVerSize(); k++){

                    if (graph.getEdge( v, k ) != null
                            && exList.indexOf(graph.getVer(k)) < 0)
                    {
                        queue.add(
                                new Pair<>(
                                        new Pair<>(
                                                graph.getVer(v),
                                                graph.getVer(k)),
                                                graph.getEdge( v, k )
                                )
                        );
                    }
                }
            }
            queue.remove(i);
        }
        return setTreeFromPairs(pairs);
    }
}