package library.exception;

public class BookNotBorrowedByReaderException extends LibraryException {
    public BookNotBorrowedByReaderException(int readerId, int bookId){
        super("Reader " + readerId + " has not borrowed book " + bookId);
    }
}
