import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Library {
    private Map<Integer, Book> catalog;
    private Map<Integer, Reader> listReader;
    private int bookIdCount = 1;
    private int readerIdCount = 1;

    private int nextIdBook(){
        return bookIdCount++;
    }
    private int nextIdReader(){
        return readerIdCount++;
    }


    /// МЕТОДЫ ДЛЯ РАБОТЫ СО СПИСКОМ КНИГ

    public void addBook(String title, String author, int publicationDate){
        int id = nextIdBook();
        Book book = new Book(title, author, publicationDate,id);
        catalog.put(id, book);
        System.out.println("ID добавленной книги " + id);
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
            else if(book.getAuthor().equalsIgnoreCase(author)){
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
        List<Book> books = new ArrayList<>(catalog.values());

        return books;
    }

    /// МЕТОДЫ ДЛЯ РАБОТЫ С СПИСКОМ ПОЛЬЗОВАТЕЛЕЙ

    public void addReader(String name,String  phone ){

        int id = nextIdReader();
        Reader reader = new Reader(name, phone, id);
        listReader.put(id, reader);
        System.out.println("ID добавленного пользователя " + id);

    }
    public boolean removeReader(int id){
        if(listReader.containsKey(id)){
            if(listReader.get(id).getBookList().isEmpty()) {
                listReader.remove(id);
                return true;
            }
            else{
                System.out.println("Пользователь не сдал все книги!");
                System.out.println("Список не сданных книг: ");
                System.out.println(listReader.get(id).getBookList());
                return false;
            }
        }
        else {
            System.out.println("Пользователь не найден!");
            return false;
        }

    }

    public Reader findReaderById(int id){
        return listReader.get(id);
    }

    public List<Reader> listAllReaders(){
        return new ArrayList<>(listReader.values());
    }

    /// МЕТОДЫ ВЫДАЧИ И ВОЗВРАТА КНИГ

    public boolean borrowBook(int readerId, int bookId) {

        Reader reader = listReader.get(readerId);
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
    public boolean returnBookInLib(int readerId, int bookId){

        Reader reader = listReader.get(readerId);
        if(reader == null){
            System.out.println("Пользователь не найден!");
            return false;
        }

        Book book = catalog.get(bookId);
        if(book == null){
            System.out.println("Книга не найдена!");
            return false;
        }

        if(!reader.getBookList().contains(book)){
            System.out.println("У пользователя нет такой книги!");
            return false;
        }

        if(book.getDueDate() != null && book.getDueDate().isBefore(LocalDate.now())){
            System.out.println("Вы просрочили дату возвращения книги!");
        }

        book.setBorrowed(false);
        book.setIssueDate(null);
        book.setDueDate(null);
        reader.returnBook(book);
        return true;
    }
    public List<Book> listBooksByReader(int readerId){
        Reader reader = listReader.get(readerId);
        if(reader == null){
            System.out.println("Пользователь не найден!");
            return new ArrayList<>();
        }
        return new ArrayList<>(reader.getBookList());
    }

    public List<Reader> listReadersWithBooks(){
        List<Reader> readers = new ArrayList<>();
        for(Reader reader : listReader.values()){
            if(reader.getBookList().isEmpty()){
                continue;
            }
            readers.add(reader);
        }
        return readers;
    }
}
