package backsoft.ivascape.model;

import backsoft.ivascape.logic.Complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GraphOnList<K extends Comparable<K>, V extends Complex<K, V>> implements Graph<K, V>, Serializable {

    private final List<K> vers = new ArrayList<>();
    private final List<List<V>> edges = new ArrayList<>();

    @Override
    public K getVertex(int index) {
        return vers.get(index);
    }

    @Override
    public V getEdge(int start, int end) {
        return edges.get(start).get(end);
    }

    public int getEdgeSize(){
        int result = 0;
        for (int i = 0; i < edges.size(); i++){
            for (int j = i+1; j < edges.get(i).size(); j++)
                if (edges.get(i).get(j) != null)
                    result += 1;
        }
        return result;
    }

    @Override
    public void addVertex(K vertex) {

        edges.add(new ArrayList<>());
        int len = edges.size()-1;
        for (int i = 0; i < len; i++) {
            edges.get(i).add(null);
            edges.get(len).add(null);
        }
        edges.get(len).add(null);
        vers.add(vertex);
    }
    @Override
    public boolean addEdge(K start, K end, V value) {

        int i = vers.indexOf(start);
        int j = vers.indexOf(end);

        if (i < 0 || j < 0 || i == j) return false;
        edges.get(i).set(j, value);
        edges.get(j).set(i, value.getMating());
        return true;
    }

    @Override
    @Deprecated
    public void addEdge(int start, int end, V value) {
        edges.get(start).set(end, value);
        edges.get(end).set(start, value.getMating());
    }

    @Override
    public V getEdge(K start, K end) {

        int i = vers.indexOf(start);
        int j = vers.indexOf(end);
        return i < 0 || j < 0 ? null : edges.get(i).get(j);
    }

    @Override
    public boolean removeVertex(K vertex) {

        int i = vers.indexOf(vertex);
        for (List<V> edge : edges) edge.remove(i);
        edges.remove(i);
        vers.remove(vertex);
        return true;
    }

    @Override
    public boolean removeEdge(K start, K end) {

        int i = vers.indexOf(start);
        int j = vers.indexOf(end);

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
            for (int i = 0; i < edges.size(); i++){
                for (int j = i+1; j < edges.get(i).size(); j++)
                    if (edges.get(i).get(j) != null)
                        add(edges.get(i).get(j));
            }
            sort(V::compareTo);
        }}).iterator();
    }

    @Override
    public Iterator<V> getEdgeIteratorForVertex(K vertex) {

        return new ArrayList<V>(){{
                for(V value : edges.get(vers.indexOf(vertex)))
                    if (value != null && value.one().equals(vertex))
                        add(value);
            sort(V::compareTo);
        }}.iterator();
    }
}