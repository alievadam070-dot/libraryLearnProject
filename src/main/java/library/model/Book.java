package library.model;

import java.time.LocalDate;

public class Book {

    private int id;
    private boolean isBorrowed = false;
    private Integer idReader;
    private String title;
    private String author;
    private int publicationDate;
    private LocalDate issueDate;



    private LocalDate dueDate;

    public Book(){

    }
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

    public Integer getIdReader() {
        return idReader;
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

    public void setIdReader(Integer idReader) {
        this.idReader = idReader;
    }

    @Override
    public String toString() {
        return  "\n Title: " + title +
                "\n Author: " + author +
                "\n Publication date: " + publicationDate +
                "\n ID: " + id +
                "\n Status: " + isBorrowed +
                "\n";
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return id == book.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
