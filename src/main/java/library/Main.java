package library;

import library.service.Library;

import library.model.*;

import library.exception.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
public class Main {


   public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       Library library = new Library();

       try {
           List<String> warnings = library.loadFromFile();
           for(String warning : warnings){
               System.out.println(warning);
           }
       }
       catch (IOException e ) {
           System.out.println(e.getMessage());
       }

       while (true) {
           System.out.println("""
                   === Library ===
                   1. Books
                   2. Readers
                   3. Take and return
                   4. Find
                   0. Exit
                   """);


           switch (readInt(scanner, "Enter the number")) {
               case 1:
                   handleBooks(scanner, library); break;
               case 2:
                   handleReaders(scanner, library); break;
               case 3:
                    handleBorrow(scanner,library); break;
               case 4:
                  handleFind(scanner, library); break;
               case 0:
                   try {
                       library.saveToFile();
                   }
                   catch (IOException e ){
                       System.out.println(e.getMessage());
                   }
                   return;
               default:
                   System.out.println("Invalid input. Please select something from the list.");

           }
       }
   }

        public static void handleBooks(Scanner scanner, Library library){
            int input;
        while(true) {
            System.out.println("""
                               === Books ===
                               1. Add book
                               2. Remove book
                               3. Show all books
                               4. Find book by id
                               0. Back
                               """);
            switch (readInt(scanner, "Enter the number")) {

                case 1:
                    System.out.println("Enter the title");
                    String title = scanner.nextLine();
                    System.out.println("Enter the author");
                    String author = scanner.nextLine();
                    int publicationDate = readInt(scanner,"Enter the publication date" );

                    Book addedBook = library.addBook(title, author, publicationDate);
                    System.out.println("ID of the added book " + addedBook.getId());
                    break;

                case 2:
                    input = readInt(scanner, "Enter the book ID to delete");
                    try{
                        library.removeBook(input);
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
                    input = readInt(scanner, "Enter the book ID to find");
                    try {
                        Book book = library.findBookById(input);
                        System.out.println(book);
                    }
                    catch (LibraryException e){
                        System.out.println(e.getMessage());
                    }
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
                switch (readInt(scanner, "Enter the number")){
                    case 1:
                        System.out.println("Enter the name");
                        String name = scanner.nextLine();
                        System.out.println("Enter the phone");
                        String phone = scanner.nextLine();
                        Reader reader = library.addReader(name, phone);
                        System.out.println("ID added user " + reader.getId());
                        break;

                    case 2:
                        try {
                            Reader removed = library.removeReader(readInt(scanner, "Enter the reader ID to delete"));
                            System.out.println("Reader has been removed: " + removed.getName());
                        }
                        catch (LibraryException e){
                            System.out.println(e.getMessage());
                        }

                        break;
                    case 3:
                        System.out.println("List all readers: ");
                        System.out.println(library.listAllReaders());
                        break;
                    case 4:
                        try {
                            System.out.println(library.findReaderById(readInt(scanner, "Enter reader ID to find")));
                        }
                        catch (LibraryException e){
                           System.out.println(e.getMessage());
                        }
                        break;
                    case 5:

                        try {
                            System.out.println(library.listBooksByReader(readInt(scanner, "Enter reader ID")));
                        }
                        catch (LibraryException e){
                            System.out.println(e.getMessage());
                        }
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

                switch (readInt(scanner, "Enter the number")){

                    case 1:
                        readerId = readInt(scanner, "Enter reader ID");
                        bookId = readInt(scanner, "Enter book ID");

                       try{
                           library.borrowBook(readerId,bookId);
                           System.out.println("Book has been added");
                       }
                       catch (LibraryException e){
                           System.out.println(e.getMessage());
                       }
                        break;

                    case 2:

                        readerId = readInt(scanner, "Enter reader ID");
                        bookId = readInt(scanner, "Enter book ID");

                        try{
                            int overDueDays = library.returnBookInLib(readerId, bookId);

                            System.out.println("Book has been returned");
                            if (overDueDays > 0) {
                                System.out.println("Overdue by " + overDueDays + " days");
                            }
                        }
                        catch (LibraryException e){
                            System.out.println(e.getMessage());
                        }

                        break;

                    case 3:
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

                switch (readInt(scanner, "Enter the number")) {
                    case 1:

                        System.out.println("Enter the author’s full name");


                        System.out.println(library.findBooksByAuthor(scanner.nextLine()));
                        break;

                    case 2:
                        System.out.println("Enter the title book");
                        System.out.println(library.findBooksByTitle(scanner.nextLine()));
                        break;

                    case 3:
                        System.out.println("Enter range year");
                        int from = readInt(scanner, "from");
                        int to = readInt(scanner, "to");
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

    public static int readInt(Scanner scanner, String promt){
        System.out.println(promt);
        while(!scanner.hasNextInt()){
            System.out.println("Invalid input, please enter a number.");
            scanner.nextLine();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

}

