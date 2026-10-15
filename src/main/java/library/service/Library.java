package library.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.model.Book;
import library.model.Reader;
import library.exception.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library {
    static final private File _DefaultDataDir = new File(".");
    public Library(File dataDir){
        this.catalogFile = new File(dataDir, "Catalog.json");
        this.readersFile = new File(dataDir, "Readers.json");
        this.countsFile = new File(dataDir, "Counts.json");
    }
    public Library(){
        this(_DefaultDataDir);
    }
    private final Map<Integer, Book> catalog = new HashMap<>();
    private final Map<Integer, Reader> readers = new HashMap<>();
    private int bookIdCount = 1;
    private int readerIdCount = 1;

    private final File catalogFile;
    private final File readersFile;
    private final File countsFile;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private int nextIdBook() {
        return bookIdCount++;
    }

    private int nextIdReader() {
        return readerIdCount++;
    }


    /// МЕТОДЫ ДЛЯ РАБОТЫ СО СПИСКОМ КНИГ

    public Book addBook(String title, String author, int publicationDate) {
        int id = nextIdBook();
        Book book = new Book(title, author, publicationDate, id);
        catalog.put(id, book);
        return book;
    }

    public void removeBook(int bookId) {
        Book book = catalog.get(bookId);

        if (book == null) {
            throw new BookNotFoundException(bookId);
        }
        if (book.isBorrowed()) {
            throw new BookBorrowedException(bookId);
        }
        catalog.remove(bookId);
    }

    public Book findBookById(int bookId) {
        Book book = catalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }
        return book;
    }

    public List<Book> findBooksByAuthor(String author) {
        List<Book> books = new ArrayList<>();
        if (author == null) {
            throw new IllegalArgumentException();
        }
        if(author.isBlank()){
            return books;
        }
        for (Book book : catalog.values()) {
            if (book.getAuthor() == null) {
                continue;
            }
            if (book.getAuthor().equalsIgnoreCase(author)) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> findBooksByTitle(String title) {
        if(title == null){
            throw new IllegalArgumentException();
        }

        String lowerTitle = title.toLowerCase();

        List<Book> books = new ArrayList<>();

        if(title.isBlank()){
            return books;
        }

        for (Book book : catalog.values()) {
            if (book.getTitle() == null) {
                continue;
            }

            String bookLowerTitle = book.getTitle().toLowerCase();
            if (bookLowerTitle.contains(lowerTitle)) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> findBooksByYear(int from, int to) {

        if(from > to){
            throw new InvertedYearRangeException(from,to);
        }

        List<Book> books = new ArrayList<>();

        for (Book book : catalog.values()) {
            if (book.getPublicationDate() >= from && book.getPublicationDate() <= to) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Book> listAllBooks() {
        return new ArrayList<>(catalog.values());
    }

    /// МЕТОДЫ ДЛЯ РАБОТЫ С СПИСКОМ ПОЛЬЗОВАТЕЛЕЙ

    public Reader addReader(String name, String phone) {

        if(name == null || phone == null){
            throw new IllegalArgumentException();
        }

        int id = nextIdReader();
        Reader reader = new Reader(name, phone, id);
        readers.put(id, reader);

        return reader;

    }

    public Reader removeReader(int readerId) {
        Reader reader = readers.get(readerId);

        if (reader == null) {
            throw new ReaderNotFoundException(readerId);
        }
        if (!reader.getBookList().isEmpty()) {
            throw new ReaderHasBorrowedBooksException(readerId, reader.getBookList());
        }


        readers.remove(readerId);
        return reader;
    }

    public Reader findReaderById(int readerId) {
        Reader reader = readers.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException(readerId);
        }
        return reader;
    }

    public List<Reader> listAllReaders() {
        return new ArrayList<>(readers.values());
    }

    /// МЕТОДЫ ВЫДАЧИ И ВОЗВРАТА КНИГ

    public void borrowBook(int readerId, int bookId) {

        Reader reader = readers.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException(readerId);
        }

        Book book = catalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        if (book.isBorrowed()) {
            throw new BookBorrowedException(bookId);
        }

        LocalDate issueDate = LocalDate.now();
        book.setIdReader(readerId);
        book.setBorrowed(true);
        book.setIssueDate(issueDate);
        book.setDueDate(issueDate.plusDays(14));
        reader.takeBook(book);
    }

    public int returnBookInLib(int readerId, int bookId) {

        Reader reader = readers.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException(readerId);
        }

        Book book = catalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        if (!reader.getBookList().contains(book)) {
            throw new BookNotBorrowedByReaderException(readerId, bookId);
        }

        long overDueDays = 0;

        if (book.getDueDate() != null && book.getDueDate().isBefore(LocalDate.now())) {
            overDueDays = ChronoUnit.DAYS.between(book.getDueDate(), LocalDate.now());
        }

        book.setBorrowed(false);
        book.setIdReader(null);
        book.setIssueDate(null);
        book.setDueDate(null);
        reader.returnBook(book);
        return (int) overDueDays;
    }

    public List<Book> listBooksByReader(int readerId) {
        Reader reader = readers.get(readerId);
        if (reader == null) {
            throw new ReaderNotFoundException(readerId);
        }
        return new ArrayList<>(reader.getBookList());
    }

    public List<Reader> listReadersWithBooks() {
        List<Reader> readers = new ArrayList<>();
        for (Reader reader : this.readers.values()) {
            if (reader.getBookList().isEmpty()) {
                continue;
            }
            readers.add(reader);
        }
        return readers;
    }

    /// МЕТОДЫ СОХРАНЕНИЕ И ЧТЕНИЯ ДАННЫХ

    public void saveToFile() throws IOException {

        mapper.writeValue(catalogFile, catalog);
        mapper.writeValue(readersFile, readers);

        Map<String, Integer> idCounts = new HashMap<>();
        idCounts.put("bookIdCount", bookIdCount);
        idCounts.put("readerIdCount", readerIdCount);

        mapper.writeValue(countsFile, idCounts);

    }

    public List<String> loadFromFile() throws IOException {

        File catalogFile = this.catalogFile;
        File readersFile = this.readersFile;
        File countsFile = this.countsFile;

        List<String> warnings = new ArrayList<>();


        if (catalogFile.exists()) {
            Map<Integer, Book> loadedBooks = mapper.readValue(catalogFile,
                    new TypeReference<>() {
                    });
            catalog.clear();
            catalog.putAll(loadedBooks);
        }

        if (readersFile.exists()) {
            Map<Integer, Reader> loadedReaders = mapper.readValue(readersFile,
                    new TypeReference<>() {
                    });
            readers.clear();
            readers.putAll(loadedReaders);
        }
        if (countsFile.exists()) {
            Map<String, Integer> loadedCounts = mapper.readValue(countsFile,
                    new TypeReference<>() {
                    });
            bookIdCount = loadedCounts.getOrDefault("bookIdCount", 1);
            readerIdCount = loadedCounts.getOrDefault("readerIdCount", 1);

        } else {
            int maxBookId = 0;
            int maxReaderId = 0;
            for (Book book : catalog.values()) {

                if (book.getId() > maxBookId) {
                    maxBookId = book.getId();
                }
            }
            bookIdCount = maxBookId + 1;

            for (Reader reader : readers.values()) {
                if (reader.getId() > maxReaderId) {
                    maxReaderId = reader.getId();
                }
            }
            readerIdCount = maxReaderId + 1;
        }
        for (Book book : catalog.values()) {
            if (book.getIdReader() == null) {
                continue;
            }

            Reader reader = readers.get(book.getIdReader());
            if (reader == null) {
                warnings.add("Book " + book.getId() + " references missing reader " + book.getIdReader());
                continue;
            }
            reader.takeBook(book);
        }
        return warnings;
    }
}
