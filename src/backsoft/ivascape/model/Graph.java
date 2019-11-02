package backsoft.ivascape.model;
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

    boolean removeVertex(K vertex);
    boolean removeEdge(K start, K end);

    int size();

    Iterator<K> getVertexIterator();

    default Iterator<V> getEdgeIterator() {
        throw new UnsupportedOperationException("edge_iter");
    }

    default Iterator<V> getEdgeIteratorForVertex(K vertex) {
        throw new UnsupportedOperationException("edges_of_ver_iter");
    }
}
