package backsoft.ivascape.model;
import java.util.Iterator;

public interface Graph<K, V> {

    void addVertex(K vertex);
    boolean addEdge(K start, K end, V value);
    void addEdge(int start, int end, V value);
    V getEdge(K start, K end);
    K getVertex(int index);
    V getEdge(int start, int end);
    boolean removeVertex(K vertex);
    boolean removeEdge(K start, K end);
    int size();

    default Iterator<K> getVertexIterator() { throw new UnsupportedOperationException("ver_iter"); }
    default Iterator<V> getEdgeIterator() {
        throw new UnsupportedOperationException("edge_iter");
    }
    default Iterator<V> getEdgeIteratorForVertex(K vertex) { throw new UnsupportedOperationException("edges_of_ver_iter"); }
}
