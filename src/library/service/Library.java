package library.service;

import library.model.Book;
import library.model.Reader;
import library.exception.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library {
    private Map<Integer, Book> catalog = new HashMap<>();
    private Map<Integer, Reader> readers = new HashMap<>();
    private int bookIdCount = 1;
    private int readerIdCount = 1;

    private int nextIdBook(){
        return bookIdCount++;
    }
    private int nextIdReader(){
        return readerIdCount++;
    }


    /// МЕТОДЫ ДЛЯ РАБОТЫ СО СПИСКОМ КНИГ

    public Book addBook(String title, String author, int publicationDate){
        int id = nextIdBook();
        Book book = new Book(title, author, publicationDate,id);
        catalog.put(id, book);
        return book;
    }
    public boolean removeBook(int id){
        if(catalog.containsKey(id) && !catalog.get(id).isBorrowed()){
            catalog.remove(id);
            return true;
        }
        else {
            return false;
        }
    }
    public Book findBookById(int id){

        return catalog.get(id);
    }
    public List<Book> findBooksByAuthor(String author){

        List<Book> books = new ArrayList<>();
        for (Book book : catalog.values()) {
            if(book.getAuthor() == null){
                continue;
            }
           if(book.getAuthor().equalsIgnoreCase(author)){
                books.add(book);
            }
        }
        return books;
    }
    public List<Book> findBooksByTitle(String title){
        String lowerTitle = title.toLowerCase();

        List<Book> books = new ArrayList<>();

        for (Book book : catalog.values()) {
            if(book.getTitle() == null){
                continue;
            }

            String bookLowerTitle = book.getTitle().toLowerCase();
            if(bookLowerTitle.contains(lowerTitle)){
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> findBooksByYear(int from, int to){
        List<Book> books = new ArrayList<>();

        for(Book book : catalog.values()){
            if(book.getPublicationDate() >= from && book.getPublicationDate() <= to){
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> listAllBooks(){
        return  new ArrayList<>(catalog.values());
    }

    /// МЕТОДЫ ДЛЯ РАБОТЫ С СПИСКОМ ПОЛЬЗОВАТЕЛЕЙ

    public Reader addReader(String name,String  phone ){

        int id = nextIdReader();
        Reader reader = new Reader(name, phone, id);
        readers.put(id, reader);

        return reader;

    }
    public boolean removeReader(int id){
        if(readers.containsKey(id)){
            if(readers.get(id).getBookList().isEmpty()) {
                readers.remove(id);
                return true;
            }
            else{
                System.out.println("Пользователь не сдал все книги!");
                System.out.println("Список не сданных книг: ");
                System.out.println(readers.get(id).getBookList());
                return false;
            }
        }
        else {
            System.out.println("Пользователь не найден!");
            return false;
        }

    }

    public Reader findReaderById(int id){
        return readers.get(id);
    }

    public List<Reader> listAllReaders(){
        return new ArrayList<>(readers.values());
    }

    /// МЕТОДЫ ВЫДАЧИ И ВОЗВРАТА КНИГ

    public boolean borrowBook(int readerId, int bookId) {

        Reader reader = readers.get(readerId);
        if (reader == null) {
            System.out.println("Пользователь не найден");
            return false;
        }

        Book book = catalog.get(bookId);
        if (book == null) {
            System.out.println("Книга с таким ID не найдена");
            return false;
        }

        if (book.isBorrowed()) {
            System.out.println("Книга занята");
            return false;
        }

        LocalDate issueDate = LocalDate.now();
        book.setBorrowed(true);
        book.setIssueDate(issueDate);
        book.setDueDate(issueDate.plusDays(14));
        reader.takeBook(book);

        return true;
    }
    public int returnBookInLib(int readerId, int bookId){

        Reader reader = readers.get(readerId);
        if(reader == null){
            throw new ReaderNotFoundException(readerId);
        }

        Book book = catalog.get(bookId);
        if(book == null){
            throw new BookNotFoundException(bookId);
        }

        if(!reader.getBookList().contains(book)){
           throw new BookNotBorrowedByReaderException(readerId,bookId);
        }

        long overDueDays = 0;

        if(book.getDueDate() != null && book.getDueDate().isBefore(LocalDate.now())){
            overDueDays = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
            return (int) overDueDays;
        }

        book.setBorrowed(false);
        book.setIssueDate(null);
        book.setDueDate(null);
        reader.returnBook(book);
        return (int) overDueDays;
    }

    public List<Book> listBooksByReader(int readerId){
        Reader reader = readers.get(readerId);
        if(reader == null){
            System.out.println("Пользователь не найден!");
            return new ArrayList<>();
        }
        return new ArrayList<>(reader.getBookList());
    }

    public List<Reader> listReadersWithBooks(){
        List<Reader> readers = new ArrayList<>();
        for(Reader reader : this.readers.values()){
            if(reader.getBookList().isEmpty()){
                continue;
            }
            readers.add(reader);
        }
        return readers;
    }
}
