package library.service;

import library.exception.BookBorrowedException;
import library.exception.BookNotFoundException;
import library.model.Book;
import library.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                () -> assertSame(book1, library.findBookById(book1.getId())),
                () -> assertSame(book2, library.findBookById(book2.getId())),
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
    void findBooksByAuthor_authorsBooksNotInCatalog_emptyList(){
        library.addBook(testTitle,testAuthor,testPublicationDate);
        library.addBook(testTitle,testAuthor,testPublicationDate);

        List<Book> books = library.findBooksByAuthor("OtherAuthor");


        assertTrue(books.isEmpty());

    }

    @Test
    void findBooksByAuthor_nullParameter_throwsIllegalArgumentException(){



        assertThrows(IllegalArgumentException.class, () -> library.findBooksByAuthor(null));


    }
}