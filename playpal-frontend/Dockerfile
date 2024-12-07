FROM node:18 AS builder

# Setting the working directory
WORKDIR /app

COPY package*.json ./

# Installing dependencies
RUN npm install

# Copy the rest of the application code
COPY ../playpal-frontend .

# Build the application for production
RUN npm run build

# Stage 2: Serve the React app with nginx
FROM nginx:1.25-alpine

# Copy the built React app from the previous stage
COPY --from=builder /app/build /usr/share/nginx/html

# Expose port 80 to serve the app
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]