
package bookexchange.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import bookexchange.model.Book;
import bookexchange.model.ExchangeRequest;
import bookexchange.repository.BookRepository;
import bookexchange.repository.ExchangeRequestRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/requests")
public class ExchangeRequestController {

    private final ExchangeRequestRepository requestRepository;
    private final BookRepository bookRepository;

    public ExchangeRequestController(
            ExchangeRequestRepository requestRepository,
            BookRepository bookRepository) {
        this.requestRepository = requestRepository;
        this.bookRepository = bookRepository;
    }

    private void requireLogin(HttpSession session) {
        if (session.getAttribute("userId") == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login first");
        }
    }

    @PostMapping
    public ExchangeRequest createRequest(
            @RequestBody ExchangeRequest request,
            HttpSession session) {

        requireLogin(session);

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"));

        if (request.getRequesterName() == null
                || request.getRequesterName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requester name is required");
        }

        if (request.getOwnerName() == null
                || !request.getOwnerName().equals(book.getOwner())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Incorrect book owner");
        }

        String loggedInName =
                (String) session.getAttribute("userName");

        if (loggedInName == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login again");
        }

        if (loggedInName.equalsIgnoreCase(book.getOwner())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot request your own book");
        }

        request.setRequesterName(loggedInName);
        request.setOwnerName(book.getOwner());
        request.setStatus("Pending");

        return requestRepository.save(request);
    }

    @GetMapping
    public List<ExchangeRequest> getAllRequests(
            HttpSession session) {

        requireLogin(session);

        String loggedInName =
                (String) session.getAttribute("userName");

        if (loggedInName == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Please login again");
        }

        return requestRepository.findAll().stream()
                .filter(request ->
                        loggedInName.equalsIgnoreCase(
                                request.getOwnerName())
                        || loggedInName.equalsIgnoreCase(
                                request.getRequesterName()))
                .toList();
    }

    @PatchMapping("/{id}/status")
    public ExchangeRequest updateStatus(
            @PathVariable int id,
            @RequestParam String status,
            HttpSession session) {

        requireLogin(session);

        if (!"Accepted".equals(status)
                && !"Rejected".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status must be Accepted or Rejected");
        }

        ExchangeRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Request not found"));

        String loggedInName =
                (String) session.getAttribute("userName");

        if (loggedInName == null
                || !loggedInName.equalsIgnoreCase(
                        request.getOwnerName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the book owner can accept or reject this request");
        }

        if (!"Pending".equals(request.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This request has already been processed");
        }

        request.setStatus(status);

        return requestRepository.save(request);
    }
}