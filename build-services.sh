#!/bin/bash

# List of service directories
services=(
  "playpal-communication-service"
  "playpal-profile-service"
  "playpal-friend-service"
  "playpal-group-service"
  "playpal-leaderboard-service"
  "playpal-runescape-service"
  "playpal-search-service"
  "playpal-user-service"
  "playpal-gateway"
  "playpal-livesearch-service"
)

# Frontend directory
frontend_dir="playpal-frontend"

# Log file
LOG_FILE="build-services.log"

echo "Starting Maven clean install and frontend build for all services..." | tee $LOG_FILE

# Loop through the service directories and build each one
for service_dir in "${services[@]}"; do
  if [ -d "$service_dir" ]; then
    echo "Building service in $service_dir..." | tee -a $LOG_FILE
    (
      cd "$service_dir" || exit
      mvn clean install -DskipTests | tee -a "../$LOG_FILE"
      if [ $? -eq 0 ]; then
        echo "Build successful for $service_dir" | tee -a "../$LOG_FILE"
      else
        echo "Build failed for $service_dir. Check $LOG_FILE for details." | tee -a "../$LOG_FILE"
      fi
    )
  else
    echo "Directory $service_dir does not exist! Skipping..." | tee -a $LOG_FILE
  fi
done

# Build the frontend
if [ -d "$frontend_dir" ]; then
  echo "Building frontend in $frontend_dir..." | tee -a $LOG_FILE
  (
    cd "$frontend_dir" || exit
    npm install | tee -a "../$LOG_FILE"
    if [ $? -eq 0 ]; then
      npm run build | tee -a "../$LOG_FILE"
      if [ $? -eq 0 ]; then
        echo "Frontend build successful in $frontend_dir" | tee -a "../$LOG_FILE"
      else
        echo "Frontend build failed in $frontend_dir. Check $LOG_FILE for details." | tee -a "../$LOG_FILE"
      fi
    else
      echo "Frontend npm install failed in $frontend_dir. Check $LOG_FILE for details." | tee -a "../$LOG_FILE"
    fi
  )
else
  echo "Directory $frontend_dir does not exist! Skipping frontend build..." | tee -a $LOG_FILE
fi

echo "Maven clean install and frontend build completed for all services (if no errors occurred)." | tee -a $LOG_FILE
