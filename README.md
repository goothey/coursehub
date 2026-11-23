# CourseHub

CPAN-228 Web Application Development. CourseHub is a learning platform where students browse courses across programming, design, business, and math. Instructors and students can add new courses, and the catalogue can be filtered, sorted, and paginated.

## Features (Deliverable 1)
- **Home page** at `/` with links into the app
- **Browse Courses** at `/courses`: filter by category and difficulty, sort by title or price, paginated results
- **Add a Course** at `/courses/new`: validated form with plain-language error messages
- **Course details** at `/courses/{id}`, shown after a course is saved
- **About** at `/about` and **FAQ** at `/faq`
- Sample data: 12 courses loaded from `data.sql` on startup

## Tech stack
- Java 26, Spring Boot 4.1.0
- Spring MVC, Thymeleaf, Spring Data JPA, Bean Validation
- H2 in-memory database (development)
- Bootstrap 5 for styling

## Running the app
Requirements: Java 26 and Maven 3.6.3 or later.

```
mvn spring-boot:run
```

Then open http://localhost:8080. The H2 console is at http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:coursehub`, user `sa`, no password).

## Running the tests
```
mvn test
```
The validation tests in `CourseValidationTest` check that bad form input is rejected with clear messages, and that valid input is saved and redirected.

## Team contributions
| Team member | Parts built |
|---|---|
| [Name 1] | [e.g. Course entity, repository, sample data] |
| [Name 2] | [e.g. Add Course form and validation] |
| [Name 3] | [e.g. Browse page with filters, sorting, and pagination] |
| [Name 4] | [e.g. Home, About, FAQ pages and navbar] |

Replace the placeholders above with your team's names and the parts each person built. Commit history on GitHub should match this table.

## Project status
Deliverable 1 (Front-End & Database Integration) is complete. Deliverable 2 (Security & User Management) and Deliverable 3 (Microservices, REST APIs & DevOps) are not yet started.
