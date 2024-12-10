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

# Docker image version
IMAGE_VERSION="1.0"

echo "Building Docker images for services..."

# Loop through the services and build each one
for service in "${!services[@]}"; do
  service_dir="${services[$service]}"

  if [ -d "$service_dir" ]; then
    echo "Building image for $service from $service_dir..."
    docker build -t "$service:$IMAGE_VERSION" "$service_dir"
    if [ $? -eq 0 ]; then
      echo "Successfully built $service:$IMAGE_VERSION"
    else
      echo "Failed to build $service:$IMAGE_VERSION"
      exit 1
    fi
  else
    echo "Directory $service_dir does not exist! Skipping $service..."
  fi
done

echo "Building Consul Importer Docker image..."

# Build the Consul Importer image
docker_consul_dir="docker/consul"
if [ -d "$docker_consul_dir" ]; then
  docker build -t "consul-importer:1.19.2" "$docker_consul_dir"
  if [ $? -eq 0 ]; then
    echo "Successfully built consul-importer:1.19.2"
  else
    echo "Failed to build consul-importer:1.19.2"
    exit 1
  fi
else
  echo "Directory $docker_consul_dir does not exist! Skipping Consul Importer..."
fi

echo "All images have been built successfully."