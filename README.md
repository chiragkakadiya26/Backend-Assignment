# Hotel Booking Backend Service

A secure backend service for hotel bookings built with Java Spring Boot, MongoDB Atlas, and Auth0 authentication.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.5.5**
- **Spring Security with OAuth2 Resource Server**
- **MongoDB Atlas**
- **Auth0 for JWT Authentication**
- **Spring Mail for Email Notifications**

## Features

- ✅ JWT-based authentication with Auth0
- ✅ Role-based authorization (STAFF, RECEPTION, ADMIN)
- ✅ Hotel booking management with conflict detection
- ✅ Email notifications for new bookings
- ✅ MongoDB integration with Atlas support
- ✅ RESTful API endpoints
- ✅ Docker containerization
- ✅ Cloud deployment ready

## API Endpoints

### Authentication Required
All booking endpoints require JWT authentication with appropriate roles.

#### Create Booking
```
POST /api/hotels/{hotelId}/bookings
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "checkIn": "2024-01-15",
  "checkOut": "2024-01-18",
  "roomType": "DELUXE",
  "numberOfGuests": 2
}
```

#### Get Bookings
```
GET /api/hotels/{hotelId}/bookings?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <JWT_TOKEN>
```

#### Create Hotel
```
POST /api/hotelCreate
Content-Type: application/json

{
  "name": "Grand Hotel",
  "address": "123 Main St",
  "city": "New York",
  "country": "USA",
  "rating": 4.5,
  "roomTypes": [
    {
      "type": "STANDARD",
      "pricePerNight": 150.0,
      "maxOccupancy": 2
    },
    {
      "type": "DELUXE",
      "pricePerNight": 250.0,
      "maxOccupancy": 4
    }
  ]
}
```

## Setup Instructions

### 1. Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB Atlas account
- Auth0 account
- Gmail account for email notifications

### 2. MongoDB Atlas Setup

1. Create a MongoDB Atlas account at [mongodb.com](https://www.mongodb.com/cloud/atlas)
2. Create a new cluster
3. Create a database user
4. Whitelist your IP address
5. Get your connection string

### 3. Auth0 Setup

1. Create an Auth0 account at [auth0.com](https://auth0.com)
2. Create a new application (Machine to Machine)
3. Create an API with the following settings:
   - Identifier: `https://hotel.com/api`
   - Signing Algorithm: `RS256`
4. Configure roles in Auth0:
   - Create roles: `STAFF`, `RECEPTION`, `ADMIN`
   - Assign roles to users
5. Get your domain and audience from Auth0 dashboard

### 4. Environment Configuration

Create a `.env` file or set environment variables:

```bash
# MongoDB Configuration
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/
MONGODB_DATABASE=hotel_booking_db

# Auth0 Configuration
AUTH0_ISSUER_URI=https://your-domain.auth0.com/
AUTH0_AUDIENCE=https://hotel.com/api

# Email Configuration
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-app-password

# Support Email
APP_SUPPORT_EMAIL=support@yourcompany.com
```

### 5. Gmail App Password Setup

1. Enable 2-factor authentication on your Gmail account
2. Generate an app password:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate password for "Mail"
3. Use this password in `SPRING_MAIL_PASSWORD`

### 6. Running the Application

#### Local Development
```bash
# Build the project
mvn clean package

# Run the application
java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar
```

#### Using Docker
```bash
# Build and run with Docker Compose
docker-compose up --build
```

#### Using Docker directly
```bash
# Build the Docker image
docker build -t hotel-booking-app .

# Run the container
docker run -p 8080:8080 \
  -e MONGODB_URI=your_mongodb_uri \
  -e AUTH0_ISSUER_URI=your_auth0_issuer \
  -e AUTH0_AUDIENCE=your_auth0_audience \
  hotel-booking-app
```

## Cloud Deployment

### Heroku Deployment

1. Install Heroku CLI
2. Create a Heroku app:
```bash
heroku create your-app-name
```

3. Set environment variables:
```bash
heroku config:set MONGODB_URI=your_mongodb_uri
heroku config:set AUTH0_ISSUER_URI=your_auth0_issuer
heroku config:set AUTH0_AUDIENCE=your_auth0_audience
heroku config:set SPRING_MAIL_USERNAME=your_email
heroku config:set SPRING_MAIL_PASSWORD=your_app_password
```

4. Deploy:
```bash
git push heroku main
```

### AWS Deployment

1. Create an EC2 instance
2. Install Docker and Docker Compose
3. Copy your application files
4. Set environment variables
5. Run with Docker Compose

### Google Cloud Platform

1. Create a Cloud Run service
2. Build and push Docker image to Container Registry
3. Deploy to Cloud Run with environment variables

## Testing the API

### Auth0 Permissions (M2M Friendly)

This project is configured to authorize using Auth0 API permissions from the `permissions` claim in the access token.

1) In Auth0 Dashboard → Applications → APIs → your API (Identifier must match `AUTH0_AUDIENCE`), enable RBAC and "Add Permissions in the Access Token".

2) Define an API permission named `booking:create`.

3) For Machine-to-Machine apps, authorize your application for this API and grant the `booking:create` permission. Tokens will contain a `permissions: ["booking:create"]` claim.

4) The POST endpoint `/api/hotels/{hotelId}/bookings` requires `booking:create`.

If you use user-login (Authorization Code / PKCE), you can also assign permissions to the user or use Actions to add custom claims.

### 1. Get JWT Token from Auth0

Use Auth0's test endpoint or your frontend application to get a JWT token.

### 2. Test Booking Creation

```bash
curl -X POST http://localhost:8080/api/hotels/{hotelId}/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "guestName": "John Doe",
    "guestEmail": "john@example.com",
    "checkIn": "2024-01-15",
    "checkOut": "2024-01-18",
    "roomType": "DELUXE",
    "numberOfGuests": 2
  }'
```

### 3. Test Booking Retrieval

```bash
curl -X GET "http://localhost:8080/api/hotels/{hotelId}/bookings" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Features

- JWT token validation (issuer, signature, expiry)
- Role-based access control
- User ID extraction from JWT claims
- Secure email notifications
- Input validation and sanitization

## Error Handling

The application includes comprehensive error handling:

- `BookingConflictException`: When room is not available
- `GlobalExceptionHandler`: Centralized error handling
- Proper HTTP status codes
- Detailed error messages

## Monitoring and Logging

- Structured logging with SLF4J
- Request/response logging
- Error tracking
- Email notification status logging

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
