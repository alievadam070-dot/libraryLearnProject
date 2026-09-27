package library.exception;

public class ReaderNotFoundException extends LibraryException {
    public ReaderNotFoundException(int readerId){
        super("Reader not found " + readerId);
    }
}
