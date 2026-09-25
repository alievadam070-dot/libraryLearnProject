package library.exception;

public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(int bookId){
        super("Book not found " + bookId);
    }
}
