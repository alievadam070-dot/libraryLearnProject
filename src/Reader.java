import java.util.ArrayList;
import java.util.List;

public class Reader {
    private int id;
    private String name;
    private List<Book> bookList;
    private String phone;

    public Reader(String name, String phone, int id){
        this.name = name;
        this.phone = phone;
        this.id = id;
        this.bookList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Phone: " + phone + ", ID: " + id + ", Book list: " + bookList;
    }

    public void takeBook(Book book){
        bookList.add(book);
    }

    public void returnBook(Book book){
        bookList.remove(book);
    }
}
