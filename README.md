
# PG3402 Microservices exam
## _PlayPal delivery group: 21320193_

## **__1. Introduction and Overview__**
**___PlayPal___** is a groundbreaking social group-finding application designed to bring gamers together from all around the world. Whether you're looking for teammates to tackle challenging in-game content, casual friends to share fun moments with, or competitive partners to climb the ranks, PlayPal offers the tools to connect you with the perfect match.

Our platform is tailored to fill a gap in the gaming industry: the lack of effective and inclusive group-finding systems. Many games either lack this functionality entirely or provide subpar solutions, leaving players to fend for themselves. With PlayPal, we aim to centralize group-finding across various gaming communities, starting with a focus on **__Old School RuneScape (OSRS)__** — a beloved MMORPG with millions of active players but no built-in group search or friend-finding system.

Through **PlayPal**, we aspire to create an engaging and user-friendly experience that allows gamers to:

* Find friends or teammates easily for cooperative gameplay.
* Use both live-searches for quick matching, or static posts for more controlled groups.
* Create and join events/groups tailored to in-game activities.
* Engage in real-time chat and community interactions.
* Participate in leaderboards and data-driven features to encourage engagement.
* Most importantly, have fun! (And level up:+1:)  :thumbsup:

## **__2. How to Build, start and Run the Project__**

### **__Option 1: Local Setup__**
Follow these steps to build and run the project without Docker.  
This will require some tools installed on your system.  
**Prerequisites:**
- **Java 21**
- **Maven:** Required to build the services locally (if testing without Docker).
- **RabbitMQ:** For asynchronous communication and messaging if used locally.
- **Postman** (_optional_): For testing API endpoints directly.
- **Consul**: Centralized configuration management and service discovery tool.
- **Frontend**: For displaying the frontend, node.js must be installed on the computer. Verify installation by running:  
  **`node --version`**
---
#### **Steps to Build and Run:**
_Either use .zip file provided or clone the repo._

**1. Clone the repo.**  
Navigate to root project folder with cd command based on where project's located on computer system.
```
git clone https://github.com/MathiasTor/MicroServices.git
cd <project-directory>
```
Replace `<project-directory>` with the name of the folder created by cloning.

----

**2. Navigate to the root directory of each service and run:**
```bash
mvn clean install
```  

***Alternatively, you can use the provideded script to build all services automatically:***  
(_Ensure the script is executed from the root folder and that you're using a Bash-compatible terminal._)
```bash
./build-services.sh
```
> [!IMPORTANT]
>  If "Permission denied" run: chmod u+x build-services.sh
**3. Start RabbitMQ and Consul**  
Run commands to start RabbitMq (or use app RabbitMQ service - start)
```
sudo rabbitmq-server
```
Run commands to start Consul
```
consul agent -dev -node playpal-local-dev-node
```

**4. Run the services:**  
Navigate to each service directory and run the service using Maven.
```
cd your-service-directory
mvn spring-boot:run
```
**5. Run the frontend.**
Navigate to playpal-frontend and run:
```
cd playpal-frontend
npm i
npm start
```
### **Ports and logins**
- **Frontend:** `3000`
- **Search Service:** `9091`
- **Runescape Service:** `9002`
- **User Service:** `9070`
- **Profile Service:** `8087`
- **Live Search Service:** `9040`
- **Leaderboard Service:** `9010`
- **Group Service:** `8090`
- **Friend Service:** `9020`
- **Communication Service:** `8085`
- **RabbitMQ:** `5672` _username:_ guest, _password:_ guest
- **Gateway:** `8080`
- **Consul:** `8500`
- **H2 login:** _username:_ sa (jdbc url defined in each service application.properties )


### **__Option 2: Container/Docker build and run__**
**Prerequisites**  
PlayPal is fully containerized using Docker, ensuring that it can run seamlessly on any system with Docker installed. The only requirements for running the project are therefore:
- **Docker:** Ensure Docker is installed and running on your system.
  Download Docker
- **Docker Compose:** Required to orchestrate multiple containers.
  Typically included with Docker Desktop. Verify installation by running:

  **`docker-compose --version`**

> [!NOTE]
> Since the services are containerized, they should work on any operating system that supports Docker (Linux, macOS, or Windows).

**Setup Manually:**  
For manual builds to make docker-compose setup work:  
Make sure docker engine is running, for example through docker desktop.  
To run these commands, make sure you're in root folder of the project.
- `docker build -t communication-service:1.0 ./playpal-communication-service`
- `docker build -t profile-service:1.0 ./playpal-profile-service`
- `docker build -t friend-service:1.0 ./playpal-friend-service`
- `docker build -t group-service:1.0 ./playpal-group-service`
- `docker build -t leaderboard-service:1.0 ./playpal-leaderboard-service`
- `docker build -t runescape-service:1.0 ./playpal-runescape-service`
- `docker build -t search-service:1.0 ./playpal-search-service`
- `docker build -t user-service:1.0 ./playpal-user-service`
- `docker build -t gateway:1.0 ./playpal-gateway`
- `docker build -t frontend:1.0 ./playpal-frontend`
- `docker build -t livesearch-service2:1.0 ./playpal-livesearch-service2`


***Alternatively, you can use the provideded script to build all images automatically:***
(_Ensure the script is executed from the root folder and that you're using a Bash-compatible terminal._)
```bash
./build-docker.sh
```
> [!IMPORTANT]
>  If "Permission denied" run: chmod u+x build-services.sh
This can also be automated if you run the build-docker.sh bash script in root.

**Running the containers:**  
Navigate to docker directory and run docker compose file.

```bash
cd docker
docker-compose up --build
```

This will run all the docker containers and images according to the configurations.

----

Due to the nature of our system, combined with the size, we've created an extensive frontend for testing purposes. Please utilize it as much as possible to test required functionality, and endpoints.

## **__3. Project Architecture__**
- [ ] a diagram showing the architecture of the system. This should show what services the
  project contains and what type of communication they have between them (synchronous or
  asynchronous).
## **__4. User Stories and Functionality__**

4. User Stories and Functionality


![Screenshot of user stories submitted in arbeidskrav](images/User Stories.jpg)

These are the initial user stories provided in our Arbeidskrav.
- [x]  Story 1: As a user, I want to find people to play with.
- [x]  Story 2: As a user, I want to be able to see ones own and other people's profiles.
- [x]  Story 3: As a user, I want to be able to communicate with other users.


**_Implementation Details:_**

**Story 1:** Find People to Play With


Users can search for active users and befriend them, search live for other players, or browse/create static group posts.

*Services Involved:*

- Search Service: Handles search logic and matchmaking.
- Live-search service: Handles live matchmaking with algorithm for matching.
- User Service: Handles registration and login.
- Communication Service: Handles chatting and messaging with websockets.
- Profile service: Creation of profiles, which allows for interaction.
- Group Service: Creates groups with applicants or people found through live search.
- Frontend to show all the functionality.

How to Test:

```
Use our frontend to register multiple users. Create static posts, apply to them on other accounts, accept and stop searching. (To create group). Add friends. Chat with friends, try to live search on one, check list on other. Live search on other and see if match based on algorithm.  
Check backend storage endpoints if necessary.
```

```
Relevant Endpoints:
```

**Story 2:** View User Profiles

Users can view profiles. With this they can look at their RuneScape stats if they're linked to assess their skills and experience. Upvoting and downvoting system in case someone makes for a bad experience, or vice versa. AI generated images.

*Services Involved:*
- Same services as user story 1.
- Additionally, Runescape Service for fetching runescape stats and showcasing them.
  How to Test:

```
Click on profile to enter your own to see. Here you can link with runescape account, edit bio and image etc. If you click to other profiles you can upvote / downvote, and see their relevant stats / information.  
Check backend storage endpoints if necessary.
```

```
Relevant Endpoints:
```
**Story 3:** Communicate with other users.

As a user, I want to communicate with others, so that I can:

Full functionality for chatting with other users with websockets through gateway. Chats generated automatically through communication service, based on what happens. For example Live Search generated group creates a conversation between those two, while accepting group with multiple users makes conversation for those users.  
Also

*Services Involved:*
Communication Service: Handles real-time chat and messaging.
Notification Service: Sends notifications for group invites, messages, and events.

How to Test:

```
Login or generate user, and add someone/join a group or livesearch to match with someone.  
Try to chat with person in two browsers for visualizing messages, and check backend storage endpoints if necessary.
```

```
Relevant Endpoints:
```

    Use /chat API to initiate a conversation.
    Confirm that messages are displayed in the frontend.

### **Full workflow suggestion:**
```
Todo:
Example data. Example runescape accounts? Write full functionality list test.
```

## **__5. Key Microservices Implementation Details__**

PlayPal is a Java and Spring Boot application architected using microservices principles. The microservices architecture ensures scalability, maintainability, and flexibility in adapting to new features or supporting additional gaming communities.

**Key Technical Principles**

- **Separation of Concerns:** Each microservice is designed with the Single Responsibility Principle (SRP), ensuring each service focuses on a specific function within the application, such as user management, matchmaking, or event planning.
- **SOLID Design Principles:** PlayPal adheres to SOLID principles to produce maintainable, extensible, and testable code.
- **API Communication:**
  - **Synchronous Communication:** Some services interact using RESTful API calls, which allow direct and real-time communication.
  - **Asynchronous Communication:** Event-driven architecture is utilized with RabbitMQ, enabling services to communicate via message queues for decoupled and efficient interactions.
- **Gateway Service:**
  Serves as the single entry point for the application.
  Handles routing to appropriate services.
  Implements load balancing to distribute traffic effectively.
- **Configuration Management:** Centralized configuration management is implemented using tools like Consul, ensuring consistent settings across all services.
- **Health Monitoring:** A health check using actuator, monitors the status of running services, enabling proactive maintenance and high availability.

4. Add Health Check Details:

health checks (e.g., via Spring Boot Actuator), explain how to verify them?
Access endpoint: http://localhost:<service>/actuator/health
Mention what a healthy response looks like ({"status":"UP"}).




Meeting Project Guidelines

Here’s how PlayPal aligns with the exam's structured requirements:

- [x] PlayPal uses multiple microservices with distinct responsibilities (e.g., User service, Live-search service).
- [x] Synchronous Communication:
  RESTful API calls enable direct communication between services. Maybe mention which ones here?
- [x] Asynchronous Communication:
  Asynchronous communication and messaging with RabbitMQ between services like the Search service, to group service, to communication service. Following event-driven architecture.
- [x] Clear Structure and Functionality:
  Each service is designed with clear functionality and modular structure, ensuring maintainability.
- [x] Architecture Documentation:
  The architecture is well-documented, including service roles, interactions, and communication protocols.
- [x] Gateway and Load Balancing:
  A Gateway Service acts as the single access point, handling routing and load balancing.
- [x] Centralized Health Monitoring:
  Health checks are implemented to monitor service uptime and status centrally. (Actuator)
- [x] Centralized Configuration Management:
  Tools like Consul are used for managing service configurations across environments.
- [x] Containerization:
  Docker is used to containerize services, supporting deployment on diverse infrastructure.

## **__6. Reflections on Architecture Choices__**

## **__7. Contribution (if applicable)__**
- [ ] if this is a team project, the README.md file should also contain a discussion of the
  contribution and responsibilities of each team member.
## **__8. Additional?**

Our mission is to enhance the social aspects of gaming by bridging the gap between players who want to participate in group content and those who lack the means to find suitable partners. PlayPal isn't just a tool — it's a community-driven platform that fosters friendships, teamwork, and collaboration across all genres of gaming.

Join us as we redefine how gamers connect and play together. Welcome to PlayPal, where your next adventure begins.