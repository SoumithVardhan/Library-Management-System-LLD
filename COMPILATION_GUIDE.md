# Compilation and Execution Guide

## Prerequisites
- Java Development Kit (JDK) 11 or higher
- Command line terminal (Terminal on macOS/Linux, Command Prompt or PowerShell on Windows)

## Method 1: Command Line Compilation and Execution

### Step 1: Navigate to the project directory
```bash
cd "Library Managment System LLD"
```

### Step 2: Create a bin directory for compiled classes
```bash
mkdir -p bin
```

### Step 3: Compile all Java files
```bash
javac -d bin -sourcepath src src/com/library/*.java src/com/library/*/*.java
```

Or compile each package separately:
```bash
# Compile model classes
javac -d bin src/com/library/model/*.java

# Compile utility classes
javac -d bin -cp bin src/com/library/util/*.java

# Compile observer pattern
javac -d bin -cp bin src/com/library/observer/*.java

# Compile strategy pattern
javac -d bin -cp bin src/com/library/strategy/*.java

# Compile factory pattern
javac -d bin -cp bin src/com/library/factory/*.java

# Compile repositories
javac -d bin -cp bin src/com/library/repository/*.java

# Compile services
javac -d bin -cp bin src/com/library/service/*.java

# Compile main class
javac -d bin -cp bin src/com/library/LibraryManagementSystem.java
```

### Step 4: Run the application
```bash
java -cp bin com.library.LibraryManagementSystem
```

## Method 2: Using IntelliJ IDEA

### Step 1: Open IntelliJ IDEA
- Launch IntelliJ IDEA

### Step 2: Open Project
- Click "Open" or "Open Project"
- Navigate to "Library Managment System LLD" folder
- Click "Open"

### Step 3: Set up Project Structure
- Go to File → Project Structure (Cmd+; on Mac, Ctrl+Alt+Shift+S on Windows)
- Under "Project Settings" → "Project":
  - Set Project SDK to Java 11 or higher
  - Set Language level to 11 or higher
- Under "Project Settings" → "Modules":
  - Mark `src` folder as "Sources" (should be blue)
- Click "OK"

### Step 4: Run the Application
- Locate `LibraryManagementSystem.java` in the Project Explorer
- Right-click on the file
- Select "Run 'LibraryManagementSystem.main()'"

## Method 3: Using Eclipse

### Step 1: Open Eclipse
- Launch Eclipse IDE

### Step 2: Import Project
- Go to File → New → Java Project
- Uncheck "Use default location"
- Browse to "Library Managment System LLD" folder
- Click "Finish"

### Step 3: Set up Build Path
- Right-click on the project in Package Explorer
- Select "Build Path" → "Configure Build Path"
- Under "Source" tab, ensure `src` folder is listed
- Click "Apply and Close"

### Step 4: Run the Application
- Locate `LibraryManagementSystem.java` in Package Explorer
- Right-click on the file
- Select "Run As" → "Java Application"

## Method 4: Using Visual Studio Code

### Step 1: Open VS Code
- Launch Visual Studio Code

### Step 2: Install Java Extension Pack
- Go to Extensions (Cmd+Shift+X on Mac, Ctrl+Shift+X on Windows)
- Search for "Extension Pack for Java"
- Install the extension pack

### Step 3: Open Project
- Go to File → Open Folder
- Navigate to and select "Library Managment System LLD" folder
- Click "Open"

### Step 4: Run the Application
- Locate `LibraryManagementSystem.java` in the Explorer panel
- Right-click on the file
- Select "Run Java"

Or use the Run button in the top right corner when viewing the file.

## Expected Output

When you run the application, you should see output similar to:

```
[2025-01-XX XX:XX:XX] [INFO] === Library Management System Started ===

[2025-01-XX XX:XX:XX] [INFO] --- Creating Library Branches ---
[2025-01-XX XX:XX:XX] [INFO] Branch created: Main Library (ID: BR-XXXXXXXX)
[2025-01-XX XX:XX:XX] [INFO] Branch created: East Branch (ID: BR-XXXXXXXX)

[2025-01-XX XX:XX:XX] [INFO] --- Adding Books to Inventory ---
[2025-01-XX XX:XX:XX] [INFO] Book added: Clean Code (ISBN: 978-0-13-468599-1)
...

[2025-01-XX XX:XX:XX] [INFO] --- Creating Patrons (Factory Pattern) ---
[2025-01-XX XX:XX:XX] [INFO] Patron added: Alice Johnson (ID: PT-XXXXXXXX)
...

[2025-01-XX XX:XX:XX] [INFO] --- Book Checkout Process ---
[2025-01-XX XX:XX:XX] [INFO] Book checked out: Clean Code to Alice Johnson
📧 Email sent to alice@email.com: Book 'Clean Code' checked out successfully...
📱 SMS sent to 555-0101: Book 'Clean Code' checked out successfully...
✅ Checkout successful! Due date: 2025-XX-XX

...

[2025-01-XX XX:XX:XX] [INFO] === Library Management System Demo Completed Successfully ===
```

## Troubleshooting

### Error: "javac: command not found"
- Install JDK if not already installed
- Ensure JAVA_HOME is set correctly
- Add Java bin directory to PATH

### Error: "class not found"
- Ensure you're in the correct directory
- Check that bin directory exists and contains compiled .class files
- Verify classpath is set correctly with `-cp bin`

### Error: "package com.library does not exist"
- Ensure source files are in correct package structure
- Verify compilation was successful
- Check that all dependencies are compiled

## Clean Build

To clean and rebuild the project:

```bash
# Remove compiled files
rm -rf bin

# Recreate bin directory
mkdir bin

# Recompile
javac -d bin -sourcepath src src/com/library/*.java src/com/library/*/*.java

# Run
java -cp bin com.library.LibraryManagementSystem
```

## Creating a JAR File (Optional)

### Step 1: Compile the project (if not already done)
```bash
javac -d bin -sourcepath src src/com/library/*.java src/com/library/*/*.java
```

### Step 2: Create a manifest file
```bash
echo "Main-Class: com.library.LibraryManagementSystem" > manifest.txt
```

### Step 3: Create JAR
```bash
jar cfm LibraryManagementSystem.jar manifest.txt -C bin .
```

### Step 4: Run JAR
```bash
java -jar LibraryManagementSystem.jar
```

## Notes
- The application runs in-memory and does not persist data
- All logs are printed to console
- The demo showcases all major features of the system
- No external dependencies are required
