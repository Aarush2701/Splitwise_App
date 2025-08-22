SplitWise – Expense Sharing App 💸

A Spring Boot + React full-stack application that simplifies expense sharing among groups and friends. The app allows users to create groups, add expenses, track balances, and settle dues efficiently, with authentication powered by JWT.

🚀 Features

User Management: Sign up, login (email/phone), update profile.

Authentication & Security:

JWT-based login with refresh token support.

Role-based access & user-level authorization.

Optional Google OAuth2 login.

Groups:

Create and manage groups.

Add or remove members (with balance validation).

Expenses:

Add expenses with multiple split strategies:

Equal Split

Exact Amount Split

Percentage Split

View group expenses history.

Update/Delete expenses.

Balances:

View user/group balances in real-time.

Settlements:

Track unpaid dues.

Settle balances with group members.

Frontend:

Dashboard with Profile, Groups, and Settlements.

Group details page with tabs for expenses, members, balances, and settlements.

Add Expense page with dynamic participant/payer selection.

🛠️ Tech Stack

Backend:

Spring Boot (Java)

Spring Security (JWT authentication)

MySQL (Database)

JPA/Hibernate

Render (backend hosting)

Frontend:

React (CRA)

TailwindCSS + Material UI

Axios (API calls)

Render (frontend hosting)

⚙️ Setup Instructions
Backend (Spring Boot)

Clone the repo:

git clone https://github.com/your-username/splitwise-backend.git
cd splitwise-backend


Configure application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/splitwise
spring.datasource.username=your_db_user
spring.datasource.password=your_db_pass
spring.jpa.hibernate.ddl-auto=update
jwt.secret=your_secret_key


Run the app:

mvn spring-boot:run


App will run at: http://localhost:8080

Frontend (React)

Clone the repo:

git clone https://github.com/your-username/splitwise-frontend.git
cd splitwise-frontend


Install dependencies:

npm install


Start the app:

npm start


App will run at: http://localhost:3000

📖 API Endpoints (Backend)
AuthController

POST /auth/signup → Register a new user

POST /auth/login → Login with email/phone + password

POST /auth/refresh → Refresh JWT

UserController

GET /users/{id} → Get user details

PUT /users/{id} → Update user

GroupController

POST /groups → Create group

GET /groups/{id} → Get group details

DELETE /groups/{id} → Delete group

ExpenseController

POST /groups/{groupid}/expenses → Add expense

PUT /groups/{groupid}/expenses/{expenseid} → Update expense

DELETE /groups/{groupid}/expenses/{expenseid} → Delete expense

GET /groups/{groupid}/expenses → View expenses

BalanceController

GET /groups/{groupid}/balances → View group balances

SettlementController

GET /groups/{groupid}/settlements → View group settlements

POST /groups/{groupid}/settle → Settle dues

🎨 Frontend Highlights

Groups Dashboard: List all groups with details & actions.

Group Details Page: Tabs for Expenses, Members, Balance, and Settlements.

Add Expense Form: Select participants, payer, and split method.

Profile Section: View and update user details.

🤝 Contribution

Fork the project

Create a feature branch (git checkout -b feature/awesome-feature)

Commit changes (git commit -m 'Add awesome feature')

Push branch (git push origin feature/awesome-feature)

Open a Pull Request

📜 License

This project is licensed under the MIT License.
