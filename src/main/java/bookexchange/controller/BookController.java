package bookexchange.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import bookexchange.dsa.BookBST;
import bookexchange.model.Book;
import bookexchange.repository.BookRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookBST bookBST;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.bookBST = new BookBST();

        List<Book> books = bookRepository.findAll();

        for (Book book : books) {
            bookBST.insert(book);
        }
    }

    @PostMapping
public Book addBook(@RequestBody Book book, HttpSession session) {
    if (session.getAttribute("userId") == null) {
        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Please login first");
    }

    Book savedBook = bookRepository.save(book);
    bookBST.insert(savedBook);
    return savedBook;
}

    @GetMapping
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable int id) {
        Book book = bookBST.search(id);

        if (book != null) {
            return book;
        }

        return null;
    }

    @GetMapping("/search/{id}")
public Book searchBook(@PathVariable int id) {
    return bookBST.search(id);
}

@GetMapping("/search/title/{title}")
public Book searchByTitle(@PathVariable String title) {
    return bookBST.searchByTitle(title);
}

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) {
        bookRepository.deleteById(id);
        return "Book deleted successfully";
    }
}