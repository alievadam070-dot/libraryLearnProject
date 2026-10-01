package library.service;

import library.exception.BookBorrowedException;
import library.exception.BookNotFoundException;
import library.exception.LibraryException;
import library.model.Book;
import library.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void addBook_addTwoBooks_booksHaveDifferentId(){
        String title1 = "Crime";
        String author1 = "Steve";
        int publicationDate1 = 2000;
        String title2 = "Garden";
        String author2 = "Locali";
        int publicationDate2 = 2000;

        Book book1 = library.addBook(title1,author1,publicationDate1);
        Book book2 = library.addBook(title2,author2,publicationDate2);

        assertAll(
                () -> assertSame(book1, library.findBookById(book1.getId())),
                () -> assertSame(book2, library.findBookById(book2.getId())),
                () -> assertNotEquals(book1.getId(),book2.getId())
        );
    }

    @Test
    void removeBook_bookNotFound_throwsException() {

        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        int bookId = book.getId() + 10;



        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () ->{
            library.removeBook(bookId);
        });

        assertAll(
                () -> assertSame(book, library.findBookById(book.getId())),
                () -> assertEquals(bookId, thrown.getBookId())
        );
    }

    @Test
    void removeBook_bookHasBorrowed_throwsBookBorrowedException(){
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader = library.addReader(testName, testPhone);

        library.borrowBook(reader.getId(),book.getId());

        BookBorrowedException thrown = assertThrows(BookBorrowedException.class, () ->{
            library.removeBook(book.getId());
        });


        assertAll(
                () -> assertEquals(book, library.findBookById(book.getId())),
                () -> assertEquals(book.getId(), thrown.getBookId())
        );

    }

    @Test
    void removeBook_existingBook_bookIsRemovedFromCatalog(){
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);



        library.removeBook(book.getId());

        assertThrows(BookNotFoundException.class, () -> {
            library.findBookById(book.getId());
        });
    }
    @Test
    void findBookById_bookNotFound_throwsBookNotFoundException(){
        Book book = library.addBook(testTitle,testAuthor,testPublicationDate);
        int noValidBookId = book.getId() + 10;

        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () ->{
            library.findBookById(noValidBookId);
        });

        assertEquals(noValidBookId, thrown.getBookId());
    }


}