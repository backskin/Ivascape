package ivascape.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Graph<K, V extends Complex<V>> implements Serializable {

        private final List<K> vers; // Список вершин в графе
        private final List<List<V>> edges; //таблица рёбер

        public Graph() {

                vers = new ArrayList<>();
                edges = new ArrayList<>();
        }

        public void addVer(K ver) {

                edges.add(new ArrayList<>());
                vers.add(ver);

                for (int i = 0; i < vers.size() - 1; i++) {

                        edges.get(edges.size() - 1).add(null);
                        edges.get(i).add(null);
                }

                edges.get(edges.size() - 1).add(null);
        }

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

        public void delVer(K verDead) {

                if (vers.indexOf(verDead) < 0)

                        return;

                for (List<V> i : edges) {

                        i.remove(vers.indexOf(verDead));
                }

                edges.remove(vers.indexOf(verDead));
                vers.remove(verDead);
        }

        public void delEdge(K verStart, K verEnd) {

                if (vers.indexOf(verStart) < 0 || vers.indexOf(verEnd) < 0)

                        return;

                edges.get(vers.indexOf(verStart)).set(vers.indexOf(verEnd), null);
                edges.get(vers.indexOf(verEnd)).set(vers.indexOf(verStart), null);
        }

        public int getVerSize() {

                return vers.size();
        }

        public K getVer(int index) {

                return vers.get(index);
        }

        public V getEdge(int row, int col) {

                return edges.get(row).get(col);
        }

        public V getEdge(K firstVer, K secondVer) {

                if (vers.indexOf(firstVer) < 0 || vers.indexOf(secondVer) < 0)
                        return null;

                return getEdge(vers.indexOf(firstVer), vers.indexOf(secondVer));
        }
}