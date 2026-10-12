package library.exception;

public class BookNotBorrowedByReaderException extends LibraryException {
    private int readerId;
    private int bookId;

    public BookNotBorrowedByReaderException(int readerId, int bookId){
        super("Reader " + readerId + " has not borrowed book " + bookId);
        this.readerId = readerId;
        this.bookId = bookId;
    }

    public int getReaderId() {
        return readerId;
    }

    public int getBookId() {
        return bookId;
    }
}
