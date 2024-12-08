# Stage 1: Build React app
FROM node:18 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json to install dependencies
COPY package*.json ./

# Install dependencies (production dependencies only)
RUN npm ci --only=production

# Copy the entire frontend codebase into the container
COPY . .

# Build the application
RUN npm run build

# Stage 2: Serve the React app with nginx
FROM nginx:1.25-alpine

# Copy the build output from the builder stage
COPY --from=builder /app/build /usr/share/nginx/html

# Expose port 80
EXPOSE 80

# Start nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]