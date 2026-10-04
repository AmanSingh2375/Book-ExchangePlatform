package bookexchange.dsa;

import bookexchange.model.Book;

public class BookBST {

    class Node {
        Book book;
        Node left;
        Node right;

        Node(Book book) {
            this.book = book;
        }
    }

    private Node root;

    public void insert(Book book) {
        root = insertNode(root, book);
    }

    private Node insertNode(Node root, Book book) {
        if (root == null) {
            return new Node(book);
        }

        if (book.getId() < root.book.getId()) {
            root.left = insertNode(root.left, book);
        } else if (book.getId() > root.book.getId()) {
            root.right = insertNode(root.right, book);
        }

        return root;
    }

    public Book search(int id) {
        Node result = searchNode(root, id);

        if (result != null) {
            return result.book;
        }

        return null;
    }

    public Book searchByTitle(String title) {
    return searchTitle(root, title);
}

private Book searchTitle(Node root, String title) {
    if (root == null) {
        return null;
    }

    if (root.book.getTitle().equalsIgnoreCase(title)) {
        return root.book;
    }

    Book leftResult = searchTitle(root.left, title);

    if (leftResult != null) {
        return leftResult;
    }

    return searchTitle(root.right, title);
}

    private Node searchNode(Node root, int id) {
        if (root == null || root.book.getId() == id) {
            return root;
        }

        if (id < root.book.getId()) {
            return searchNode(root.left, id);
        }

        return searchNode(root.right, id);
    }
}

