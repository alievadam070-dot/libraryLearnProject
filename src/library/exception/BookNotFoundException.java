package library.exception;

public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(int bookId){
        super("library.model.Book not found " + bookId);
    }
}
