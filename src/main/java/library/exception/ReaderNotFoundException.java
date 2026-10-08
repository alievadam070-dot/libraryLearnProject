package library.exception;

public class ReaderNotFoundException extends LibraryException {
    private int readerId;
    public ReaderNotFoundException(int readerId){
        super("Reader not found " + readerId);
        this.readerId = readerId;
    }

    public int getReaderId() {
        return readerId;
    }
}
