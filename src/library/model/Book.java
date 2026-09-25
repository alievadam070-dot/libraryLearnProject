package library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Book {

    private int id;
    private boolean isBorrowed = false;
    private String title;
    private String author;
    private int publicationDate;
    private LocalDate issueDate;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private LocalDate dueDate;

    public Book(String title, String author, int publicationDate, int id){
        this.author =  author;
        this.publicationDate = publicationDate;
        this.title = title;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }


    public int getPublicationDate() {
        return publicationDate;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "Title: " + title +
                ", Author: " + author +
                ", Publication date: " + publicationDate +
                ", ID: " + id +
                ", Status: " + isBorrowed;
    }
}
