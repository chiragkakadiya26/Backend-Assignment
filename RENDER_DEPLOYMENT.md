# Render Deployment Guide

## Environment Variables Configuration for Render

### 1. MongoDB Atlas Configuration

#### Required Environment Variables:
```
MONGODB_URI=mongodb+srv://root:YOUR_PASSWORD@cluster0.jowrje7.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&authSource=admin
MONGODB_DATABASE=hotel_booking_db
```

#### How to get your MongoDB Atlas connection string:
1. Go to [MongoDB Atlas](https://cloud.mongodb.com)
2. Click on your cluster
3. Click "Connect" → "Connect your application"
4. Copy the connection string
5. Replace `<password>` with your actual database password
6. Replace `<dbname>` with your database name (optional, we'll use MONGODB_DATABASE env var)

### 2. Auth0 Configuration

#### Required Environment Variables:
```
AUTH0_ISSUER_URI=https://dev-yci6xoknjzaj8md4.us.auth0.com/
AUTH0_AUDIENCE=https://hotel-api/api
```

#### How to get your Auth0 configuration:
1. Go to [Auth0 Dashboard](https://manage.auth0.com)
2. Go to "Applications" → Your API
3. Copy the "Identifier" (this is your audience)
4. Copy the "Domain" and add `https://` prefix (this is your issuer URI)

### 3. Email Configuration (Optional)

#### Required Environment Variables:
```
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
SUPPORT_EMAIL=your-support-email@gmail.com
```

### 4. Application Configuration

#### Required Environment Variables:
```
SPRING_PROFILES_ACTIVE=prod
PORT=9090
```

## How to Set Environment Variables in Render

### Method 1: Using Render Dashboard
1. Go to your Render service dashboard
2. Click on "Environment" tab
3. Add each environment variable:
   - **Key**: `MONGODB_URI`
   - **Value**: `mongodb+srv://root:YOUR_PASSWORD@cluster0.jowrje7.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0`

### Method 2: Using Render CLI
```bash
# Install Render CLI
npm install -g @render/cli

# Login to Render
render login

# Set environment variables
render env set MONGODB_URI "mongodb+srv://root:YOUR_PASSWORD@cluster0.jowrje7.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&authSource=admin"
render env set MONGODB_DATABASE "hotel_booking_db"
render env set AUTH0_ISSUER_URI "https://dev-yci6xoknjzaj8md4.us.auth0.com/"
render env set AUTH0_AUDIENCE "https://hotel-api/api"
render env set SPRING_PROFILES_ACTIVE "prod"
render env set MAIL_USERNAME "your-email@gmail.com"
render env set MAIL_PASSWORD "your-app-password"
render env set SUPPORT_EMAIL "your-support-email@gmail.com"
```

## Complete Environment Variables List for Render

Copy and paste these into your Render environment variables:

```
MONGODB_URI=mongodb+srv://root:YOUR_PASSWORD@cluster0.jowrje7.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&authSource=admin
MONGODB_DATABASE=hotel_booking_db
AUTH0_ISSUER_URI=https://dev-yci6xoknjzaj8md4.us.auth0.com/
AUTH0_AUDIENCE=https://hotel-api/api
SPRING_PROFILES_ACTIVE=prod
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
SUPPORT_EMAIL=your-support-email@gmail.com
PORT=9090
```

## Security Best Practices

1. **Never commit sensitive data to your repository**
2. **Use strong passwords for MongoDB Atlas**
3. **Restrict IP access in MongoDB Atlas to Render's IP ranges**
4. **Use Auth0's built-in security features**
5. **Enable 2FA on all your accounts**

## Troubleshooting

### Common Issues:

1. **MongoDB SSL Connection Failed (javax.net.ssl.SSLException)**
   - **Solution**: Use the updated connection string with SSL parameters:
     ```
     MONGODB_URI=mongodb+srv://root:YOUR_PASSWORD@cluster0.jowrje7.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0&ssl=true&authSource=admin
     ```
   - **Additional fixes**:
     - Ensure your MongoDB Atlas cluster allows connections from all IPs (0.0.0.0/0)
     - Verify your database user has proper permissions
     - Check if your MongoDB Atlas cluster is running (not paused)

2. **MongoDB Connection Failed**
   - Check if your IP is whitelisted in MongoDB Atlas
   - Verify the connection string format
   - Ensure the database user has proper permissions

3. **Auth0 Authentication Failed**
   - Verify the issuer URI and audience
   - Check if your Auth0 application is configured correctly
   - Ensure the JWT token is being sent in the Authorization header

4. **Email Service Not Working**
   - Use App Passwords for Gmail (not your regular password)
   - Enable 2FA on your Gmail account
   - Check if "Less secure app access" is enabled (if not using App Passwords)

## Testing Your Deployment

1. **Check Application Logs** in Render dashboard
2. **Test API Endpoints** using Postman or curl
3. **Verify Database Connection** by checking if data is being saved
4. **Test Authentication** by making authenticated requests

## Render Service Configuration

Make sure your Render service is configured with:
- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/Backend-Assignment-0.0.1-SNAPSHOT.jar`
- **Environment**: Java
- **Java Version**: 17
