
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
* Participate/view leaderboards to encourage engagement.
* Most importantly, have fun! (And level up:👍)  

## **__2. How to Build, Start and Run the Project__**


### **__Option 1: Container/Docker build and run__**
**Prerequisites**  
PlayPal is fully containerized using Docker, ensuring that it can run seamlessly on any system with Docker installed. The only requirements for running the project are therefore:
- **Docker:** Ensure Docker is installed and running on your system.
Verify installation by running:

  **`docker --version`**

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
```
./build-docker.sh
```
> [!IMPORTANT]
>  If "Permission denied" run: chmod u+x build-services.sh
This can also be automated if you run the build-docker.sh bash script in root.

**Running the containers:**  
Navigate to docker directory and run docker compose file.

```
cd docker
docker-compose up --build
```

This will run all the docker containers and images according to the configurations.

----

Due to the nature of our system, combined with the size, we've created an extensive frontend for testing purposes. Please utilize it as much as possible to test required functionality, and endpoints.

### **__Option 2: Local Setup (without docker)__**
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
```
mvn clean install
```  

***Alternatively, you can use the provideded script to build all services automatically:***  
(_Ensure the script is executed from the root folder and that you're using a Bash-compatible terminal._)
```
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
**5. Run the frontend:**  
Navigate to playpal-frontend and run.
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

**Post-Setup Verification:**

TODO: How to confirm services are running (via logs, browser URLs, or health endpoints).

## **__3. User Stories and Functionality__**
Image of user stories from Arbeidskrav:

![Screenshot of user stories submitted in arbeidskrav](images/User Stories.jpg)

- [x]  **Story 1: As a user, I want to find people to play with.**
- [x]  **Story 2: As a user, I want to be able to see ones own and other people's profiles.**
- [x]  **Story 3: As a user, I want to be able to communicate with other users.**


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
Use our frontend to register multiple users. Create static posts,
apply to them on other accounts, accept and stop searching. (To create group). 
Add friends. Chat with friends, try to live search on one, check list on other. 
Live search on other and see if match based on algorithm.  
Check backend storage endpoints if necessary.
```
---
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
Click on profile to enter your own to see. Here you can link with runescape account, 
edit bio and image etc. If you click to other profiles you can upvote / downvote, 
and see their relevant stats / information.  
Check backend storage endpoints if necessary.
```

```
Relevant Endpoints:
```
**Story 3:** Communicate with other users.

Users can communicate with other users through chat and messages.
Full functionality for chatting with other users with websockets through gateway.  
Chats generated automatically through communication service, based on what happens. 
For example, Live Search generated group creates a conversation between those two, 
while accepting group with multiple users makes conversation for those users.  


*Services Involved:*
- Same services as user story 1 and 2.
- Communication Service: Handles real-time chat and messaging.

How to Test:

```
Login or generate user, and add someone/join a group or livesearch to match with someone.  
Try to chat with person in two browsers for visualizing messages, and check backend storage endpoints if necessary.
```

```
Relevant Endpoints:
```
All of our initial user stories are implemented with full functionality. We've also expanded
upon our initial user stories to include more functionality, such as the ability to link a runescape account to a profile, and view stats.
This was to reach the full potential of our project and best case scenario, and to showcase the full functionality of our services.

### **Additional "user stories" tests, and full workflow suggestion:**

_To test all functionality, we suggest the following workflow:_  

"**Story 4:** As a user I want to use a runescape group-finder application to find people to play with."
```
Todo:
Example data. Example runescape accounts? Write full functionality list test.
```


## **__4. Project Architecture__**
- [ ] a diagram showing the architecture of the system. This should show what services the
  project contains and what type of communication they have between them (synchronous or
  asynchronous).

**Services:**


A brief description of each microservice (e.g., User Service handles user registration and login, Profile Service manages user profiles and stats, etc.).
Mention what database each service uses (if any).
State whether a service communicates primarily via REST or message queues.

Each microservice focuses on a single concern. For example, the User Service handles core user data (login, registration), the Profile Service manages user profile enhancements, and the Live-search Service runs dynamic matching logic. This strict separation ensures easier testing, clearer code, and simpler maintenance.


Gateway Service Details:

Explain how the gateway routes external calls.
    Mention load balancing if implemented.

Configuration and Health Checks:

Show how Consul is used to manage configuration centrally.
    Explain how to access health check endpoints (e.g., http://localhost:<service-port>/actuator/health).
    Show a sample healthy response ({"status":"UP"}).

## **__5. Reflections on Architecture Choices__**
 **Microservices**

We made significant changes to our architecture from the initial proposal in the arbeidskrav.  
As our project grew, we realized that a more separated approach would be beneficial.  
We also realized that we had to separate concerns out further, as well as focus on key services, rather than some unecessary ones.

This resulted in more services, and a more complex architecture, with some changes, but also a more scalable/maintainable and functional one.

One of the key changes was to not create a separate service for the storage, but rather let each individual service have their own database.
This was to ensure that each service could be more independent, and that we could scale them individually if needed.  
It also means that services can do what they are intended to do, even if other service's databases are down.

We also decided to remove notifcation service, as the focus of this exam was not on frontend. As we already had quite a big frontend, 
we decided to focus on the backend services, and the communication between them.

We also renamed some of the services to better reflect their functionality, and to make it easier to understand what they do.

Due to the changes in architecture, we also had to change the communication patterns between services. This is reflected in our new architecture diagram.


During development, you may have to make a number of architecture decisions, assumptions
about the domain you are working with, or simplifications to how such a project would work
in reality. Document these decisions, assumptions, and simplifications in the README.md
file.



**CHANGES FROM ARBEIDSKRAV**

**Meeting Project Guidelines**
Below is a summary of how PlayPal meets each of the microservice-specific and architectural requirements outlined in the exam:

- Multiple Microservices and clear structure/functionality:
    The system includes multiple services that have separate functionality, clear structure, and communicate with each other.  
User, Profile, Live-Search, Communication, Group, and more. Each has a specific responsibility, adhering to Single-Responsibility Principles and SOLID.

- Synchronous Communication:
    The system includes a lot of synchronous communication as shown in the architecture. 
The User Service calls the Profile Service via REST to retrieve user details.

- Asynchronous Communication:
    The system includes asynchronous communication with RabbitMQ as shown in the architecture. 
For example, search post creation events are published to RabbitMQ, consumed by the Group Service to create groups, then sent to the Communication Service to create chat rooms.

- Consistent Architecture and Documentation:
    Our architecture diagram in Section 4 illustrates all services and their communication patterns. The README details each service’s role and how they connect.

- Single Entry Point & Load Balancing:
    The Gateway Service routes incoming requests to the correct microservices and distributes load evenly.

- Centralized Health Monitoring and configuration:
    Spring Boot Actuator health endpoints (/actuator/health) provide a simple means to check each service’s status.
 Consul manages service configurations. Consul also checks whether a service is "status":"UP", through our actuator health points (f.example. localhost:8080/group/actuator/health)
    
- Containerization:
    All services are containerized with Docker, and are running and interacting with each other. Build and run instructions are detailed in Section 2.

## **__7. Contributions__**

Our team collaborated effectively to develop PlayPal, with each member contributing to most aspects of the project. 
Considering this is a group project, we've all contributed to the project in various ways.
Throughout the semester, we worked together to design the architecture, implement services, and ensure seamless communication between components.

We knew from previous experience that working physically works well for us, and we therefore made the decision to do so for this project as well.  
As a natural result of this, we pair programmed a lot, and helped each other out with different tasks and services when needed.
There was also tasks related to architecture, theory application, design and documentation that was divided between us. 
However, the flow was quite natural, and whoever finished a task, would then proceed to do a "Highest Priority" task that was necessary to complete.
If someone struggled with a functionality, or had a question, we would help each other out by either swapping the
responsibility, or by pair programming to solve the issue together. 

When it comes to individual contributions, we had an initial responsibility assigned to each member, even though the tasks were not strictly divided, and we all worked on all services.
This was mostly to get some services up and running, and to have a starting point for each member. Not to decide that this service was yours alone to work on.

**We all worked on all services, and all services were touched by all members.**  
_Here's a breakdown of our contributions (based on initial responsibilities, but not sole responsibilities):_
- **Robin:** 
  - **Responsibility:** Group Service, Leaderboard service.
  - **Contributions:** Initial creation and work with Group Service, and leaderboard service. Worked on group creation, leaderboards.  
Worked on rabbitMQ and event driven.
  - Helped everyone else with their services/tasks.
- **Mathias:**
  - **Responsibility:** Frontend, RuneScape Service, Friend Service, general overview.
  - **Contributions:** Implemented most of the necessary frontend for our application. Worked on Runescape service and friend service, and also adjusted services as needed for correct system flow/architecture.
Due to having control of frontend, had a good overview of everyone else's work / services.
  - Helped everyone else with their services/tasks.
- **Borse:**
  - **Responsibility:** Search Service, Communication Service, Websocket, Live Search Service, Documentation/Theory.
  - **Contributions:** Implemented Search Service, Communication Service and Live Search service. Added initial websocket config for chats/messages. Worked on rabbitMQ and event driven.
  - Helped everyone else with their services/tasks. 
- **Ernad:**
  - **Responsibility:** Profile Service, Friend Service, Logging.
  - **Contributions:** Implemented user and profile service. Assisted Maiwand on docker setup and config. Centralized logging with docker.
  - Helped everyone else with their services/tasks.
- **Maiwand:**
  - **Responsibility:** Docker, Cleanup, General assisting others.
  - **Contributions:** Implemented Docker and containerization. Helped with cleanup and general assistance.
  - Helped everyone else with their services/tasks.


## **__8. Additional?**


Join us as we redefine how gamers connect and play together. Welcome to PlayPal, where your next adventure begins.