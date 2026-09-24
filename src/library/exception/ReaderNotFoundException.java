package library.exception;

public class ReaderNotFoundException extends LibraryException {
    public ReaderNotFoundException(int readerId){
        super("library.model.Reader not found " + readerId);
    }
}
