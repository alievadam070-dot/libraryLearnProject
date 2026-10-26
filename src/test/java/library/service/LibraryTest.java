package library.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import library.exception.*;
import library.model.Book;
import library.model.Reader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private final String testTitle = "TestTitle";
    private final String testAuthor = "TestAuthor";
    private final String testName = "TestName";
    private final String testPhone = "TestPhone";
    private final int testPublicationDate = 2077;
    private ObjectMapper mapper;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        library = new Library(tempDir);
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
    void addReader_addTwoReaders_readersHaveDifferentId() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);

        assertNotEquals(reader1.getId(), reader2.getId());
    }

    @Test
    void addReader_validData_returnsReadersWithSameData() {
        Reader reader = library.addReader(testName, testPhone);

        assertAll(
                () -> assertEquals(testName, reader.getName()),
                () -> assertEquals(testPhone, reader.getPhone())
        );
    }

    @Test
    void removeReader_readerNotFound_throwsReaderNotFoundException() {
        Reader reader = library.addReader(testName, testPhone);


        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.removeReader(reader.getId() + 10));
        List<Reader> readers = library.listAllReaders();

        assertAll(
                () -> assertEquals(reader.getId() + 10, thrown.getReaderId()),
                () -> assertTrue(readers.contains(reader))
        );


    }

    @Test
    void removeReader_readerHasBorrowedBooks_throwsReaderHasBorrowedBooksException() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);
        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.borrowBook(reader1.getId(), book1.getId());
        library.borrowBook(reader1.getId(), book2.getId());

        ReaderHasBorrowedBooksException thrown = assertThrows(ReaderHasBorrowedBooksException.class, () -> library.removeReader(reader1.getId()));


        assertAll(
                () -> assertTrue(library.listAllReaders().contains(reader1)),
                () -> assertTrue(library.listAllReaders().contains(reader2)),
                () -> assertEquals(reader1.getId(), thrown.getReaderId())
        );
    }

    @Test
    void removeReader_commonCase_readerRemoved() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);

        library.removeReader(reader1.getId());

        assertAll(
                () -> assertEquals(1, library.listAllReaders().size()),
                () -> assertTrue(library.listAllReaders().contains(reader2))
        );
    }

    @Test
    void findReaderById_readerNotFound_throwsReaderNotFoundException() {
        Reader reader = library.addReader(testName, testPhone);


        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.findReaderById(reader.getId() + 10));

        assertEquals(reader.getId() + 10, thrown.getReaderId());
    }

    @Test
    void findReaderById_commonCase_readerFind() {
        Reader reader = library.addReader(testName, testPhone);
        library.addReader("  ", "  ");

        Reader readerFindById = library.findReaderById(reader.getId());

        assertSame(reader, readerFindById);
    }


    @Test
    void borrowBook_readerNotFound_throwsReaderNotFoundException() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);

        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.borrowBook(9999, book.getId()));

        assertAll(
                () -> assertEquals(9999, thrown.getReaderId()),
                () -> assertFalse(book.isBorrowed())
        );

    }

    @Test
    void borrowBook_bookNotFound_throwsBookNotFoundException() {
        Reader reader = library.addReader(testName, testPhone);

        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () -> library.borrowBook(reader.getId(), 9999));

        assertEquals(9999, thrown.getBookId());
    }

    @Test
    void borrowBook_bookBorrowed_throwsBookBorrowedException() {
        Reader reader1 = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader2 = library.addReader(testName, testPhone);
        library.borrowBook(reader1.getId(), book.getId());

        BookBorrowedException thrown = assertThrows(BookBorrowedException.class, () -> library.borrowBook(reader2.getId(), book.getId()));

        assertAll(
                () -> assertEquals(book.getId(), thrown.getBookId()),
                () -> assertTrue(book.isBorrowed()),
                () -> assertEquals(reader1.getId(), book.getIdReader())
        );
    }

    @Test
    void borrowBook_commonCase_allParametersSet() {
        Reader reader = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);

        library.borrowBook(reader.getId(), book.getId());
        List<Book> books = reader.getBookList();


        assertAll(
                () -> assertEquals(LocalDate.now(), book.getIssueDate()),
                () -> assertEquals(reader.getId(), book.getIdReader()),
                () -> assertTrue(book.isBorrowed()),
                () -> assertEquals(book.getIssueDate().plusDays(14), book.getDueDate()),
                () -> assertTrue(books.contains(book)),
                () -> assertFalse(books.contains(book2))
        );
    }

    @Test
    void returnBookInLib_readerNotFound_throwsReaderNotFoundException() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader = library.addReader(testName, testPhone);
        library.borrowBook(reader.getId(), book.getId());


        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.returnBookInLib(-1, book.getId()));

        assertAll(
                () -> assertEquals(-1, thrown.getReaderId()),
                () -> assertTrue(book.isBorrowed())
        );

    }

    @Test
    void returnBookInLib_bookNotFound_throwsBookNotFoundException() {
        Reader reader = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.borrowBook(reader.getId(), book.getId());

        BookNotFoundException thrown = assertThrows(BookNotFoundException.class, () -> library.returnBookInLib(reader.getId(), -1));
        List<Book> books = library.listBooksByReader(reader.getId());

        assertAll(
                () -> assertEquals(-1, thrown.getBookId()),
                () -> assertTrue(books.contains(book)),
                () -> assertEquals(1, books.size())
        );
    }

    @Test
    void returnBookInLib_bookNotBorrowed_throwsBookNotBorrowedException() {
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader = library.addReader(testName, testPhone);
        Book bookBorrow = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.borrowBook(reader.getId(), bookBorrow.getId());

        BookNotBorrowedByReaderException thrown = assertThrows(BookNotBorrowedByReaderException.class, () -> library.returnBookInLib(reader.getId(), book.getId()));


        assertAll(
                () -> assertFalse(book.isBorrowed()),
                () -> assertEquals(book.getId(), thrown.getBookId()),
                () -> assertEquals(reader.getId(), thrown.getReaderId())
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 14})
    void returnBookInLib_overDueDays_dueDaysCounter(int days) {
        Reader reader = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.borrowBook(reader.getId(), book.getId());
        book.setDueDate(book.getIssueDate().minusDays(days));

        int overDueDays = library.returnBookInLib(reader.getId(), book.getId());


        assertEquals(days, overDueDays);
    }

    @Test
    void returnBookInLib_commonCase_allParametersSet() {
        Reader reader = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.borrowBook(reader.getId(), book.getId());


        library.returnBookInLib(reader.getId(), book.getId());
        List<Book> books = reader.getBookList();

        assertAll(
                () -> assertFalse(book.isBorrowed()),
                () -> assertNull(book.getIdReader()),
                () -> assertNull(book.getIssueDate()),
                () -> assertNull(book.getDueDate()),
                () -> assertFalse(books.contains(book))
        );
    }

    @Test
    void listBooksByReader_readerNotFound_throwsReaderNotFoundException() {
        ReaderNotFoundException thrown = assertThrows(ReaderNotFoundException.class, () -> library.listBooksByReader(-1));

        assertEquals(-1, thrown.getReaderId());
    }

    @Test
    void listBooksByReader_readerHaveBooks_returnListWithBooks() {
        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);
        library.addBook(testTitle, testAuthor, testPublicationDate);
        Reader reader = library.addReader(testName, testPhone);
        library.borrowBook(reader.getId(), book1.getId());
        library.borrowBook(reader.getId(), book2.getId());

        List<Book> books = library.listBooksByReader(reader.getId());

        assertAll(
                () -> assertTrue(books.contains(book1)),
                () -> assertTrue(books.contains(book2)),
                () -> assertEquals(2, books.size())
        );
    }

    @Test
    void listReadersWithBooks_returnsReadersWhoHaveBooks() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);
        Reader reader3 = library.addReader(testName, testPhone);

        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);

        library.borrowBook(reader1.getId(), book1.getId());
        library.borrowBook(reader3.getId(), book2.getId());

        List<Reader> result = library.listReadersWithBooks();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(reader1)),
                () -> assertTrue(result.contains(reader3)),
                () -> assertFalse(result.contains(reader2))
        );
    }

    @Test
    void listReadersWithBooks_returnsEmptyListWhenNoReadersExist() {
        List<Reader> result = library.listReadersWithBooks();

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void listReadersWithBooks_returnsEmptyListWhenNoReadersHaveBooks() {
        library.addReader(testName, testPhone);
        library.addReader(testName, testPhone);

        List<Reader> result = library.listReadersWithBooks();

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void listReadersWithBooks_returnsAllReadersWhenEveryoneHasBooks() {
        Reader reader1 = library.addReader(testName, testPhone);
        Reader reader2 = library.addReader(testName, testPhone);

        Book book1 = library.addBook(testTitle, testAuthor, testPublicationDate);
        Book book2 = library.addBook(testTitle, testAuthor, testPublicationDate);

        library.borrowBook(reader1.getId(), book1.getId());
        library.borrowBook(reader2.getId(), book2.getId());


        List<Reader> result = library.listReadersWithBooks();

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.containsAll(List.of(reader1, reader2)))
        );
    }

    @Test
    void listReadersWithBooks_doesNotReturnReaderAfterReturningAllBooks() {

        Reader reader = library.addReader(testName, testPhone);
        Book book = library.addBook(testTitle, testAuthor, testPublicationDate);

        library.borrowBook(reader.getId(), book.getId());
        library.returnBookInLib(reader.getId(), book.getId());


        List<Reader> result = library.listReadersWithBooks();


        assertTrue(result.isEmpty());
    }

    @Test
    void saveToFile_savingData_allFilesSaving() throws IOException {
        library.saveToFile();

        assertAll(
                () -> assertTrue(new File(tempDir, "Catalog.json").exists()),
                () -> assertTrue(new File(tempDir, "Readers.json").exists()),
                () -> assertTrue(new File(tempDir, "Counts.json").exists())
        );
    }

    @Test
    void saveToFile_readCatalogFile_catalogHaveValidData() throws IOException{
        Book book1 = library.addBook("Book1Title","Book1Author",1111);
        Book book2 = library.addBook("Book2Title","Book2Author",2222);
        Reader reader = library.addReader(testName,testPhone);
        library.borrowBook(reader.getId(),book1.getId());

        library.saveToFile();

        Map<Integer,Book> catalog = mapper.readValue(new File(tempDir,"Catalog.json"), new TypeReference<>() {
        });

        assertAll(
                () -> assertTrue(catalog.containsKey(book1.getId())),
                () -> assertTrue(catalog.containsKey(book2.getId())),
                () -> assertEquals(book1.getTitle(),catalog.get(book1.getId()).getTitle()),
                () -> assertEquals(book2.getTitle(),catalog.get(book2.getId()).getTitle()),
                () -> assertEquals(book1.getAuthor(), catalog.get(book1.getId()).getAuthor()),
                () -> assertEquals(book2.getAuthor(), catalog.get(book2.getId()).getAuthor()),
                () -> assertEquals(book1.getPublicationDate(), catalog.get(book1.getId()).getPublicationDate()),
                () -> assertEquals(book2.getPublicationDate(), catalog.get(book2.getId()).getPublicationDate()),
                () -> assertEquals(reader.getId(), catalog.get(book1.getId()).getIdReader()),
                () -> assertTrue(catalog.get(book1.getId()).isBorrowed()),
                () -> assertFalse(catalog.get(book2.getId()).isBorrowed()),
                () -> assertNull(catalog.get(book2.getId()).getIdReader()),
                () -> assertEquals(2, catalog.size())
        );
    }

    @Test
    void saveToFile_readReadersFile_readersHaveValidData() throws IOException{
        Reader reader1 = library.addReader("Reader1Name", "Reader1Phone");
        Reader reader2 = library.addReader("Reader2Name", "Reader2Phone");


        library.saveToFile();

        Map<Integer, Reader> readerMap = mapper.readValue(new File(tempDir, "Readers.json"), new TypeReference<>() {
        });

        assertAll(
                () -> assertTrue(readerMap.containsKey(reader1.getId())),
                () -> assertTrue(readerMap.containsKey(reader2.getId())),
                () -> assertEquals(reader1.getName(), readerMap.get(reader1.getId()).getName()),
                () -> assertEquals(reader2.getName(), readerMap.get(reader2.getId()).getName()),
                () -> assertEquals(reader1.getPhone(), readerMap.get(reader1.getId()).getPhone()),
                () -> assertEquals(reader2.getPhone(), readerMap.get(reader2.getId()).getPhone())
        );
    }

    @Test
    void saveToFile_readCountsFile_countsHaveValidData() throws IOException{
        library.addBook(testTitle,testAuthor,testPublicationDate);
        library.addBook(testTitle,testAuthor,testPublicationDate);
        library.addReader(testName,testPhone);
        library.addReader(testName,testPhone);


        library.saveToFile();

        Map<String,Integer> idCounts = mapper.readValue(new File(tempDir, "Counts.json"), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(3, idCounts.get("bookIdCount")),
                () -> assertEquals(3, idCounts.get("readerIdCount"))
        );
    }


    @Test
    void saveAndLoad_roundTrip_dataCorrectSaveAndLoad() throws IOException{
        Book book1 = library.addBook("Book1Title", "Book1Author", 1111);
        Book book2 = library.addBook("Book2Title", "Book2Author", 2222);
        Reader reader1 = library.addReader("Reader1Name", "Reader1Phone");
        Reader reader2 = library.addReader("Reader2Name", "Reader2Phone");
        library.borrowBook(reader1.getId(),book1.getId());

        library.saveToFile();

        Library newLib = new Library(tempDir);
        newLib.loadFromFile();

        Book loadedBook1 = newLib.findBookById(book1.getId());
        Book loadedBook2 = newLib.findBookById(book2.getId());
        Reader loadedReader1 = newLib.findReaderById(reader1.getId());
        Reader loadedReader2 = newLib.findReaderById(reader2.getId());

        assertAll(
                () -> assertEquals(book1.getTitle(),loadedBook1.getTitle()),
                () -> assertEquals(book2.getTitle(),loadedBook2.getTitle()),
                () -> assertEquals(book1.getAuthor(), loadedBook1.getAuthor()),
                () -> assertEquals(book2.getAuthor(), loadedBook2.getAuthor()),
                () -> assertEquals(book1.getPublicationDate(), loadedBook1.getPublicationDate()),
                () -> assertEquals(book2.getPublicationDate(), loadedBook2.getPublicationDate()),
                () -> assertTrue(loadedBook1.isBorrowed()),
                () -> assertFalse(loadedBook2.isBorrowed()),
                () -> assertEquals(reader1.getId(), loadedBook1.getIdReader()),
                () -> assertNull(loadedBook2.getIdReader()),
                () -> assertEquals(book1.getIssueDate(), loadedBook1.getIssueDate()),
                () -> assertNull(loadedBook2.getIssueDate()),
                () -> assertEquals(book1.getDueDate(), loadedBook1.getDueDate()),
                () -> assertNull(loadedBook2.getDueDate()),
                () -> assertEquals(reader1.getName(),loadedReader1.getName()),
                () -> assertEquals(reader2.getName(),loadedReader2.getName()),
                () -> assertEquals(reader1.getPhone(), loadedReader1.getPhone()),
                () -> assertEquals(reader2.getPhone(), loadedReader2.getPhone()),
                () -> assertTrue(loadedReader2.getBookList().isEmpty()),
                () -> assertTrue(loadedReader1.getBookList().contains(loadedBook1)),
                () -> assertEquals(2, newLib.listAllBooks().size()),
                () -> assertEquals(2, newLib.listAllReaders().size())
        );
    }

    @Test
    void saveAndLoad_idCountsPastLoad_idCountsCorrect() throws IOException{
        Book book1 = library.addBook("Book1Title", "Book1Author", 1111);
        Book book2 = library.addBook("Book2Title", "Book2Author", 2222);
        Reader reader1 = library.addReader("Reader1Name", "Reader1Phone");
        Reader reader2 = library.addReader("Reader2Name", "Reader2Phone");

        library.saveToFile();

        Library newLib = new Library(tempDir);
        newLib.loadFromFile();



        Book newBook = newLib.addBook(testTitle,testAuthor,testPublicationDate);
        Reader newReader = newLib.addReader(testName,testPhone);

        assertAll(
                () -> assertNotEquals(book1.getId(), newBook.getId()),
                () -> assertNotEquals(book2.getId(), newBook.getId()),
                () -> assertNotEquals(reader1.getId(), newReader.getId()),
                () -> assertNotEquals(reader2.getId(), newReader.getId()),
                () -> assertEquals(3, newLib.listAllBooks().size())
        );
    }


    @Test
    void loadFromFile_loadingAllFiles_allFilesLoadedWithCorrectData() throws IOException{
        createCatalogFile();
        createReadersFile();
        createCountsFile();

        List<String> warnings = library.loadFromFile();

        Book book1 = library.findBookById(1);
        Book book2 = library.findBookById(2);
        Book book6 = library.findBookById(6);

        Reader reader1 = library.findReaderById(1);
        Reader reader2 = library.findReaderById(2);
        Reader reader5 = library.findReaderById(5);

        assertAll(
                () -> assertEquals(3,library.listAllBooks().size()),
                () -> assertEquals(3, library.listAllReaders().size()),
                () -> assertTrue(warnings.isEmpty())
        );

        assertAll(
                () -> assertTrue(book6.isBorrowed()),
                () -> assertEquals(2, book6.getIdReader()),
                () -> assertEquals( "Night", book6.getTitle()),
                () -> assertEquals("Steve", book6.getAuthor()),
                () -> assertEquals(2015, book6.getPublicationDate()),
                () -> assertEquals(LocalDate.parse("2026-10-10"), book6.getIssueDate()),
                () -> assertEquals(LocalDate.parse("2026-10-24"), book6.getDueDate())
        );
        assertAll(
                () -> assertTrue(book1.isBorrowed()),
                () -> assertEquals(1, book1.getIdReader()),
                () -> assertEquals("Crime", book1.getTitle()),
                () -> assertEquals("Steve", book1.getAuthor()),
                () -> assertEquals(2000, book1.getPublicationDate()),
                () -> assertEquals(LocalDate.parse("2026-10-01"), book1.getIssueDate()),
                () -> assertEquals(LocalDate.parse("2026-10-15"), book1.getDueDate())
        );
        assertAll(
                () -> assertFalse(book2.isBorrowed()),
                () -> assertNull(book2.getIdReader()),
                () -> assertEquals("Garden", book2.getTitle()),
                () -> assertEquals("Locali", book2.getAuthor()),
                () -> assertEquals(2010, book2.getPublicationDate()),
                () -> assertNull(book2.getIssueDate()),
                () -> assertNull(book2.getDueDate())
        );
        assertAll(
                () -> assertEquals("Anna", reader1.getName()),
                () -> assertEquals("111", reader1.getPhone()),
                () -> assertTrue(reader1.getBookList().contains(book1))
        );
        assertAll(
                () -> assertEquals("Boris", reader2.getName()),
                () -> assertEquals("222", reader2.getPhone()),
                () -> assertTrue(reader2.getBookList().contains(book6))
        );
        assertAll(
                () -> assertEquals("Clara", reader5.getName()),
                () -> assertEquals("333", reader5.getPhone()),
                () -> assertTrue(reader5.getBookList().isEmpty())
        );

        Book newBook = library.addBook(testTitle,testAuthor,testPublicationDate);
        Reader newReader = library.addReader(testName,testPhone);
        assertAll(
                () -> assertEquals(99, newBook.getId()),
                () -> assertEquals(77, newReader.getId())
        );

    }

    @Test
    void loadFromFile_countsFileMissing_nextIdCalculatedFromMaxId() throws IOException{
        createReadersFile();
        createCatalogFile();

        library.loadFromFile();

        Book newBook = library.addBook(testTitle,testAuthor,testPublicationDate);
        Reader newReader = library.addReader(testName,testPhone);
        assertAll(
                () -> assertEquals(7, newBook.getId()),
                () -> assertEquals(6, newReader.getId())
        );
    }

    @Test
    void loadFromFile_readersFileMissing_returnWarnings() throws IOException{
        createCatalogFile();
        createCountsFile();

        List<String> warnings = library.loadFromFile();

        boolean found1 = false;
        boolean found2 =false;

        for(String w : warnings) {
            if (w.contains("Book 1 ") && w.contains("reader 1")){
                found1 = true;
            }
            if(w.contains("Book 6") && w.contains("reader 2")){
                found2 = true;
            }
        }
        boolean finalFound1 = found1;
        boolean finalFound2 = found2;
        assertAll(
                () -> assertTrue(finalFound1),
                () -> assertTrue(finalFound2),
                () -> assertEquals(2, warnings.size()),
                () -> assertEquals(3, library.listAllBooks().size()),
                () -> assertTrue(library.listAllReaders().isEmpty())
        );
    }

    @Test
    void loadFromFile_allFilesMissing_emptyLibrary() throws IOException{
        List<String> warnings = library.loadFromFile();

        assertAll(
                () -> assertTrue(library.listAllReaders().isEmpty()),
                () -> assertTrue(library.listAllBooks().isEmpty()),
                () -> assertTrue(warnings.isEmpty())
        );

        Book book = library.addBook(testTitle,testAuthor,testPublicationDate);
        Reader reader = library.addReader(testName,testPhone);

        assertAll(
                () -> assertEquals(1, book.getId()),
                () -> assertEquals(1, reader.getId())
        );
    }

    @Test
    void loadFromFile_brokenFile_throwsIOException() throws IOException{
        createReadersFile();
        createCatalogFile();
        File catalogFile = new File(tempDir, "Catalog.json");
        Files.writeString(catalogFile.toPath(), """
               {
                  "1": {
                    "id": 1,
                    "borrowed": true,
                    "idReader": 1,
                    "title": "Crime",
                    "eqweqthor": "Steve",
                    "publicationDate": 2000,
                    "issuasdaDate": "2026-10-01",
                    "dueDate": "2026-10-15"
                  },
                  "2": {
                    "id": 2,
                    "bord": faewqse,
                    "idReader": null,
                    "title": "Garden",
                    "aueweqeor": "Locali",
                    "publicationDate": 2010,
                    "issueDate": null,
                    "dueDate": null
                  },
                  "6": {
                    "id": 6,
                    "borrowed": true,
                    "idReader": 2,
                    "title": "Night",
                    "author": "Steve",
                    "publicationDate": 2015,
                    "issueDate": "2026-10-10",
                    "dueDate": "2026-10-24"
                  }
                }""");

        assertThrows(IOException.class, () -> library.loadFromFile());
    }
    @Test
    void loadFromFile_emptyCountsFile_nextIdCalculatedFromMaxId() throws IOException{
        createCatalogFile();
        createReadersFile();
        File countsFile = new File(tempDir, "Counts.json");
        Files.writeString(countsFile.toPath(), """
               {}""");

       library.loadFromFile();

       Book book = library.addBook(testTitle,testAuthor,testPublicationDate);
       Reader reader = library.addReader(testName,testPhone);

       assertAll(
               () -> assertEquals("Crime", library.findBookById(1).getTitle()),
               () -> assertEquals(7, book.getId()),
               () -> assertEquals("Anna", library.findReaderById(1).getName()),
               () -> assertEquals(6, reader.getId())
       );
    }

    private void createCountsFile() throws IOException {
        File countsFile = new File(tempDir, "Counts.json");
        Files.writeString(countsFile.toPath(), """
               { "bookIdCount": 99, "readerIdCount": 77 }""");
    }

    private void createReadersFile() throws IOException {
        File readersfile = new File(tempDir, "Readers.json");
        Files.writeString(readersfile.toPath(), """
                {
                  "1": { "id": 1, "name": "Anna", "phone": "111" },
                  "2": { "id": 2, "name": "Boris", "phone": "222" },
                  "5": { "id": 5, "name": "Clara", "phone": "333" }
                }""");
    }

    private void createCatalogFile() throws IOException {
        File catalogFile = new File(tempDir, "Catalog.json");
        Files.writeString(catalogFile.toPath(), """
                {
                  "1": {
                    "id": 1,
                    "borrowed": true,
                    "idReader": 1,
                    "title": "Crime",
                    "author": "Steve",
                    "publicationDate": 2000,
                    "issueDate": "2026-10-01",
                    "dueDate": "2026-10-15"
                  },
                  "2": {
                    "id": 2,
                    "borrowed": false,
                    "idReader": null,
                    "title": "Garden",
                    "author": "Locali",
                    "publicationDate": 2010,
                    "issueDate": null,
                    "dueDate": null
                  },
                  "6": {
                    "id": 6,
                    "borrowed": true,
                    "idReader": 2,
                    "title": "Night",
                    "author": "Steve",
                    "publicationDate": 2015,
                    "issueDate": "2026-10-10",
                    "dueDate": "2026-10-24"
                  }
                }
                """);
    }


}