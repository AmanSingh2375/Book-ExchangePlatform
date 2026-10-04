
package bookexchange.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bookexchange.dsa.BookGraph;

@RestController
@RequestMapping("/graph")
public class BookGraphController {

    private final BookGraph bookGraph = new BookGraph();

    @PostMapping("/connect/{user1}/{user2}")
    public String connectUsers(@PathVariable int user1,
                               @PathVariable int user2) {
        bookGraph.addConnection(user1, user2);
        return "Users connected successfully";
    }

    @GetMapping("/connections/{userId}")
    public List<Integer> getConnections(@PathVariable int userId) {
        return bookGraph.getConnections(userId);
    }

    @GetMapping("/bfs/{userId}")
    public List<Integer> runBFS(@PathVariable int userId) {
        return bookGraph.bfs(userId);
    }

    @GetMapping("/dfs/{userId}")
    public List<Integer> runDFS(@PathVariable int userId) {
        return bookGraph.dfs(userId);
    }
}