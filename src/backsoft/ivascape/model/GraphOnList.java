package backsoft.ivascape.model;

import backsoft.ivascape.logic.Complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GraphOnList<K extends Comparable<K>, V extends Complex<K, V>> implements Graph<K, V>, Serializable {

    private final List<K> vers;
    private final List<List<V>> edges;

    GraphOnList() {

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
    public boolean addEdge(K start, K end, V value) {

        int i = indexOf(start);
        int j = indexOf(end);

        if (i < 0 || j < 0 || i == j) return false;

        V mateValue = value.createMating();
        mateValue.setMating(value);

        edges.get(i).set(j, value);
        edges.get(j).set(i, value.getMating());
        return true;
    }

    @Override
    public K getVertex(int index) {

        return vers.get(index);
    }

    @Override
    public V getEdge(K start, K end) {

        int i = indexOf(start);
        int j = indexOf(end);
        return i < 0 || j < 0 ? null : getEdge(i, j);
    }

    @Override
    public boolean removeVertex(K vertex) {

        int i = indexOf(vertex);
        if (i < 0) return false;
        for (List<V> edge : edges)
            edge.remove(i);
        edges.remove(i);
        vers.remove(vertex);
        return true;
    }

    @Override
    public boolean removeEdge(K start, K end) {

        int i = indexOf(start);
        int j = indexOf(end);

        if (i < 0 || j < 0 || edges.get(i).get(j) == null || edges.get(j).get(i) == null) return false;

        edges.get(i).set(j, null);
        edges.get(j).set(i, null);
        return true;
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

        return (new ArrayList<V>(){{
            for (int i = 0; i < edges.size(); i++) {
                for (int j = 0; j < i; j++)
                    if (edges.get(i).get(j) != null)
                        add(edges.get(i).get(j));
            }
            sort(V::compareTo);
        }}).iterator();
    }

    @Override
    public Iterator<V> getEdgeIteratorForVertex(K vertex) {

        return new ArrayList<V>(){{
            int index = vers.indexOf(vertex);
            for (int i = 0; i < edges.get(index).size(); i++)
                if (i != index && edges.get(index).get(i) != null)
                    add(edges.get(index).get(i));
            sort(V::compareTo);
        }}.iterator();
    }
}