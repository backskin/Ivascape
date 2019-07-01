package ivascape.logic;
import java.util.Iterator;

public interface Graph<K, V> {

    void addVertex(K vertex);
    void addEdge(K start, K end, V value);

    V getEdge(K start, K end);
    default V getEdge(int start, int end) {
        throw new UnsupportedOperationException("edge_by_indx");
    }

    K getVertex(int index);
    int indexOf(K vertex);

    void removeVertex(K vertex);
    void removeEdge(K start, K end);

    int size();

    Iterator<K> getVertexIterator();

    default Iterator<V> getEdgeIterator() { throw new UnsupportedOperationException("edge_iter");}
}
