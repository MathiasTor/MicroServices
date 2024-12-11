#!/bin/bash

# List of services and their corresponding directories
declare -A services=(
  ["communication-service"]="playpal-communication-service"
  ["profile-service"]="playpal-profile-service"
  ["friend-service"]="playpal-friend-service"
  ["group-service"]="playpal-group-service"
  ["runescape-service"]="playpal-runescape-service"
  ["search-service"]="playpal-search-service"
  ["user-service"]="playpal-user-service"
  ["gateway"]="playpal-gateway"
  ["frontend"]="playpal-frontend"
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
for service in "${!services[@]}"; do
  service_dir="${services[$service]}"

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
