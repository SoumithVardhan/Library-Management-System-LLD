# Library Management System - Low Level Design

A comprehensive Library Management System implemented in Java, demonstrating Object-Oriented Programming principles, SOLID principles, and multiple design patterns.

## 📋 Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [SOLID Principles](#solid-principles)
- [Class Diagram](#class-diagram)
- [Project Structure](#project-structure)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Key Components](#key-components)

## ✨ Features

### Core Features
- **Book Management**: Add, update, remove, and search books
- **Patron Management**: Register and manage library members
- **Lending Operations**: Checkout and return books with due date tracking
- **Inventory Management**: Track available and borrowed books

### Advanced Features
- **Multi-Branch Support**: Manage multiple library branches
- **Book Transfer**: Transfer books between branches
- **Reservation System**: Reserve books that are currently checked out
- **Notification System**: Email and SMS notifications for due dates and reservations
- **Recommendation System**: Book recommendations based on borrowing history
- **Search Functionality**: Search books by title, author, or ISBN using Strategy pattern

## 🏗️ Architecture

The system follows a layered architecture:

```
┌─────────────────────────────────────┐
│     Presentation Layer (Main)       │
├─────────────────────────────────────┤
│        Service Layer                │
│  (Business Logic & Orchestration)   │
├─────────────────────────────────────┤
│       Repository Layer              │
│     (Data Access & Storage)         │
├─────────────────────────────────────┤
│         Model Layer                 │
│    (Domain Entities & DTOs)         │
└─────────────────────────────────────┘
```

## 🎨 Design Patterns

### 1. Factory Pattern
**Location**: `com.library.factory.PatronFactory`

Creates different types of patrons (Student, Faculty, General) with appropriate configurations.

```java
Patron student = PatronFactory.createStudent("John Doe", "john@email.com", "555-0101");
```

### 2. Strategy Pattern
**Location**: `com.library.strategy.*`

Implements flexible book search strategies (Title, Author, ISBN).

```java
bookService.setSearchStrategy(new TitleSearchStrategy());
List<Book> results = bookService.searchBooks("Clean Code");
```

### 3. Observer Pattern
**Location**: `com.library.observer.*`

Implements notification system for book events (checkout, return, reservation).

```java
lendingService.attach(new EmailNotificationObserver("user@email.com"));
lendingService.attach(new SMSNotificationObserver("555-0101"));
```

### 4. Singleton Pattern
**Location**: `com.library.util.Logger`

Ensures single instance of logger throughout the application.

```java
Logger logger = Logger.getInstance();
```

### 5. Repository Pattern
**Location**: `com.library.repository.*`

Abstracts data access logic and provides a collection-like interface.

## 🎯 SOLID Principles

### Single Responsibility Principle (SRP)
Each class has a single, well-defined responsibility:
- `BookService`: Manages book operations only
- `PatronService`: Manages patron operations only
- `LendingService`: Manages lending operations only

### Open/Closed Principle (OCP)
The system is open for extension but closed for modification:
- New search strategies can be added without modifying existing code
- New observer types can be added without changing the subject

### Liskov Substitution Principle (LSP)
All observer implementations can be substituted for the base Observer interface without affecting functionality.

### Interface Segregation Principle (ISP)
Interfaces are client-specific and focused:
- `Observer`: Only contains update method
- `Subject`: Only contains attach/detach/notify methods
- `SearchStrategy`: Only contains search method

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- Services depend on repository interfaces, not concrete implementations
- Search functionality depends on SearchStrategy interface

## 📊 Class Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLASS DIAGRAM                            │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────┐         ┌──────────────────┐
│      Book        │         │     Patron       │
├──────────────────┤         ├──────────────────┤
│ -isbn: String    │         │ -patronId: String│
│ -title: String   │         │ -name: String    │
│ -author: String  │         │ -email: String   │
│ -publicationYear │         │ -phone: String   │
│ -status: Status  │         │ -patronType: Type│
│ -branchId: String│         │ -history: List   │
├──────────────────┤         ├──────────────────┤
│ +getters()       │         │ +getters()       │
│ +setters()       │         │ +setters()       │
└──────────────────┘         └──────────────────┘
        │                            │
        │                            │
        └─────────┬──────────────────┘
                  │
                  │ uses
                  ▼
        ┌──────────────────┐
        │ BorrowingRecord  │
        ├──────────────────┤
        │ -recordId: String│
        │ -patronId: String│
        │ -isbn: String    │
        │ -checkoutDate    │
        │ -dueDate         │
        │ -returnDate      │
        ├──────────────────┤
        │ +isOverdue()     │
        │ +isReturned()    │
        └──────────────────┘

┌────────────────────────────────────────────────┐
│              DESIGN PATTERNS                   │
└────────────────────────────────────────────────┘

┌─────────────────┐
│  «interface»    │
│ SearchStrategy  │◄──────────┐
├─────────────────┤           │
│ +search()       │           │ implements
└─────────────────┘           │
        △                     │
        │                     │
        ├─────────────────────┼──────────────────┐
        │                     │                  │
┌───────────────┐   ┌──────────────────┐  ┌──────────────┐
│TitleSearch    │   │ AuthorSearch     │  │ ISBNSearch   │
│Strategy       │   │ Strategy         │  │ Strategy     │
└───────────────┘   └──────────────────┘  └──────────────┘

┌─────────────────┐
│  «interface»    │
│    Observer     │◄──────────┐
├─────────────────┤           │
│ +update()       │           │ implements
└─────────────────┘           │
        △                     │
        │                     │
        ├─────────────────────┼──────────────────┐
        │                     │                  │
┌───────────────┐   ┌──────────────────┐  ┌──────────────┐
│EmailNotif     │   │ SMSNotif         │  │   ...        │
│Observer       │   │ Observer         │  │              │
└───────────────┘   └──────────────────┘  └──────────────┘

┌─────────────────┐
│  «interface»    │
│    Subject      │
├─────────────────┤
│ +attach()       │
│ +detach()       │
│ +notify()       │
└─────────────────┘
        △
        │ implements
        │
┌───────────────────┐
│ LendingService    │
│ ReservationService│
└───────────────────┘

┌─────────────────┐
│ PatronFactory   │
├─────────────────┤
│ +createStudent()│
│ +createFaculty()│
│ +createGeneral()│
└─────────────────┘
        │ creates
        ▼
┌─────────────────┐
│     Patron      │
└─────────────────┘

┌────────────────────────────────────────────────┐
│              SERVICE LAYER                     │
└────────────────────────────────────────────────┘

┌──────────────────┐   ┌──────────────────┐
│  BookService     │   │  PatronService   │
├──────────────────┤   ├──────────────────┤
│ +addBook()       │   │ +addPatron()     │
│ +updateBook()    │   │ +updatePatron()  │
│ +removeBook()    │   │ +removePatron()  │
│ +searchBooks()   │   │ +findPatronById()│
└──────────────────┘   └──────────────────┘
        │                       │
        └───────────┬───────────┘
                    │ uses
                    ▼
┌────────────────────────────────────┐
│       LendingService               │
├────────────────────────────────────┤
│ +checkoutBook()                    │
│ +returnBook()                      │
│ +renewBook()                       │
│ +getActiveBorrowings()             │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│     ReservationService             │
├────────────────────────────────────┤
│ +reserveBook()                     │
│ +cancelReservation()               │
│ +notifyNextInQueue()               │
└────────────────────────────────────┘

┌────────────────────────────────────┐
│    RecommendationService           │
├────────────────────────────────────┤
│ +getRecommendations()              │
│ +getPopularBooks()                 │
│ +getCollaborativeRecommendations() │
└────────────────────────────────────┘
```

## 📁 Project Structure

```
Library Management System LLD/
│
├── src/
│   └── com/
│       └── library/
│           ├── LibraryManagementSystem.java (Main Application)
│           │
│           ├── model/                      # Domain Entities
│           │   ├── Book.java
│           │   ├── Patron.java
│           │   ├── BorrowingRecord.java
│           │   ├── LibraryBranch.java
│           │   ├── Reservation.java
│           │   ├── BookStatus.java         (Enum)
│           │   ├── PatronType.java         (Enum)
│           │   └── ReservationStatus.java  (Enum)
│           │
│           ├── repository/                 # Data Access Layer
│           │   ├── BookRepository.java
│           │   ├── PatronRepository.java
│           │   ├── BorrowingRecordRepository.java
│           │   └── BranchRepository.java
│           │
│           ├── service/                    # Business Logic Layer
│           │   ├── BookService.java
│           │   ├── PatronService.java
│           │   ├── LendingService.java
│           │   ├── BranchService.java
│           │   ├── ReservationService.java
│           │   ├── BookTransferService.java
│           │   └── RecommendationService.java
│           │
│           ├── factory/                    # Factory Pattern
│           │   └── PatronFactory.java
│           │
│           ├── strategy/                   # Strategy Pattern
│           │   ├── SearchStrategy.java     (Interface)
│           │   ├── TitleSearchStrategy.java
│           │   ├── AuthorSearchStrategy.java
│           │   └── ISBNSearchStrategy.java
│           │
│           ├── observer/                   # Observer Pattern
│           │   ├── Observer.java           (Interface)
│           │   ├── Subject.java            (Interface)
│           │   ├── EmailNotificationObserver.java
│           │   └── SMSNotificationObserver.java
│           │
│           └── util/                       # Utility Classes
│               ├── Logger.java             (Singleton)
│               └── IdGenerator.java
│
└── README.md
```

## 🚀 Setup and Installation

### Prerequisites
- Java 11 or higher
- Git

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Library\ Managment\ System\ LLD
   ```

2. **Compile the project**
   ```bash
   javac -d bin src/com/library/**/*.java src/com/library/*.java
   ```

3. **Run the application**
   ```bash
   java -cp bin com.library.LibraryManagementSystem
   ```

### Alternative: Using an IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, or VS Code)
2. Set the source folder as `src`
3. Run `LibraryManagementSystem.java`

## 💻 Usage

### Basic Operations

#### 1. Add a Book
```java
Book book = new Book("978-0-13-468599-1", "Clean Code", 
                     "Robert C. Martin", 2008, branchId);
bookService.addBook(book);
```

#### 2. Create a Patron
```java
Patron student = PatronFactory.createStudent(
    "John Doe", "john@email.com", "555-0101"
);
patronService.addPatron(student);
```

#### 3. Checkout a Book
```java
BorrowingRecord record = lendingService.checkoutBook(
    patronId, isbn, branchId
);
```

#### 4. Search Books
```java
bookService.setSearchStrategy(new TitleSearchStrategy());
List<Book> results = bookService.searchBooks("Clean");
```

#### 5. Reserve a Book
```java
Reservation reservation = reservationService.reserveBook(
    patronId, isbn
);
```

#### 6. Get Recommendations
```java
List<Book> recommendations = recommendationService
    .getRecommendations(patronId, 5);
```

## 🔑 Key Components

### Models
- **Book**: Represents a book with ISBN, title, author, publication year, and status
- **Patron**: Represents a library member with borrowing history
- **BorrowingRecord**: Tracks book checkout and return information
- **LibraryBranch**: Represents a library branch location
- **Reservation**: Manages book reservations

### Services
- **BookService**: Manages book CRUD operations and search
- **PatronService**: Manages patron operations
- **LendingService**: Handles checkout, return, and renewal
- **ReservationService**: Manages book reservations and notifications
- **BranchService**: Manages library branches
- **BookTransferService**: Handles inter-branch book transfers
- **RecommendationService**: Provides book recommendations

### Enums
- **BookStatus**: AVAILABLE, CHECKED_OUT, RESERVED, MAINTENANCE, LOST
- **PatronType**: STUDENT (3 books, 14 days), FACULTY (10 books, 30 days), GENERAL (5 books, 21 days)
- **ReservationStatus**: ACTIVE, NOTIFIED, FULFILLED, CANCELLED, EXPIRED

## 📝 Design Decisions

1. **Immutable IDs**: Entity IDs (ISBN, PatronID, etc.) are final to prevent accidental modification
2. **Defensive Copying**: Collections are copied when returned to prevent external modification
3. **Repository Pattern**: Abstracts data storage for easy testing and future database integration
4. **Service Layer**: Business logic is separated from data access
5. **Strategy Pattern for Search**: Allows easy addition of new search criteria
6. **Observer Pattern for Notifications**: Decouples notification logic from business logic

## 🧪 Testing

The main class (`LibraryManagementSystem.java`) includes comprehensive demonstrations of all features:
- Book and patron management
- Checkout and return operations
- Reservation system
- Multi-branch operations
- Book transfers
- Recommendation system
- Search functionality

---

**Note**: This is an in-memory implementation for demonstration purposes.
