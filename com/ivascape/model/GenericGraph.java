package ivascape.model;

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

        for (int i = 0; i < size(); i ++){
            if (getVertex(i) == ver) return i;
        }
        return -1;
    }

    @Override
    public V getEdge(int start, int end) {

        return edges.get(start).get(end);
    }

    @Override
    public void addVertex(K vertex) {

        edges.add(new ArrayList<>());
        vers.add(vertex);

        for (int i = 0; i < vers.size() - 1; i++) {

            edges.get(edges.size() - 1).add(null);
            edges.get(i).add(null);
        }

        edges.get(edges.size() - 1).add(null);
    }

    @Override
    public void addEdge(K verStart, K verEnd, V value) {

        if (vers.indexOf(verStart) < 0 || vers.indexOf(verEnd) < 0)
            return;

        V mateValue = value.createMating();
        mateValue.setMating(value);

        edges.get(vers.indexOf(verStart)).set(
                vers.indexOf(verEnd), value);

        edges.get(vers.indexOf(verEnd)).set(
                vers.indexOf(verStart), value.getMating());
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
            private List<List<V>> list = edges;

            @Override
            public boolean hasNext() {
                return (i < list.size()) && (j < list.get(i).size());
            }

            @Override
            public V next() {

                V value;
                if (i >= list.size()) throw new IndexOutOfBoundsException();
                do {
                    value = list.get(i).get(j);
                    if (j < i) j++;
                    else { j = 0; i++; }
                }
                while (value == null);
                return value;
            }
        };
    }
}