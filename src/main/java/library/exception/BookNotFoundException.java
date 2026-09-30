package library.exception;

public class BookNotFoundException extends LibraryException {
    private int bookId;
    public BookNotFoundException(int bookId){

        super("Book not found " + bookId);
        this.bookId= bookId;
    }
    public int getBookId(){
        return bookId;
    }
}
