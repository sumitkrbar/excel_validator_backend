# Excel Validator

A Spring Boot application that validates and processes Excel files containing participant data. The application performs comprehensive validation, highlights errors, and stores valid records in a PostgreSQL database with JWT-based authentication.

## Features

- **Excel File Validation**: Validates Excel files (.xls/.xlsx) with specific sheet structure requirements
- **Column Validation**: Ensures required columns are present with proper naming
- **Data Validation**: Validates individual cell data including:
  - Email format validation
  - Phone number format with country codes (+91, +44, +1, +33, +49)
  - Alphanumeric name validation
  - Relationship type validation (Peer, Manager, Direct Report, Customer)
  - Boolean family relationship validation
- **Duplicate Detection**: Identifies duplicate seeker emails, provider emails, and phone numbers
- **Error Highlighting**: Returns Excel files with highlighted error cells and comments
- **Database Storage**: Stores validated records in PostgreSQL
- **JWT Authentication**: Secure API endpoints with token-based authentication
- **Comprehensive Logging**: Detailed logging with Logback configuration

## Technology Stack

- **Java**: 21
- **Spring Boot**: 4.0.2
- **Spring Security**: JWT-based authentication
- **Spring Data JPA**: Database operations
- **PostgreSQL**: Database
- **Apache POI**: Excel file processing (5.2.5)
- **Lombok**: Boilerplate code reduction
- **Maven**: Build tool
- **Logback**: Logging framework

## Prerequisites

- JDK 21 or higher
- PostgreSQL database
- Maven 3.6+

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE excel_validator_db_test;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/excel_validator_db_test
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Configuration

### Application Properties

Key configurations in `application.properties`:

- **Database**: PostgreSQL connection settings
- **JPA**: Hibernate DDL auto-update, SQL logging, batch processing
- **JWT**: Secret key and token expiration (1 hour)
- **Security**: CORS enabled for `http://localhost:5173`

### JWT Configuration

Update the JWT secret key in `application.properties` for production:
```properties
app.jwt.secret=your_secure_secret_key_here
app.jwt.expiration=3600000
```

## Installation & Running

1. Clone the repository
2. Navigate to project directory
3. Update `application.properties` with your database credentials
4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

Or using the Maven wrapper:
```bash
./mvnw spring-boot:run   # Linux/Mac
mvnw.cmd spring-boot:run # Windows
```

The application will start on the default port (8080).

## API Endpoints

### Authentication

#### Register User
```
POST /auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword"
}
```

#### Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "securePassword"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### File Operations

#### Upload and Validate Excel File
```
POST /api/upload
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <excel-file>

Response (Success):
{
  "success": true,
  "message": "File validated and data saved successfully",
  "fileBytes": null
}

Response (Validation Errors):
{
  "success": false,
  "message": "Errors found in the file. Please check the highlighted cells.",
  "fileBytes": <base64-encoded-excel-file-with-highlights>
}
```

## Excel File Requirements

### Structure Requirements

1. **File Format**: .xls or .xlsx
2. **Sheet Count**: Exactly 2 sheets
3. **Sheet Names**: 
   - "Participant upload data" (data sheet)
   - "Instructions" (instructions sheet)

### Required Columns

The data sheet must contain the following columns (header names must match exactly):

1. **Seeker Name**: Alphanumeric characters only
2. **Seeker Phone no.**: Format: +[country code] [10 digits]
   - Supported country codes: 91, 44, 1, 33, 49
3. **Seeker Email**: Valid email format
4. **Provider Name**: Alphanumeric characters only
5. **Provider Email**: Valid email format
6. **Relationship with Seeker**: Must be one of: Peer, Manager, Direct Report, Customer
7. **is Family Related**: Boolean value (TRUE/FALSE)

### Validation Rules

- **Required Fields**: All fields are mandatory
- **Name Validation**: Only alphanumeric characters and spaces allowed
- **Email Validation**: Must follow standard email format
- **Phone Validation**: Must include country code and exactly 10 digits
- **Relationship Validation**: Case-insensitive match with allowed values
- **Uniqueness**: Seeker emails, provider emails, and phone numbers must be unique across the file
- **No Special Characters**: Names cannot contain special characters

### Example Valid Row

| Seeker Name | Seeker Phone no. | Seeker Email | Provider Name | Provider Email | Relationship with Seeker | is Family Related |
|-------------|------------------|--------------|---------------|----------------|--------------------------|-------------------|
| John Doe    | +91 9876543210   | john@example.com | Jane Smith | jane@example.com | Manager | FALSE |

## Project Structure

```
src/main/java/com/sumit/excelvalidator/
├── config/                   # Security and JWT configuration
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── controller/               # REST controllers
│   ├── AuthController.java
│   └── FileController.java
├── dto/                      # Data Transfer Objects
│   ├── AuthResponse.java
│   ├── CellError.java
│   ├── ExcelProcessorResult.java
│   ├── LoginRequest.java
│   ├── MessageResponse.java
│   ├── RegisterRequest.java
│   ├── RowData.java
│   ├── StructureInfo.java
│   └── ValidationResponse.java
├── entity/                   # JPA entities
│   ├── ExcelRecord.java
│   ├── UploadedFile.java
│   └── User.java
├── exceptions/               # Exception handling
│   └── GlobalExceptionHandler.java
├── processor/                # Excel processing logic
│   └── ExcelProcessor.java
├── reader/                   # Excel reading utilities
│   └── ExcelReader.java
├── repository/               # JPA repositories
│   ├── ExcelRecordRepository.java
│   ├── UploadedFileRepository.java
│   └── UserRepository.java
├── security/                 # Security utilities
├── service/                  # Business logic
│   ├── CustomUserDetailsService.java
│   ├── ExcelValidationService.java
│   └── JwtService.java
├── validator/                # Validation logic
│   ├── ColumnValidator.java
│   ├── DataValidator.java
│   └── ExcelFileValidator.java
├── writer/                   # Excel writing utilities
│   └── ExcelHighlighter.java
└── ExcelValidatorApplication.java
```

## Validation Process Flow

1. **File Upload**: User uploads Excel file via `/api/upload` endpoint
2. **File Validation**: System validates file format and structure
3. **Column Validation**: Checks for required columns in correct format
4. **Data Extraction**: Reads all data rows from the sheet
5. **Data Validation**: Validates each cell against business rules
6. **Duplicate Check**: Identifies duplicate emails and phone numbers
7. **Error Handling**:
   - If errors found: Highlights error cells, adds comments, returns modified file
   - If no errors: Saves valid records to database
8. **Response**: Returns success/error response with appropriate message

## Error Handling

The application provides detailed error messages for:

- Invalid file format
- Missing or incorrect sheets
- Missing required columns
- Invalid cell data
- Duplicate values
- Authentication errors
- Database errors

Errors in Excel files are highlighted in red with comments explaining the issue.

## Logging

Logs are stored in the `logs/` directory:

- `excel-validator.log`: General application logs
- `excel-validator-errors.log`: Error-specific logs
- Date-based rolling logs for archival

Configure logging levels in `src/main/resources/logback-spring.xml`.

## Security

- **Authentication**: JWT token-based authentication required for file operations
- **Authorization**: Bearer token must be included in request headers
- **Password Encryption**: BCrypt password encoding
- **CORS**: Configured for frontend access (localhost:5173)
- **Stateless Sessions**: No server-side session storage

## Performance Optimization

- **Batch Processing**: Hibernate batch inserts enabled (batch size: 50)
- **Order Optimization**: Optimized insert and update ordering
- **Connection Pooling**: Default HikariCP configuration
- **Lazy Loading**: JPA lazy loading for related entities

## Database Schema

### Tables

1. **users**: User authentication information
2. **uploaded_files**: Metadata about uploaded files
3. **excel_records**: Individual participant records from Excel files
4. **Sequences**: Optimized batch sequence generation

## Development Notes

- JPA entities use Lombok annotations for cleaner code
- Validation uses Jakarta Bean Validation (JSR 380)
- Excel processing handles both .xls and .xlsx formats
- Error highlighting preserves original file formatting

## Future Enhancements

- File download endpoint for validated files
- Enhanced reporting and analytics
- Email notifications for validation results
- Support for additional Excel formats
- Bulk file processing
- Admin dashboard

## Troubleshooting

### Common Issues

1. **Database Connection Error**: Verify PostgreSQL is running and credentials are correct
2. **JWT Token Invalid**: Check token expiration and secret key configuration
3. **Excel File Rejected**: Ensure file has exactly 2 sheets with correct names
4. **Column Not Found**: Verify column headers match exactly (case-sensitive)

## License

This project is developed as part of the Mettl Task assignment.

## Author

Sumit

## Contact

For issues or questions, please refer to the project documentation or contact the development team.

