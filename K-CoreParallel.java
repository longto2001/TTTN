import java.util.*;
import java.util.stream.*;

public class KCoreParallel {

    public static Map<Integer, Set<Integer>> getKCore(Graph graph, int k) {
        Map<Integer, Integer> vertexDegree = new HashMap<>();
        Set<Integer> verticesToRemove = Collections.newSetFromMap(new ConcurrentHashMap<>());
        Set<Integer> vertices = graph.vertices();
        vertices.parallelStream().forEach(v -> {
            vertexDegree.put(v, graph.degree(v));
            if (vertexDegree.get(v) < k) {
                verticesToRemove.add(v);
            }
        });
        while (!verticesToRemove.isEmpty()) {
            Set<Integer> currentVerticesToRemove = verticesToRemove;
            verticesToRemove = Collections.newSetFromMap(new ConcurrentHashMap<>());

            currentVerticesToRemove.parallelStream().forEach(v -> {
                graph.adjacentVertices(v).parallelStream().forEach(w -> {
                    int degree = vertexDegree.get(w);
                    if (degree > k) {
                        vertexDegree.put(w, degree - 1);
                        if (degree - 1 < k) {
                            verticesToRemove.add(w);
                        }
                    }
                });
                vertexDegree.remove(v);
            });
            vertices.removeAll(currentVerticesToRemove);
        }
        Map<Integer, Set<Integer>> kCore = new HashMap<>();
        vertices.parallelStream().forEach(v -> {
            Set<Integer> neighbors = graph.adjacentVertices(v).stream().filter(vertices::contains).collect(Collectors.toSet());
            kCore.put(v, neighbors);
        });

        return kCore;
    }
}/*vertices(): trả về tập hợp các đỉnh của đồ thị.
degree(v): trả về bậc của đỉnh v.
adjacentVertices(v): trả về tập hợp các đỉnh kề với đỉnh v*/
