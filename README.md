# Re-create the README.md content after execution state reset
readme_content = """
# Inventory Management System

## 📖 Description
The **Inventory Management System** is a web application designed to manage inventory efficiently. It combines **Spring Boot** for the backend and **Angular** for the frontend, supporting functionalities such as viewing inventory, adding new products, and editing existing information.

---

## 🚀 Technologies Used

### Backend
- **Spring Boot 3.x**
- **Java 17+**
- **Hibernate JPA**
- **MySQL** for data storage

### Frontend
- **Angular 16**
- **Bootstrap 5** for the user interface

---

## 🛠️ Prerequisites

Before starting, make sure you have the following installed:

1. [Node.js](https://nodejs.org/) and npm (for the Angular frontend)
2. [Java 17+](https://adoptopenjdk.net/) (for the Spring Boot backend)
3. [Gradle](https://gradle.org/) (for building the backend)
4. [MySQL](https://www.mysql.com/) (for the database)

---

## ⚙️ How to Run the Application

### Backend (Spring Boot)
1. Navigate to the backend directory:
   ```bash
   cd Back-end/

    Build the backend using Gradle:

Να εμφανίζονται πάντα οι λεπτομέρειες

./gradlew build

Run the backend:

Να εμφανίζονται πάντα οι λεπτομέρειες

    java -jar build/libs/inventory-managment-system-0.0.1-SNAPSHOT.jar

    Ensure that your MySQL database is running and configured as per the application.properties or application-dev.properties files.

Frontend (Angular)

    Navigate to the frontend directory:

Να εμφανίζονται πάντα οι λεπτομέρειες

cd Front-end_v16/

Install dependencies:

Να εμφανίζονται πάντα οι λεπτομέρειες

npm install

Run the frontend:

Να εμφανίζονται πάντα οι λεπτομέρειες

    ng serve

    Open the application in your browser at http://localhost:4200.

🛢️ Database Configuration

The application uses MySQL for storing data. Update the application.properties or application-dev.properties file in the backend directory with your database details:

Να εμφανίζονται πάντα οι λεπτομέρειες

spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password

🖥️ Deployment
Backend Deployment

    Deploy the generated .jar file to your server.
    Make sure the database is properly configured and accessible.

Frontend Deployment

    Build the Angular application:

Να εμφανίζονται πάντα οι λεπτομέρειες

    ng build --prod

    Host the contents of the dist/ folder on a web server (e.g., Nginx, Apache).

📂 Project Structure

Να εμφανίζονται πάντα οι λεπτομέρειες

InventoryManagementSystem/
├── Back-end/                # Spring Boot backend
│   ├── src/                 # Java source code
│   ├── build.gradle         # Gradle build file
│   └── application.properties
├── Front-end_v16/           # Angular frontend
│   ├── src/                 # Angular source code
│   ├── angular.json         # Angular configuration
│   └── package.json         # Node.js dependencies
└── README.md                # Project documentation

🤝 Contributing

Contributions are welcome! Please fork this repository, make your changes, and submit a pull request.
📄 License

This project is licensed under the MIT License. See the LICENSE file for more details. """
Save the content to a file

file_path = "/mnt/data/README.md" with open(file_path, "w") as file: file.write(readme_content)
