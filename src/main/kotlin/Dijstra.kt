// version 1.1.51

import java.util.TreeSet

class Edge(val name: String, val vertex: String, val dist: Int)

/** One vertex of the graph, complete with mappings to neighbouring vertices */
class Vertex(val name: String) : Comparable<Vertex> {

    var dist = Int.MAX_VALUE  // MAX_VALUE assumed to be infinity
    var previous: Vertex? = null
    val neighbours = HashMap<Vertex, Int>()

    fun getPath(list: MutableList<Pair<String, Int>>) {
        if (this == previous) {
            list.add(Pair(name, 0))
        }
        else if (previous == null) {
            list.add(Pair("Unreachable", Int.MAX_VALUE))
        }
        else {
            previous!!.getPath(list)
            list.add(Pair(name, dist))
        }
    }

    override fun compareTo(other: Vertex): Int {
        if (dist == other.dist) return name.compareTo(other.name)
        return dist.compareTo(other.dist)
    }

    override fun toString() = "($name, $dist)"
}

class Graph(
    edges: List<Edge>,
    directed: Boolean,
    val showAllPaths: Boolean = false
) {
    // mapping of vertex names to Vertex objects, built from a set of Edges
    private val graph = HashMap<String, Vertex>(edges.size)

    init {
        // one pass to find all vertices
        for (e in edges) {
            if (!graph.containsKey(e.name)) graph.put(e.name, Vertex(e.name))
            if (!graph.containsKey(e.vertex)) graph.put(e.vertex, Vertex(e.vertex))
        }

        // another pass to set neighbouring vertices
        for (e in edges) {
            graph[e.name]!!.neighbours.put(graph[e.vertex]!!, e.dist)
            // also do this for an undirected graph if applicable
            if (!directed) graph[e.vertex]!!.neighbours.put(graph[e.name]!!, e.dist)
        }
    }

    /** Runs dijkstra using a specified source vertex */
    fun dijkstra(startName: String) {
        if (!graph.containsKey(startName)) {
            println("Graph doesn't contain start vertex '$startName'")
            return
        }
        val source = graph[startName]
        val q = TreeSet<Vertex>()

        // set-up vertices
        for (v in graph.values) {
            v.previous = if (v == source) source else null
            v.dist = if (v == source)  0 else Int.MAX_VALUE
            q.add(v)
        }

        dijkstra(q)
    }

    /** Implementation of dijkstra's algorithm using a binary heap */
    private fun dijkstra(q: TreeSet<Vertex>) {
        while (!q.isEmpty()) {
            // vertex with shortest distance (first iteration will return source)
            val u = q.pollFirst()
            // if distance is infinite we can ignore 'u' (and any other remaining vertices)
            // since they are unreachable
            if (u.dist == Int.MAX_VALUE) break

            //look at distances to each neighbour
            for (a in u.neighbours) {
                val v = a.key // the neighbour in this iteration

                val alternateDist = u.dist + a.value
                if (alternateDist < v.dist) { // shorter path to neighbour found
                    q.remove(v)
                    v.dist = alternateDist
                    v.previous = u
                    q.add(v)
                }
            }
        }
    }

    /** Gets a path from the source to the specified vertex */
    fun getPath(endName: String): List<Pair<String, Int>> {
        if (!graph.containsKey(endName)) {
            println("Graph doesn't contain end vertex '$endName'")
            return emptyList()
        }
        val mutableList = mutableListOf<Pair<String, Int>>()
        graph[endName]!!.getPath(mutableList)
        return mutableList
    }
}