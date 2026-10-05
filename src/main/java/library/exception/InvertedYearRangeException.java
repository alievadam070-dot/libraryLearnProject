package library.exception;

public class InvertedYearRangeException extends LibraryException{
    private int from;
    private int to;

    public InvertedYearRangeException(int from, int to){
        super("Range inverted: " + from + " < " + to);
        this.from = from;
        this.to = to;
    }

    public int getTo() {
        return to;
    }
    public int getFrom(){
        return from;
    }
}
