package library.exception;

public class BookBorrowedException extends LibraryException{

        public BookBorrowedException(int bookId) {
            super("Book is borrowed: " + bookId);
        }

}
