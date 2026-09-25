package library;

import library.service.*;

import library.model.*;

import library.exception.*;

import java.util.Scanner;
public class Main {


   static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       Library library = new Library();
       while (true) {
           System.out.println("""
                   === Library ===
                   1. Books
                   2. Readers
                   3. Take and return
                   4. Find
                   0. Exit
                   """);


           switch (scanner.nextInt()) {
               case 1:
                   handleBooks(scanner, library); break;
               case 2:
                   handleReaders(scanner, library); break;
               case 3:
                    handleBorrow(scanner,library); break;
               case 4:
                  handleFind(scanner, library); break;
               case 0:
                   return;
               default:
                   System.out.println("Please, enter the number");
           }
       }
   }

        public static void handleBooks(Scanner scanner, Library library){
        while(true) {
            System.out.println("""
                               === Books ===
                               1. Add book
                               2. Remove book
                               3. Show all books
                               4. Find book by id
                               0. Back
                               """);
            switch (scanner.nextInt()) {

                case 1:
                    scanner.nextLine();
                    System.out.println("Enter the title");
                    String title = scanner.nextLine();
                    System.out.println("Enter the author");
                    String author = scanner.nextLine();
                    System.out.println("Enter the publication date");
                    int publicationDate = scanner.nextInt();

                    Book addedBook = library.addBook(title, author, publicationDate);
                    System.out.println("ID of the added book " + addedBook.getId());
                    break;

                case 2:
                    System.out.println("Enter the book ID to delete");
                    try{
                        library.removeBook(scanner.nextInt());
                        System.out.println("Book has been deleted");
                    }
                    catch (LibraryException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("List all books: ");
                    System.out.println(library.listAllBooks());
                    break;
                case 4:
                    System.out.println("Enter the book ID to find");
                    Book book = library.findBookById(scanner.nextInt());
                    if (book == null) {
                        System.out.println("Book not found");
                        break;
                    }
                    System.out.println(book);
                    break;
                case 0:
                    return;

                default:
                    System.out.println("Error, try again");
                    break;

            }
        }
    }

        public static void handleReaders(Scanner scanner, Library library){
            while(true){
                System.out.println("""
                               === Readers ===
                               1. Add reader
                               2. Remove reader
                               3. Show all reader
                               4. Find reader by ID
                               5. Show reader books
                               0. Back
                               """);
                switch (scanner.nextInt()){
                    case 1:
                        scanner.nextLine();
                        System.out.println("Enter the name");
                        String name = scanner.nextLine();
                        System.out.println("Enter the phone");
                        String phone = scanner.nextLine();
                        Reader reader = library.addReader(name, phone);
                        System.out.println("ID added user " + reader.getId());
                        break;

                    case 2:
                        scanner.nextLine();
                        System.out.println("Enter the reader ID to delete");
                        try {
                            Reader removed = library.removeReader(scanner.nextInt());
                            System.out.println("Reader has been removed: " + removed.getName());
                        }
                        catch (LibraryException e){
                            System.out.println(e.getMessage());
                        }

                        break;
                    case 3:
                        scanner.nextLine();
                        System.out.println("List all readers: ");
                        System.out.println(library.listAllReaders());
                        break;
                    case 4:
                        scanner.nextLine();
                        System.out.println("Enter reader ID to find");
                        System.out.println(library.findReaderById(scanner.nextInt()));
                        break;
                    case 5:
                        scanner.nextLine();
                        System.out.println("Enter reader ID");
                        System.out.println(library.listBooksByReader(scanner.nextInt()));
                        break;
                    case 0:
                        return;

                    default:
                        System.out.println("Error, try again");
                        break;
                }
            }
   }

        public static void handleBorrow(Scanner scanner, Library library){
            while(true){
                System.out.println("""
                               === Take and return ===
                               1. Take book
                               2. Return book
                               3. Show readers with books
                               0. Back
                               """);

                int readerId;
                int bookId;

                switch (scanner.nextInt()){

                    case 1:
                        scanner.nextLine();
                        System.out.println("Enter reader ID");
                        readerId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter book ID");
                        bookId = scanner.nextInt();
                        scanner.nextLine();

                       try{
                           library.borrowBook(readerId,bookId);
                           System.out.println("Book has been added");
                       }
                       catch (LibraryException e){
                           System.out.println(e.getMessage());
                       }
                        break;

                    case 2:
                        scanner.nextLine();
                        System.out.println("Enter reader ID");
                        readerId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println("Enter book ID");
                        bookId = scanner.nextInt();
                        scanner.nextLine();

                        try{
                            int overDueDays = library.returnBookInLib(readerId, bookId);

                            System.out.println("Book has been return");
                            if (overDueDays > 0) {
                                System.out.println("Overdue by " + overDueDays + " days");
                            }
                        }
                        catch (LibraryException e){
                            System.out.println(e.getMessage());
                        }

                        break;

                    case 3:
                        scanner.nextLine();
                        System.out.println("List readers with books: ");
                        System.out.println(library.listReadersWithBooks());
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Error, try again");
                        break;

                }
            }
        }

        public static void handleFind(Scanner scanner, Library library){

            while (true) {
                System.out.println("""
                               === Find ===
                               1. By author
                               2. By title
                               3. By year(range)
                               0. Back
                               """);

                switch (scanner.nextInt()) {
                    case 1:
                        scanner.nextLine();
                        System.out.println("Enter the author’s full name");


                        System.out.println(library.findBooksByAuthor(scanner.nextLine()));
                        break;

                    case 2:
                        scanner.nextLine();
                        System.out.println("Enter the title book");

                        System.out.println(library.findBooksByTitle(scanner.nextLine()));
                        break;

                    case 3:
                        scanner.nextLine();
                        System.out.println("Enter range year");
                        int from = scanner.nextInt();
                        scanner.nextLine();
                        int to = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println(library.findBooksByYear(from, to));
                        break;

                    case 0:
                        return;
                    default:
                        System.out.println("Error, try again");
                        break;

                }


        }
    }

}

