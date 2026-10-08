package library.exception;

import library.model.Book;

import java.util.List;

public class ReaderHasBorrowedBooksException extends LibraryException{
    private int readerId;
    public ReaderHasBorrowedBooksException(int readerId, List<Book> books) {
        super("Reader " + readerId + " has not returned all books: " + books);
        this.readerId = readerId;
    }

    public int getReaderId() {
        return readerId;
    }
}
