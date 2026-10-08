package library.service;

import library.exception.*;
import library.model.Book;
import library.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private final String testTitle = "TestTitle";
    private final String testAuthor = "TestAuthor";
    private final String testName = "TestName";
    private final String testPhone = "TestPhone";
    private final int testPublicationDate = 2077;

    @BeforeEach
    void setUp() {
        library = new Library();

    }


    @Test
    void addBook_validData_returnsBookWithSameData() {


        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);

        assertAll(
                () -> assertEquals(testTitle, book.getTitle()),
                () -> assertEquals(testAuthor, book.getAuthor()),
                () -> assertEquals(testPublicationDate, book.getPublicationDate())
        );
    }

    @Test
    void addBook_validData_catalogContainsBook() {


        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);

        assertSame(book, library.findBookById(book.getId()));
    }

    @Test
    void addBook_addTwoBooks_booksHaveDifferentId() {
        String title1 = "Crime";
        String author1 = "Steve";
        int publicationDate1 = 2000;
        String title2 = "Garden";
        String author2 = "Locali";
        int publicationDate2 = 2000;

        Book book1 = library.addBook(title1, author1, publicationDate1);
        Book book2 = library.addBook(title2, author2, publicationDate2);

        assertAll(
                () -> assertNotEquals(book1.getId(), book2.getId())
        );
    }

    @Test
    void removeBook_bookNotFound_throwsException() {

        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        int bookId = book.getId() + 10;


        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () -> library.removeBook(bookId));

        assertAll(
                () -> assertSame(book, library.findBookById(book.getId())),
                () -> assertEquals(bookId, thrown.getBookId())
        );
    }

    @Test
    void removeBook_bookHasBorrowed_throwsBookBorrowedException() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader = library.addReader(testName, testPhone);

        library.borrowBook(reader.getId(), book.getId());

        BookBorrowedException thrown = assertThrows(BookBorrowedException.class, () -> library.removeBook(book.getId()));


        assertAll(
                () -> assertEquals(book, library.findBookById(book.getId())),
                () -> assertEquals(book.getId(), thrown.getBookId())
        );

    }

    @Test
    void removeBook_existingBook_bookIsRemovedFromCatalog() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);


        library.removeBook(book.getId());

        assertThrows(BookNotFoundException.class, () -> library.findBookById(book.getId()));
    }

    @Test
    void findBookById_bookNotFound_throwsBookNotFoundException() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        int noValidBookId = book.getId() + 10;

        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () -> library.findBookById(noValidBookId));

        assertEquals(noValidBookId, thrown.getBookId());
    }

    @Test
    void findBooksByAuthor_bookInCatalogHaveNullAuthor_returnListBookWithOneBook() {
        Book bookNulls = library.addBook(null, null, testPublicationDate);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);

        List<Book> bookList = library.findBooksByAuthor(testAuthor);

        assertAll(
                () -> assertTrue(bookList.contains(book)),
                () -> assertFalse(bookList.contains(bookNulls)),
                () -> assertEquals(1, bookList.size())
        );
    }

    @Test
    void findBooksByAuthor_severalBookByTheAuthor_allBooksThisAuthorInList() {
        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book3 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book4 = library.addBook(testTitle, "otherAuthor", testPublicationDate);

        List<Book> books = library.findBooksByAuthor(testAuthor);

        assertAll(
                () -> assertTrue(books.contains(book1), "Book1 not found"),
                () -> assertTrue(books.contains(book2), "Book2 not found"),
                () -> assertTrue(books.contains(book3), "Book3 not found"),
                () -> assertFalse(books.contains(book4), "Book4 added in list")
        );
    }


    @Test
    void findBooksByAuthor_differentCase_allBooksInList() {
        String upperCaseAuthor = "TESTAUTHOR";
        String lowerCaseAuthor = "testauthor";
        String lowerAndUpperCase = "TestAuthor";
        Book upperCaseAuthorBook = library.addBook(testTitle, upperCaseAuthor, testPublicationDate);
        Book lowerCaseAuthorBook = library.addBook(testTitle, lowerCaseAuthor, testPublicationDate);
        Book book = library.addBook(testTitle, lowerAndUpperCase, testPublicationDate);

        List<Book> books = library.findBooksByAuthor(testAuthor);

        assertAll(
                () -> assertTrue(books.contains(upperCaseAuthorBook)),
                () -> assertTrue(books.contains(lowerCaseAuthorBook)),
                () -> assertTrue(books.contains(book)),
                () -> assertEquals(3, books.size())
        );

    }

    @Test
    void findBooksByAuthor_authorsBooksNotInCatalog_emptyList() {
        library.addBook(testTitle, testAuthor, testPublicationDate);
        library.addBook(testTitle, testAuthor, testPublicationDate);

        List<Book> books = library.findBooksByAuthor("OtherAuthor");


        assertTrue(books.isEmpty());

    }

    @Test
    void findBooksByAuthor_nullParameter_throwsIllegalArgumentException() {


        assertThrows(IllegalArgumentException.class, () -> library.findBooksByAuthor(null));


    }

    @Test
    void findBooksByTitle_nullParameter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.findBooksByTitle(null));
    }

    @Test
    void findBooksByTitle_differentCase_bookFound() {
        String titleCaseUpAndLow = "tEsTtItLe";
        String titleOtherCase = "TESTTitLE";
        Book book = library.addBook(titleOtherCase, testAuthor, testPublicationDate);


        List<Book> books = library.findBooksByTitle(titleCaseUpAndLow);

        assertAll(
                () -> assertTrue(books.contains(book)),
                () -> assertEquals(1, books.size())
        );
    }

    @Test
    void findBooksByTitle_foundRightBook_found() {
        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book rightBook = library.addBook("RightTitle", testAuthor, testPublicationDate);

        List<Book> books = library.findBooksByTitle("RightTitle");

        assertAll(
                () -> assertTrue(books.contains(rightBook)),
                () -> assertFalse(books.contains(book1)),
                () -> assertFalse(books.contains(book2)),
                () -> assertEquals(1, books.size())
        );
    }

    @Test
    void findBooksByTitle_foundByFragment_BooksFound() {
        Book book1 = library.addBook("test", testAuthor, testPublicationDate);
        Book book2 = library.addBook("TestTitle", testAuthor, testPublicationDate);
        Book book3 = library.addBook("titleTest", testAuthor, testPublicationDate);
        Book book4 = library.addBook("otherBook", testAuthor, testPublicationDate);

        List<Book> books = library.findBooksByTitle("test");

        assertAll(
                () -> assertTrue(books.contains(book1)),
                () -> assertTrue(books.contains(book2)),
                () -> assertTrue(books.contains(book3)),
                () -> assertFalse(books.contains(book4)),
                () -> assertEquals(3, books.size())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void findBooksByTitle_foundByEmptyString_returnEmptyList(String empty) {
        library.addBook("  ", testAuthor, testPublicationDate);

        List<Book> books1 = library.findBooksByTitle(empty);

        assertTrue(books1.isEmpty());
    }

    @Test
    void findBooksByTitle_bookCatalogHaveNullTitle_returnListWithTwoBook() {
        library.addBook(null, testAuthor, testPublicationDate);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.addBook(null, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);

        List<Book> books = library.findBooksByTitle(testTitle);

        assertAll(
                () -> assertTrue(books.contains(book)),
                () -> assertTrue(books.contains(book2)),
                () -> assertEquals(2, books.size())
        );
    }

    @Test
    void findBooksByYear_invertedRange_throwsInvertedYearRangeException() {
        int from = 1000;
        int to = 1;

        InvertedYearRangeException thrown = assertThrows(InvertedYearRangeException.class, () -> library.findBooksByYear(from, to));
        assertAll(
                () -> assertEquals(from, thrown.getFrom()),
                () -> assertEquals(to, thrown.getTo())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {2008, 2020, 2015})
    void findBooksByYear_findByCorrectRange_listIncludesBookFromRange(int year) {
        int from = 2008;
        int to = 2020;
        Book book = library.addBook(testTitle, testAuthor, 2025);
        Book book2 = library.addBook(testTitle, testAuthor, year);
        Book book3 = library.addBook(testTitle, testAuthor, year);
        Book book4 = library.addBook(testTitle, testAuthor, year);


        List<Book> books = library.findBooksByYear(from, to);

        assertAll(
                () -> assertEquals(3, books.size()),
                () -> assertTrue(books.contains(book2)),
                () -> assertTrue(books.contains(book3)),
                () -> assertTrue(books.contains(book4)),
                () -> assertFalse(books.contains(book))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 3000, 2007, 2021})
    void findBooksByYear_findByOutRange_returnEmptyList(int year) {
        int from = 2008;
        int to = 2020;
        library.addBook(testTitle, testAuthor, year);

        List<Book> books = library.findBooksByYear(from, to);

        assertTrue(books.isEmpty());
    }

    @Test
    void addReader_nameNullParameter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.addReader(null, testPhone));
    }

    @Test
    void addReader_phoneNullParameter_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.addReader(testName, null));
    }

    @Test
    void addReader_correctReadersData_readersAddInList() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);

        List<Reader> readers = library.listAllReaders();

        assertAll(
                () -> assertEquals(2, readers.size()),
                () -> assertTrue(readers.contains(reader1)),
                () -> assertTrue(readers.contains(reader2))
        );
    }

    @Test
    void addReader_addTwoReaders_readersHaveDifferentId(){
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);

        assertNotEquals(reader1.getId(), reader2.getId());
    }

    @Test
    void addReader_validData_returnsReadersWithSameData(){
        Reader reader = library.addReader(testName,testPhone);

        assertAll(
                () -> assertEquals(testName,reader.getName()),
                () -> assertEquals(testPhone, reader.getPhone())
        );
    }

    @Test
    void removeReader_readerNotFound_throwsReaderNotFoundException(){
       Reader reader = library.addReader(testName,testPhone);


        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.removeReader(reader.getId() + 10));
        List<Reader> readers = library.listAllReaders();

        assertAll(
                () -> assertEquals(reader.getId() + 10, thrown.getReaderId()),
                () -> assertTrue(readers.contains(reader))
        );


    }
    @Test
    void removeReader_readerHasBorrowedBooks_throwsReaderHasBorrowedBooksException(){
        Reader reader1 = library.addReader(testName,testPhone);
        Reader reader2 = library.addReader(testName,testPhone);
        Book book1 = library.addBook(testTitle,testAuthor,testPublicationDate);
        Book book2 = library.addBook(testTitle,testAuthor,testPublicationDate);
        library.borrowBook(reader1.getId(),book1.getId());
        library.borrowBook(reader1.getId(),book2.getId());

        ReaderHasBorrowedBooksException thrown = assertThrows(ReaderHasBorrowedBooksException.class, () -> library.removeReader(reader1.getId()));


        assertAll(
                () -> assertTrue(library.listAllReaders().contains(reader1)),
                () -> assertTrue(library.listAllReaders().contains(reader2)),
                () -> assertEquals(reader1.getId(),thrown.getReaderId())
        );
    }

    @Test
    void removeReader_commonCase_readerRemoved(){
        Reader reader1 = library.addReader(testName,testPhone);
        Reader reader2 = library.addReader(testName,testPhone);

        library.removeReader(reader1.getId());

        assertAll(
                () -> assertEquals(1, library.listAllReaders().size()),
                () -> assertTrue(library.listAllReaders().contains(reader2))
        );
    }
}