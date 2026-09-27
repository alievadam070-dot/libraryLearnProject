package library.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Reader {
    private int id;
    private String name;

    @JsonIgnore
    private List<Book> bookList =  new ArrayList<>();

    private String phone;

    public Reader(){

    }
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
        return  "\n Name: " + name +
                "\n Phone: " + phone +
                "\n ID: " + id +
                "\n Book list: " + bookList +
                "\n";
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return id == reader.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    public void takeBook(Book book){
        bookList.add(book);
    }

    public void returnBook(Book book){
        bookList.remove(book);
    }
}
