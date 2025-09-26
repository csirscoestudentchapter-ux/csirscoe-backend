# CSI Backend - User Management System

This is a Spring Boot application that provides a user management system with predefined users and admin functionality.

## Features

- **Predefined Users Only**: No self-registration from the website
- **Admin Management**: Only admins can add/update/delete users
- **Secure Authentication**: Email + password login with BCrypt hashing
- **User Dashboard**: Display user details after successful login
- **Admin Panel**: Complete CRUD operations for user management
- **Password Reset**: Admins can reset user passwords

## User Model

Each user has the following fields:
- `id` (auto-generated)
- `name` (required)
- `email` (required, unique)
- `password` (required, hashed)
- `role` (required, "admin" or "user")

## Password Requirements

- **Admin users**: Password is always `admin@123`
- **Regular users**: Password is always `pass@123`
- All passwords are hashed using BCrypt for security

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login

### Admin User Management
- `GET /api/admin/users` - Get all users
- `GET /api/admin/users/{id}` - Get user by ID
- `POST /api/admin/users` - Create new user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `POST /api/admin/users/{id}/reset-password` - Reset user password

## Setup Instructions

1. **Database Setup**:
   - Ensure MySQL is running on localhost:3306
   - Create database named `csi_db`
   - Update database credentials in `application.properties` if needed

2. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Default Admin User**:
   - Email: `admin@csi.com`
   - Password: `admin@123`
   - This user is automatically created when the application starts

## Frontend Testing

1. Open `http://localhost:8080` in your browser
2. Use the provided HTML interface to test the system
3. Login with the default admin credentials
4. Use the admin panel to manage users

## Security Features

- **Password Hashing**: All passwords are hashed using BCrypt
- **No Self-Registration**: Users can only be created by admins
- **Input Validation**: Email uniqueness and required field validation
- **CORS Configuration**: Configured for frontend integration

## Database Schema

The application uses JPA/Hibernate with automatic schema generation. The `users` table will be created automatically with the following structure:

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
);
```

## Password Management

- Admin users always have password: `admin@123`
- Regular users always have password: `pass@123`
- Passwords are never stored in plain text
- Password reset functionality restores the default passwords based on role

## Production Considerations

1. **Security**: Implement proper JWT authentication
2. **Email Integration**: Send password reset emails
3. **Logging**: Add proper logging for user actions
4. **Validation**: Add more comprehensive input validation
5. **Rate Limiting**: Implement rate limiting for login attempts
6. **HTTPS**: Use HTTPS in production
7. **Environment Variables**: Move sensitive data to environment variables
