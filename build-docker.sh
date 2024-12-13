#!/bin/bash

# List of services and their corresponding directories using indexed arrays
keys=(
  "communication-service"
  "profile-service"
  "friend-service"
  "group-service"
  "leaderboard-service"
  "runescape-service"
  "search-service"
  "user-service"
  "gateway"
  "frontend"
  "livesearch-service"
)

values=(
  "playpal-communication-service"
  "playpal-profile-service"
  "playpal-friend-service"
  "playpal-group-service"
  "playpal-leaderboard-service"
  "playpal-runescape-service"
  "playpal-search-service"
  "playpal-user-service"
  "playpal-gateway"
  "playpal-frontend"
  "playpal-livesearch-service"

)

# Docker image version for services
IMAGE_VERSION="1.0"

# Docker image name and directory for consul-importer
CONSUL_IMPORTER_IMAGE="consul-importer:1.19.2"
CONSUL_IMPORTER_DIR="docker/consul"  # Updated path

# Log file
LOG_FILE="build-images.log"

echo "Building Docker images for services..." | tee $LOG_FILE

# Build consul-importer image
if [ -d "$CONSUL_IMPORTER_DIR" ]; then
  echo "Building image for consul-importer from $CONSUL_IMPORTER_DIR..." | tee -a $LOG_FILE
  docker build -t "$CONSUL_IMPORTER_IMAGE" "$CONSUL_IMPORTER_DIR" | tee -a $LOG_FILE
  if [ $? -eq 0 ]; then
    echo "Successfully built $CONSUL_IMPORTER_IMAGE" | tee -a $LOG_FILE
  else
    echo "Failed to build $CONSUL_IMPORTER_IMAGE. Check $LOG_FILE for details." | tee -a $LOG_FILE
    exit 1
  fi
else
  echo "Directory $CONSUL_IMPORTER_DIR does not exist! Skipping consul-importer..." | tee -a $LOG_FILE
fi

# Loop through the services and build each one
for i in "${!keys[@]}"; do
  service="${keys[$i]}"
  service_dir="${values[$i]}"

  echo "Key: $service, Value: $service_dir" | tee -a $LOG_FILE # Debug output for key-value pairs

  if [ -d "$service_dir" ]; then
    echo "Building image for $service from $service_dir..." | tee -a $LOG_FILE
    docker build -t "$service:$IMAGE_VERSION" "$service_dir" | tee -a $LOG_FILE
    if [ $? -eq 0 ]; then
      echo "Successfully built $service:$IMAGE_VERSION" | tee -a $LOG_FILE
    else
      echo "Failed to build $service:$IMAGE_VERSION. Check $LOG_FILE for details." | tee -a $LOG_FILE
      exit 1
    fi
  else
    echo "Directory $service_dir does not exist! Skipping $service..." | tee -a $LOG_FILE
  fi
done

echo "All images have been built and tagged successfully." | tee -a $LOG_FILE
