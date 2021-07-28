package mengxu.taskscheduling.dag;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DigraphGenerator;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdRandom;

public class DigraphGeneratorMX {

    private static final class Edge implements Comparable<DigraphGeneratorMX.Edge> {
        private final int v;
        private final int w;

        private Edge(int v, int w) {
            this.v = v;
            this.w = w;
        }

        public int compareTo(DigraphGeneratorMX.Edge that) {
            if (this.v < that.v) return -1;
            if (this.v > that.v) return +1;
            if (this.w < that.w) return -1;
            if (this.w > that.w) return +1;
            return 0;
        }
    }

    // this class cannot be instantiated
    private DigraphGeneratorMX() { }

    /**
     * Returns a random simple digraph containing {@code V} vertices and {@code E} edges.
     * @param V the number of vertices
     *
     * @return a random simple digraph on {@code V} vertices, containing a total
     *     of {@code E} edges
     * @throws IllegalArgumentException if no such simple digraph exists
     */
    public static Digraph link(int V) {
        Digraph G = new Digraph(V);
        int[] vertices = new int[V];
        for (int i = 0; i < V; i++)
            vertices[i] = i;
        for (int i = 0; i < V-1; i++) {
            G.addEdge(vertices[i], vertices[i+1]);
        }
        return G;
    }

    public static Digraph rootedOutDAG(int V, int E) {
        if (E > (long) V*(V-1) / 2) throw new IllegalArgumentException("Too many edges");
        if (E < V-1)                throw new IllegalArgumentException("Too few edges");
        Digraph G = new Digraph(V);
        SET<Edge> set = new SET<>();

        // fix a topological order
        int[] vertices = new int[V];
        for (int i = 0; i < V; i++)
            vertices[i] = i;
//        StdRandom.shuffle(vertices);

        StdRandom.setSeed(0);
        // one edge pointing from each vertex, other than the root = vertices[V-1]
        for (int v = 0; v < V-1; v++) {
            int w = StdRandom.uniform(v+1, V);
            Edge e = new Edge(v, w);
            set.add(e);
            G.addEdge(vertices[v], vertices[w]);
        }

        while (G.E() < E) {
            int v = StdRandom.uniform(V);
            int w = StdRandom.uniform(V);
            Edge e = new Edge(v, w);
            if ((v < w) && !set.contains(e)) {
                set.add(e);
                G.addEdge(vertices[v], vertices[w]);
            }
        }
        return G;
    }

    public static Digraph FixedDAG() {
        int V = 10;
        Digraph G = new Digraph(V);

        // fix a topological order
        int[] vertices = new int[V];
        for (int i = 0; i < V; i++)
            vertices[i] = i;

        G.addEdge(vertices[0], vertices[1]);
        G.addEdge(vertices[0], vertices[2]);
        G.addEdge(vertices[0], vertices[3]);
        G.addEdge(vertices[0], vertices[4]);
        G.addEdge(vertices[0], vertices[5]);

        G.addEdge(vertices[1], vertices[7]);
        G.addEdge(vertices[1], vertices[8]);

        G.addEdge(vertices[2], vertices[6]);

        G.addEdge(vertices[3], vertices[7]);
        G.addEdge(vertices[3], vertices[8]);

        G.addEdge(vertices[4], vertices[8]);

        G.addEdge(vertices[5], vertices[7]);

        G.addEdge(vertices[6], vertices[9]);

        G.addEdge(vertices[7], vertices[9]);

        G.addEdge(vertices[8], vertices[9]);

        return G;
    }
}
