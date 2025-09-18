# Hotel Booking Backend (Spring Boot + MongoDB Atlas + Auth0)

Secure hotel booking API built with Spring Boot 3.5.5, MongoDB Atlas, and Auth0. Includes email notifications and ready-to-deploy Docker/Render setup.

## Tech Stack
- Java 17
- Spring Boot (Web, Security, OAuth2 Resource Server, Data MongoDB, Validation, Actuator, Mail)
- MongoDB Atlas
- Auth0 (JWT)
- Docker

## Environment Variables
Match these keys to `src/main/resources/application.yml` and `application-prod.yml`.

Required for production (Render):
```
MONGODB_URI=mongodb+srv://USER:PASS@cluster.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&authSource=admin
MONGODB_DATABASE=hotel_booking_db

AUTH0_ISSUER_URI=https://YOUR_DOMAIN.auth0.com/
AUTH0_AUDIENCE=https://hotel-api/api

SPRING_PROFILES_ACTIVE=prod
PORT=9090
JAVA_OPTS=-Xmx512m -Xms256m

# Email (optional; app starts without them)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
SUPPORT_EMAIL=your-support-email@gmail.com
```

Notes:
- The app uses `permissions` claim and custom roles claim `https://hotel.com/roles` as configured in `AuthConfiguration`.
- Email service is optional. If `JavaMailSender` is not configured, emails are skipped without failing the app.

## MongoDB Atlas Setup
1. Create cluster and DB user.
2. Network Access → allow `0.0.0.0/0` (or Render IP ranges).
3. Use `mongodb+srv://` URI and add `&ssl=true&authSource=admin`.
4. Set `MONGODB_DATABASE` to your database name.

## Auth0 Setup
1. Create API: Identifier `https://hotel-api/api`, RS256.
2. Enable RBAC and “Add Permissions in the Access Token”.
3. Define permissions:
   - `booking:create` (required for POST bookings)
   - `booking:read` (required for GET bookings if no role)
4. Optionally add roles claim under namespace `https://hotel.com/roles` and assign roles `STAFF`, `RECEPTION`, `ADMIN`.
5. Set `AUTH0_ISSUER_URI` to your domain (e.g. `https://dev-xxxx.us.auth0.com/`).

## Run Locally
```bash
mvn clean package -DskipTests
java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar
# or with env
MONGODB_URI=... AUTH0_ISSUER_URI=... AUTH0_AUDIENCE=... java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar
```

Docker
```bash
docker build -t hotel-booking-app .
docker run -p 9090:9090 \
  -e MONGODB_URI=... \
  -e MONGODB_DATABASE=hotel_booking_db \
  -e AUTH0_ISSUER_URI=... \
  -e AUTH0_AUDIENCE=... \
  -e SPRING_PROFILES_ACTIVE=prod \
  hotel-booking-app
```

## Deploy to Render
Option A: Dashboard
1. Create Web Service from this repo.
2. Build Command: `mvn clean package -DskipTests`
3. Start Command: `java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar`
4. Health Check Path: `/actuator/health`
5. Add env vars listed above.

Option B: render.yaml (already included)
```
services:
  - type: web
    name: hotel-booking-api
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar
    healthCheckPath: /actuator/health
```

## API Endpoints
Base URL (Render): `https://backend-assignment-8bng.onrender.com`

Public:
```
POST /api/hotelCreate
```

Protected (Auth0 JWT required):
```
POST /api/hotels/{hotelId}/bookings    # requires role STAFF/RECEPTION/ADMIN or permission booking:create
GET  /api/hotels/{hotelId}/bookings    # requires role STAFF/RECEPTION/ADMIN or permission booking:read
```

Example payload (POST):
```
{
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "checkIn": "2025-10-01",
  "checkOut": "2025-10-03",
  "roomType": "DELUXE",
  "numberOfGuests": 2
}
```

## How to test in Postman

1) Get an access token (Client Credentials)
- Create a request:
  - Method: POST
  - URL: `https://dev-yci6xoknjzaj8md4.us.auth0.com/oauth/token`
  - Headers: `Content-Type: application/json`
  - Body (raw / JSON):
    ```
    {
      "client_id": "NeUQGgi5vQPPqIvYtxVB97IjNyAsh5x6",
      "client_secret": "-i5wAv05sOSbzWlSm8qr5uom2wVTHQtlA-BNDYYhcKtjzHQrczmaawjZevwMr_sf",
      "audience": "https://hotel-api/api",
      "grant_type": "client_credentials"
    }
    ```
- Copy the `access_token` value from the response.

2) Create Booking (POST)
- Method: POST
- URL: `https://backend-assignment-8bng.onrender.com/api/hotels/{hotelId}/bookings`
- Headers:
  - `Authorization: Bearer <ACCESS_TOKEN>`
  - `Content-Type: application/json`
- Body (raw / JSON):
  ```
  {
    "guestName": "John Doe",
    "guestEmail": "john@example.com",
    "checkIn": "2025-10-01",
    "checkOut": "2025-10-03",
    "roomType": "DELUXE",
    "numberOfGuests": 2
  }
  ```
- Expected 201 response (example):
  ```
  {
    "id": "66f0160d2b3f1a0c1c9f7b21",
    "hotelId": "68c96e8889088248ef88f92a",
    "guestName": "John Doe",
    "guestEmail": "john@example.com",
    "checkIn": "2025-10-01",
    "checkOut": "2025-10-03",
    "roomType": "DELUXE",
    "numberOfGuests": 2,
    "totalAmount": 240.0,
    "status": "CONFIRMED",
    "createdAt": "2025-09-18T05:40:31.123Z"
  }
  ```

3) Get Bookings (GET)
- Method: GET
- URL: `https://backend-assignment-8bng.onrender.com/api/hotels/{hotelId}/bookings`
  - Optional query params: `startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- Headers:
  - `Authorization: Bearer <ACCESS_TOKEN>`
- Expected 200 response (example):
  ```
  [
    {
      "id": "66f0160d2b3f1a0c1c9f7b21",
      "hotelId": "68c96e8889088248ef88f92a",
      "guestName": "John Doe",
      "guestEmail": "john@example.com",
      "checkIn": "2025-10-01",
      "checkOut": "2025-10-03",
      "roomType": "DELUXE",
      "numberOfGuests": 2,
      "totalAmount": 240.0,
      "status": "CONFIRMED",
      "createdAt": "2025-09-18T05:40:31.123Z"
    }
  ]
  ```

Common error examples:
- 401 Unauthorized: token missing/invalid or wrong audience/issuer.
- 403 Forbidden: token lacks `booking:create` (POST) or `booking:read` (GET), or required role.
- 409 Conflict: room not available for selected dates.

## Get an Auth0 Access Token (Client Credentials)

Generic request (replace values with your own):
```
POST https://YOUR_AUTH0_DOMAIN/oauth/token
Content-Type: application/json

{
  "client_id": "YOUR_CLIENT_ID",
  "client_secret": "YOUR_CLIENT_SECRET",
  "audience": "https://hotel-api/api",
  "grant_type": "client_credentials"
}
```

Your current configuration (provided):
```
POST https://dev-yci6xoknjzaj8md4.us.auth0.com/oauth/token
Content-Type: application/json

{
  "client_id": "NeUQGgi5vQPPqIvYtxVB97IjNyAsh5x6",
  "client_secret": "-i5wAv05sOSbzWlSm8qr5uom2wVTHQtlA-BNDYYhcKtjzHQrczmaawjZevwMr_sf",
  "audience": "https://hotel-api/api",
  "grant_type": "client_credentials"
}
```

Example curl to call your deployed API with the token:
```bash
ACCESS_TOKEN=$(curl -s --request POST \
  --url https://dev-yci6xoknjzaj8md4.us.auth0.com/oauth/token \
  --header 'content-type: application/json' \
  --data '{
    "client_id": "NeUQGgi5vQPPqIvYtxVB97IjNyAsh5x6",
    "client_secret": "-i5wAv05sOSbzWlSm8qr5uom2wVTHQtlA-BNDYYhcKtjzHQrczmaawjZevwMr_sf",
    "audience": "https://hotel-api/api",
    "grant_type": "client_credentials"
  }' | jq -r .access_token)

curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://backend-assignment-8bng.onrender.com/api/hotels/{hotelId}/bookings"
```

Security note: avoid committing secrets in version control; prefer Render Environment Variables or a secure secrets manager.

## Troubleshooting
- SSLException to MongoDB:
  - Ensure `MONGODB_URI` includes `&ssl=true&authSource=admin` and points to Atlas.
  - Allow IPs in Atlas Network Access.
- 401 Unauthorized:
  - Verify `AUTH0_ISSUER_URI` and `AUTH0_AUDIENCE`.
  - Token must include `permissions` or roles under `https://hotel.com/roles`.
- Empty bookings list:
  - Ensure documents exist in DB `hotel_booking_db`, collection `booking`, with matching `hotelId`.
- MappingException for Booking → BookingResponse:
  - Fixed by aligning DTO fields with entity (`checkIn`, `checkOut`, `createdAt`).
- Email startup failures:
  - Email is optional; without mail env vars, emails are skipped.

## License
MIT
