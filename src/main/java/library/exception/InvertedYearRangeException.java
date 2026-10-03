package library.exception;

public class InvertedYearRangeException extends LibraryException{
    public InvertedYearRangeException(int from, int to){
        super("Range inverted: " + from + " < " + to);
    }
}
