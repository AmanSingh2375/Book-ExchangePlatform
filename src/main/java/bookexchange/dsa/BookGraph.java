
package bookexchange.dsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class BookGraph {

    private Map<Integer, List<Integer>> graph = new HashMap<>();

    public void addUser(int userId) {
        graph.putIfAbsent(userId, new ArrayList<>());
    }

    public void addConnection(int user1, int user2) {
        addUser(user1);
        addUser(user2);

        graph.get(user1).add(user2);
        graph.get(user2).add(user1);
    }

    public List<Integer> getConnections(int userId) {
        return graph.getOrDefault(userId, new ArrayList<>());
    }

    public List<Integer> bfs(int startUser) {
        List<Integer> result = new ArrayList<>();

        if (!graph.containsKey(startUser)) {
            return result;
        }

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();

        visited.add(startUser);
        queue.add(startUser);

        while (!queue.isEmpty()) {
            int user = queue.poll();
            result.add(user);

            for (int neighbor : graph.get(user)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    public List<Integer> dfs(int startUser) {
        List<Integer> result = new ArrayList<>();

        if (!graph.containsKey(startUser)) {
            return result;
        }

        Set<Integer> visited = new HashSet<>();
        dfsTraversal(startUser, visited, result);

        return result;
    }

    private void dfsTraversal(int user, Set<Integer> visited,
                              List<Integer> result) {
        visited.add(user);
        result.add(user);

        for (int neighbor : graph.get(user)) {
            if (!visited.contains(neighbor)) {
                dfsTraversal(neighbor, visited, result);
            }
        }
    }
}