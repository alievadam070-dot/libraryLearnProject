package library.exception;

public class BookNotBorrowedByReaderException extends LibraryException {
    public BookNotBorrowedByReaderException(int readerId, int bookId){
        super("library.model.Reader " + readerId + " has not borrowed book " + bookId);
    }
}
