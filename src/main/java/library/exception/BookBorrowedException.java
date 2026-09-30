package library.exception;

public class BookBorrowedException extends LibraryException{

    private int bookId;
        public BookBorrowedException(int bookId) {
            super("Book is borrowed: " + bookId);
            this.bookId = bookId;
        }

    public int getBookId() {
        return bookId;
    }
}
