# Library Project

A console application for managing a small library: books, readers, and book loans. Data is saved to JSON files between runs.

This is a learning project written in Java.

## Features

- Add, remove, list, and find books by ID
- Search books by author, by title (partial match, case-insensitive), or by publication year range
- Add, remove, list, and find readers by ID
- Lend a book to a reader (the due date is set to 14 days after the issue date)
- Return a book; the program reports how many days it is overdue, if any
- Show the books held by a specific reader, and all readers who currently have books
- Save data on exit and load it on startup

## Requirements

- JDK 21
- Maven (dependencies are downloaded automatically; the project uses Jackson for JSON)
- IntelliJ IDEA is recommended

## How to run

1. Clone the repository:

   ```
   git clone https://github.com/alievadam070-dot/libraryLearnProject.git
   ```

2. Open the project folder in IntelliJ IDEA and wait for Maven to load the dependencies.
3. Run the `main` method of the `library.Main` class (`src/main/java/library/Main.java`).
4. Use the numbered menu in the console.

## Data storage

The program stores its data in the working directory (the project root when started from IDEA):

- `Catalog.json` — books
- `Readers.json` — readers
- `Counts.json` — ID counters for new books and readers

Data is written only when you exit through option `0` in the main menu. If the program is closed in any other way, changes made during that session are lost.

These files are listed in `.gitignore` and are not part of the repository.

## Project structure

- `library.model` — `Book` and `Reader` classes
- `library.service` — `Library` class with the business logic and file storage
- `library.exception` — custom exceptions
- `library.Main` — console menu
