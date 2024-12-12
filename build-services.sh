#!/bin/bash

# List of backend services to build (Spring Boot)
backend_services=(
  "playpal-communication-service"
  "playpal-friend-service"
  "playpal-group-service"
  "playpal-profile-service"
  "playpal-runescape-service"
  "playpal-leaderboard-service"
  "playpal-search-service"
  "playpal-livesearch-service"
  "playpal-user-service"
  "playpal-gateway"

)

# Build backend services
echo "Building backend services..."
for service in "${backend_services[@]}"
do
  echo "Building JAR for $service..."
  pushd "$service" || exit
  mvn clean package -DskipTests
  popd || exit
done
echo "Backend services built successfully."

# Build the frontend (React)
frontend_service="playpal-frontend"
echo "Building frontend service ($frontend_service)..."
pushd "$frontend_service" || exit
# Install dependencies and build
npm install
npm run build
popd || exit
echo "Frontend service built successfully."

echo "All services built successfully."
