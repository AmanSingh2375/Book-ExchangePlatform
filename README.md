# 📚 Book Exchange Platform

A web-based platform that allows students to list their books and exchange books with other students. The project is built using Java, Spring Boot, MySQL, and Data Structures and Algorithms (DSA).

## ✨ Features

* **User Registration & Login** – Users can register, log in, and log out.
* **Book Listing** – Add books with details such as title, author, and category.
* **Book Browsing** – View available books listed on the platform.
* **Exchange Requests** – Send requests to exchange books and accept or reject incoming requests.
* **Session Management** – Maintain user login sessions.
* **Password Encryption** – Store passwords using BCrypt hashing.
* **Data Structures** – Implement a Binary Search Tree (BST) and graph traversal algorithms.

## 🛠️ Technologies Used

* **Backend:** Java, Spring Boot
* **Frontend:** HTML, CSS, JavaScript
* **Database:** MySQL
* **Data Access:** Spring Data JPA, Hibernate
* **Security:** BCrypt password hashing
* **Build Tool:** Maven

## 🧠 DSA Concepts

* **Binary Search Tree (BST):** Used to organize book data and support tree-based operations.
* **Graph:** Represents relationships between books or users using an adjacency list.
* **BFS (Breadth-First Search):** Traverses graph nodes level by level.
* **DFS (Depth-First Search):** Explores graph paths depth-first.

## ⚙️ Setup and Installation

### Prerequisites

* Java (compatible with the version configured in the project)
* MySQL
* Git

### 1. Clone the repository

```bash
git clone https://github.com/AmanSingh2375/Book-ExchangePlatform.git
cd Book-ExchangePlatform
```

### 2. Create the database

Open MySQL and run:

```sql
CREATE DATABASE bookexchange;
```

### 3. Configure database credentials

In `src/main/resources/application.properties`, configure your MySQL username and use an environment variable for the password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookexchange
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}
```

Set your password in the terminal before starting the application. In Windows PowerShell:

```powershell
$env:DB_PASSWORD="your_mysql_password"
```

Replace the example value with your own local MySQL password. Never commit real passwords to GitHub.

### 4. Run the application

On Windows, run:

```powershell
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080** in your browser.

## 🎯 Project Objective

The goal of this project is to provide a simple platform for students to exchange books while applying Java backend development, database integration, and fundamental DSA concepts.

## 🚀 Future Improvements

* Improve graph-based book and user recommendations.
* Add book search and filtering options.
* Improve request management and user interface.

## 👨‍💻 Author

**Aman Singh**

GitHub: [@AmanSingh2375](https://github.com/AmanSingh2375)

---

*Developed as a student project to practice full-stack web development and Data Structures and Algorithms.*
