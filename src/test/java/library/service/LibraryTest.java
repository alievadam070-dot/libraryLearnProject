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
    @BeforeEach
    void setUp() {
     library = new Library();
    }


    @Test
    void addBook_validData_returnsBookWithSameData() {
        String title = "Crime";
        String author = "Steve";
        int publicationDate = 2000;


        Book book = library.addBook(title, author, publicationDate);

        assertAll(
                () -> assertEquals(title, book.getTitle()),
                () -> assertEquals(author, book.getAuthor()),
                () -> assertEquals(publicationDate, book.getPublicationDate())
        );
    }
    @Test
    void addBook_validData_catalogContainsBook() {
        String title = "Crime";
        String author = "Steve";
        int publicationDate = 2000;


        Book book = library.addBook(title, author, publicationDate);

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

        Book book = library.addBook("TestTitle", "TestAuthor", 1999);
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
    void removeBook_bookHasBorrowed_throwsException(){
        Book book = library.addBook("TestTitle", "TestAuthor", 1999);
        Reader reader = library.addReader("TestName", "TestPhone");

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
        Book book = library.addBook("TestTitle", "TestAuthor", 1999);
        Reader reader = library.addReader("TestName", "TestPhone");


        library.removeBook(book.getId());

        assertThrows(BookNotFoundException.class, () -> {
            library.findBookById(book.getId());
        });
    }
}