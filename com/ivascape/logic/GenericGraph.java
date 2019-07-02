package ivascape.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GenericGraph<K extends Comparable<K>, V extends Complex<V>> implements Graph<K, V>, Serializable {

    private final List<K> vers;
    private final List<List<V>> edges;

    public GenericGraph() {

        vers = new ArrayList<>();
        edges = new ArrayList<>();
    }

    @Override
    public int indexOf(K ver){

        return vers.indexOf(ver);
    }

    @Override
    public V getEdge(int start, int end) {

        return edges.get(start).get(end);
    }

    @Override
    public void addVertex(K vertex) {


        vers.add(vertex);
        edges.add(new ArrayList<>());

        for (int i = 0; i < vers.size(); i++) {

            edges.get(edges.size()-1).add(null);
            edges.get(i).add(null);
        }

        edges.get(edges.size()-1).add(null);
    }
    @Override
    public void addEdge(K start, K end, V value) {

        int i = indexOf(start);
        int j = indexOf(end);

        if (i < 0 || j < 0) return;

        V mateValue = value.createMating();
        mateValue.setMating(value);

        edges.get(i).set(j, value);
        edges.get(i).set(j, value.getMating());
    }

    @Override
    public K getVertex(int index) {

        return vers.get(index);
    }

    @Override
    public V getEdge(K start, K end) {

        if (vers.indexOf(start) < 0 || vers.indexOf(end) < 0) return null;

        return getEdge(vers.indexOf(start), vers.indexOf(end));
    }

    @Override
    public void removeVertex(K vertex) {

        if (vers.indexOf(vertex) < 0) return;

        for (List<V> i : edges)
            i.remove(vers.indexOf(vertex));

        edges.remove(vers.indexOf(vertex));
        vers.remove(vertex);
    }

    @Override
    public void removeEdge(K start, K end) {

        if (vers.indexOf(start) < 0 || vers.indexOf(end) < 0)

            return;

        edges.get(vers.indexOf(start)).set(vers.indexOf(end), null);
        edges.get(vers.indexOf(end)).set(vers.indexOf(start), null);

    }

    @Override
    public int size() { return vers.size();}

    @Override
    public Iterator<K> getVertexIterator(){

        List<K> sortedList = new ArrayList<>(vers);
        sortedList.sort(K::compareTo);

        return sortedList.iterator();
    }

    @Override
    public Iterator<V> getEdgeIterator() {

        return new Iterator<V>() {

            private int i = 0;
            private int j = 0;

            @Override
            public boolean hasNext() {
                return (i < edges.size()) && (j <= i);
            }

            @Override
            public V next() {

                V value;

                do {
                    value = edges.get(i).get(j);
                    i = j < i ? i : i+1;
                    j = j < i ? j+1 : 0;
                }
                while (value == null);
                return value;
            }
        };
    }
}