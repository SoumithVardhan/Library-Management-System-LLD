# Library Management System - Class Diagram

## Complete Class Diagram

### Model Layer

```
┌─────────────────────────────────────────────────────────────────────┐
│                           MODEL CLASSES                              │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│           Book               │
├──────────────────────────────┤
│ - isbn: String (final)       │
│ - title: String              │
│ - author: String             │
│ - publicationYear: int       │
│ - status: BookStatus         │
│ - currentBranchId: String    │
├──────────────────────────────┤
│ + Book(isbn, title, author,  │
│   publicationYear, branchId) │
│ + getIsbn(): String          │
│ + getTitle(): String         │
│ + getAuthor(): String        │
│ + getPublicationYear(): int  │
│ + getStatus(): BookStatus    │
│ + getCurrentBranchId(): String│
│ + setTitle(String): void     │
│ + setAuthor(String): void    │
│ + setPublicationYear(int): void│
│ + setStatus(BookStatus): void│
│ + setCurrentBranchId(String): void│
│ + equals(Object): boolean    │
│ + hashCode(): int            │
│ + toString(): String         │
└──────────────────────────────┘

┌──────────────────────────────┐
│          Patron              │
├──────────────────────────────┤
│ - patronId: String (final)   │
│ - name: String               │
│ - email: String              │
│ - phone: String              │
│ - borrowingHistory: List     │
│ - currentBorrowedBooks: List │
│ - reservedBooks: List        │
│ - patronType: PatronType     │
├──────────────────────────────┤
│ + Patron(patronId, name,     │
│   email, phone, patronType)  │
│ + getPatronId(): String      │
│ + getName(): String          │
│ + getEmail(): String         │
│ + getPhone(): String         │
│ + getBorrowingHistory(): List│
│ + getCurrentBorrowedBooks(): List│
│ + getReservedBooks(): List   │
│ + getPatronType(): PatronType│
│ + setName(String): void      │
│ + setEmail(String): void     │
│ + setPhone(String): void     │
│ + setPatronType(PatronType): void│
│ + addBorrowingRecord(BorrowingRecord): void│
│ + addCurrentBorrowedBook(String): void│
│ + removeCurrentBorrowedBook(String): void│
│ + addReservedBook(String): void│
│ + removeReservedBook(String): void│
│ + canBorrowMoreBooks(): boolean│
│ + equals(Object): boolean    │
│ + hashCode(): int            │
│ + toString(): String         │
└──────────────────────────────┘

┌──────────────────────────────┐
│      BorrowingRecord         │
├──────────────────────────────┤
│ - recordId: String (final)   │
│ - patronId: String (final)   │
│ - isbn: String (final)       │
│ - checkoutDate: LocalDate (final)│
│ - dueDate: LocalDate         │
│ - returnDate: LocalDate      │
│ - branchId: String           │
├──────────────────────────────┤
│ + BorrowingRecord(recordId,  │
│   patronId, isbn, checkoutDate,│
│   dueDate, branchId)         │
│ + getRecordId(): String      │
│ + getPatronId(): String      │
│ + getIsbn(): String          │
│ + getCheckoutDate(): LocalDate│
│ + getDueDate(): LocalDate    │
│ + getReturnDate(): LocalDate │
│ + getBranchId(): String      │
│ + setDueDate(LocalDate): void│
│ + setReturnDate(LocalDate): void│
│ + isOverdue(): boolean       │
│ + isReturned(): boolean      │
│ + equals(Object): boolean    │
│ + hashCode(): int            │
│ + toString(): String         │
└──────────────────────────────┘

┌──────────────────────────────┐
│       LibraryBranch          │
├──────────────────────────────┤
│ - branchId: String (final)   │
│ - branchName: String         │
│ - address: String            │
│ - inventory: List<String>    │
├──────────────────────────────┤
│ + LibraryBranch(branchId,    │
│   branchName, address)       │
│ + getBranchId(): String      │
│ + getBranchName(): String    │
│ + getAddress(): String       │
│ + getInventory(): List       │
│ + setBranchName(String): void│
│ + setAddress(String): void   │
│ + addBookToInventory(String): void│
│ + removeBookFromInventory(String): void│
│ + hasBook(String): boolean   │
│ + equals(Object): boolean    │
│ + hashCode(): int            │
│ + toString(): String         │
└──────────────────────────────┘

┌──────────────────────────────┐
│         Reservation          │
├──────────────────────────────┤
│ - reservationId: String (final)│
│ - patronId: String (final)   │
│ - isbn: String (final)       │
│ - reservationDate: LocalDateTime (final)│
│ - status: ReservationStatus  │
│ - notificationSentDate: LocalDateTime│
├──────────────────────────────┤
│ + Reservation(reservationId, │
│   patronId, isbn)            │
│ + getReservationId(): String │
│ + getPatronId(): String      │
│ + getIsbn(): String          │
│ + getReservationDate(): LocalDateTime│
│ + getStatus(): ReservationStatus│
│ + getNotificationSentDate(): LocalDateTime│
│ + setStatus(ReservationStatus): void│
│ + setNotificationSentDate(LocalDateTime): void│
│ + equals(Object): boolean    │
│ + hashCode(): int            │
│ + toString(): String         │
└──────────────────────────────┘

┌──────────────────────────────┐
│      <<enumeration>>         │
│        BookStatus            │
├──────────────────────────────┤
│ AVAILABLE                    │
│ CHECKED_OUT                  │
│ RESERVED                     │
│ MAINTENANCE                  │
│ LOST                         │
└──────────────────────────────┘

┌──────────────────────────────┐
│      <<enumeration>>         │
│        PatronType            │
├──────────────────────────────┤
│ STUDENT(3, 14)               │
│ FACULTY(10, 30)              │
│ GENERAL(5, 21)               │
├──────────────────────────────┤
│ - maxBooksAllowed: int       │
│ - maxBorrowDays: int         │
├──────────────────────────────┤
│ + getMaxBooksAllowed(): int  │
│ + getMaxBorrowDays(): int    │
└──────────────────────────────┘

┌──────────────────────────────┐
│      <<enumeration>>         │
│     ReservationStatus        │
├──────────────────────────────┤
│ ACTIVE                       │
│ NOTIFIED                     │
│ FULFILLED                    │
│ CANCELLED                    │
│ EXPIRED                      │
└──────────────────────────────┘
```

### Repository Layer

```
┌─────────────────────────────────────────────────────────────────────┐
│                        REPOSITORY CLASSES                            │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│      BookRepository          │
├──────────────────────────────┤
│ - books: Map<String, Book>   │
├──────────────────────────────┤
│ + save(Book): void           │
│ + findByIsbn(String): Optional<Book>│
│ + findAll(): List<Book>      │
│ + findByBranch(String): List<Book>│
│ + findByStatus(BookStatus): List<Book>│
│ + delete(String): boolean    │
│ + exists(String): boolean    │
│ + count(): int               │
└──────────────────────────────┘

┌──────────────────────────────┐
│     PatronRepository         │
├──────────────────────────────┤
│ - patrons: Map<String, Patron>│
├──────────────────────────────┤
│ + save(Patron): void         │
│ + findById(String): Optional<Patron>│
│ + findAll(): List<Patron>    │
│ + delete(String): boolean    │
│ + exists(String): boolean    │
│ + count(): int               │
└──────────────────────────────┘

┌──────────────────────────────┐
│ BorrowingRecordRepository    │
├──────────────────────────────┤
│ - records: Map<String, BorrowingRecord>│
├──────────────────────────────┤
│ + save(BorrowingRecord): void│
│ + findById(String): Optional<BorrowingRecord>│
│ + findAll(): List<BorrowingRecord>│
│ + findByPatronId(String): List<BorrowingRecord>│
│ + findByIsbn(String): List<BorrowingRecord>│
│ + findActiveRecords(): List<BorrowingRecord>│
│ + findOverdueRecords(): List<BorrowingRecord>│
└──────────────────────────────┘

┌──────────────────────────────┐
│     BranchRepository         │
├──────────────────────────────┤
│ - branches: Map<String, LibraryBranch>│
├──────────────────────────────┤
│ + save(LibraryBranch): void  │
│ + findById(String): Optional<LibraryBranch>│
│ + findAll(): List<LibraryBranch>│
│ + delete(String): boolean    │
│ + exists(String): boolean    │
└──────────────────────────────┘
```

### Service Layer

```
┌─────────────────────────────────────────────────────────────────────┐
│                         SERVICE CLASSES                              │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│        BookService           │
├──────────────────────────────┤
│ - bookRepository: BookRepository│
│ - logger: Logger             │
│ - searchStrategy: SearchStrategy│
├──────────────────────────────┤
│ + addBook(Book): void        │
│ + updateBook(String, String, String, int): void│
│ + removeBook(String): void   │
│ + findBookByIsbn(String): Optional<Book>│
│ + getAllBooks(): List<Book>  │
│ + getBooksByBranch(String): List<Book>│
│ + getAvailableBooks(): List<Book>│
│ + setSearchStrategy(SearchStrategy): void│
│ + searchBooks(String): List<Book>│
│ + updateBookStatus(String, BookStatus): void│
│ + getTotalBookCount(): int   │
└──────────────────────────────┘

┌──────────────────────────────┐
│       PatronService          │
├──────────────────────────────┤
│ - patronRepository: PatronRepository│
│ - logger: Logger             │
├──────────────────────────────┤
│ + addPatron(Patron): void    │
│ + updatePatron(String, String, String, String): void│
│ + updatePatronType(String, PatronType): void│
│ + findPatronById(String): Optional<Patron>│
│ + getAllPatrons(): List<Patron>│
│ + removePatron(String): void │
│ + getTotalPatronCount(): int │
└──────────────────────────────┘

┌──────────────────────────────┐
│      LendingService          │
│      <<implements Subject>>  │
├──────────────────────────────┤
│ - bookRepository: BookRepository│
│ - patronRepository: PatronRepository│
│ - recordRepository: BorrowingRecordRepository│
│ - logger: Logger             │
│ - observers: List<Observer>  │
├──────────────────────────────┤
│ + checkoutBook(String, String, String): BorrowingRecord│
│ + returnBook(String, String): void│
│ + getPatronBorrowingHistory(String): List<BorrowingRecord>│
│ + getActiveBorrowings(): List<BorrowingRecord>│
│ + getOverdueBorrowings(): List<BorrowingRecord>│
│ + renewBook(String, String): void│
│ + attach(Observer): void     │
│ + detach(Observer): void     │
│ + notifyObservers(String): void│
└──────────────────────────────┘

┌──────────────────────────────┐
│      BranchService           │
├──────────────────────────────┤
│ - branchRepository: BranchRepository│
│ - logger: Logger             │
├──────────────────────────────┤
│ + createBranch(String, String): LibraryBranch│
│ + updateBranch(String, String, String): void│
│ + getBranchById(String): Optional<LibraryBranch>│
│ + getAllBranches(): List<LibraryBranch>│
│ + deleteBranch(String): void │
└──────────────────────────────┘

┌──────────────────────────────┐
│    ReservationService        │
│    <<implements Subject>>    │
├──────────────────────────────┤
│ - bookRepository: BookRepository│
│ - patronRepository: PatronRepository│
│ - reservationQueues: Map     │
│ - reservationById: Map       │
│ - logger: Logger             │
│ - observers: List<Observer>  │
├──────────────────────────────┤
│ + reserveBook(String, String): Reservation│
│ + cancelReservation(String): void│
│ + notifyNextInQueue(String): void│
│ + fulfillReservation(String, String): void│
│ + getReservationsForBook(String): List<Reservation>│
│ + getReservationsForPatron(String): List<Reservation>│
│ + getQueuePosition(String): int│
│ + attach(Observer): void     │
│ + detach(Observer): void     │
│ + notifyObservers(String): void│
└──────────────────────────────┘

┌──────────────────────────────┐
│   BookTransferService        │
├──────────────────────────────┤
│ - bookRepository: BookRepository│
│ - branchRepository: BranchRepository│
│ - logger: Logger             │
├──────────────────────────────┤
│ + transferBook(String, String, String): void│
│ + canTransferBook(String, String): boolean│
└──────────────────────────────┘

┌──────────────────────────────┐
│   RecommendationService      │
├──────────────────────────────┤
│ - bookRepository: BookRepository│
│ - patronRepository: PatronRepository│
│ - recordRepository: BorrowingRecordRepository│
│ - logger: Logger             │
├──────────────────────────────┤
│ + getRecommendations(String, int): List<Book>│
│ + getCollaborativeRecommendations(String, int): List<Book>│
│ + getPopularBooks(int): List<Book>│
│ + getRecommendationsByAuthor(String, String, int): List<Book>│
└──────────────────────────────┘
```

### Design Patterns

```
┌─────────────────────────────────────────────────────────────────────┐
│                      DESIGN PATTERN CLASSES                          │
└─────────────────────────────────────────────────────────────────────┘

STRATEGY PATTERN
─────────────────
┌──────────────────────────────┐
│      <<interface>>           │
│      SearchStrategy          │
├──────────────────────────────┤
│ + search(List<Book>, String): List<Book>│
└──────────────────────────────┘
            △
            │ implements
            ├─────────────────────┐
            │                     │
┌───────────────────┐  ┌──────────────────┐
│ TitleSearchStrategy│  │AuthorSearchStrategy│
├───────────────────┤  ├──────────────────┤
│ + search(): List  │  │ + search(): List │
└───────────────────┘  └──────────────────┘
            │
┌───────────────────┐
│ ISBNSearchStrategy│
├───────────────────┤
│ + search(): List  │
└───────────────────┘

OBSERVER PATTERN
────────────────
┌──────────────────────────────┐
│      <<interface>>           │
│        Observer              │
├──────────────────────────────┤
│ + update(String): void       │
└──────────────────────────────┘
            △
            │ implements
            ├─────────────────────┐
            │                     │
┌───────────────────────┐ ┌──────────────────────┐
│EmailNotificationObserver│ │SMSNotificationObserver│
├───────────────────────┤ ├──────────────────────┤
│ - email: String       │ │ - phoneNumber: String│
│ - logger: Logger      │ │ - logger: Logger     │
├───────────────────────┤ ├──────────────────────┤
│ + update(String): void│ │ + update(String): void│
└───────────────────────┘ └──────────────────────┘

┌──────────────────────────────┐
│      <<interface>>           │
│         Subject              │
├──────────────────────────────┤
│ + attach(Observer): void     │
│ + detach(Observer): void     │
│ + notifyObservers(String): void│
└──────────────────────────────┘
            △
            │ implements
            ├─────────────────────┐
            │                     │
┌──────────────────┐  ┌──────────────────┐
│ LendingService   │  │ReservationService│
└──────────────────┘  └──────────────────┘

FACTORY PATTERN
───────────────
┌──────────────────────────────┐
│      PatronFactory           │
├──────────────────────────────┤
│ + createPatron(String, String, String, PatronType): Patron│
│ + createStudent(String, String, String): Patron│
│ + createFaculty(String, String, String): Patron│
│ + createGeneralMember(String, String, String): Patron│
└──────────────────────────────┘
            │ creates
            ▼
┌──────────────────────────────┐
│          Patron              │
└──────────────────────────────┘

SINGLETON PATTERN
─────────────────
┌──────────────────────────────┐
│          Logger              │
├──────────────────────────────┤
│ - instance: Logger (static)  │
│ - formatter: DateTimeFormatter│
├──────────────────────────────┤
│ - Logger()                   │
│ + getInstance(): Logger (static)│
│ + info(String): void         │
│ + warn(String): void         │
│ + error(String): void        │
│ + debug(String): void        │
└──────────────────────────────┘
```

### Utility Classes

```
┌─────────────────────────────────────────────────────────────────────┐
│                        UTILITY CLASSES                               │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│        IdGenerator           │
├──────────────────────────────┤
│ + generateBookId(): String (static)│
│ + generatePatronId(): String (static)│
│ + generateBranchId(): String (static)│
│ + generateRecordId(): String (static)│
│ + generateReservationId(): String (static)│
└──────────────────────────────┘
```

## Relationships Summary

### Composition Relationships
- LibraryManagementSystem **has** BookService, PatronService, LendingService, etc.
- BookService **has** BookRepository
- PatronService **has** PatronRepository
- Patron **has** List of BorrowingRecord
- LibraryBranch **has** List of Book ISBNs

### Association Relationships
- BorrowingRecord **associates** Patron and Book
- Reservation **associates** Patron and Book
- Book **belongs to** LibraryBranch

### Dependency Relationships
- Services **depend on** Repositories
- Observers **depend on** Subject
- Strategies **are used by** BookService

### Inheritance/Implementation
- TitleSearchStrategy, AuthorSearchStrategy, ISBNSearchStrategy **implement** SearchStrategy
- EmailNotificationObserver, SMSNotificationObserver **implement** Observer
- LendingService, ReservationService **implement** Subject

## Design Pattern Summary

1. **Factory Pattern**: PatronFactory creates different types of patrons
2. **Strategy Pattern**: Different search strategies for book search
3. **Observer Pattern**: Notification system for lending and reservations
4. **Singleton Pattern**: Logger ensures single instance
5. **Repository Pattern**: Abstract data access layer
