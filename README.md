
# PG3402 Microservices exam
## _PlayPal delivery_
- Candidate number: 49
- Candidate number: 55
- Candidate number: 38
- Candidate number: 58
- Candidate number: 25

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

---
#### **Steps to Build and Run:**
_Either use .zip file provided or clone the repo._

**1. Clone the repo.**  
Navigate to root project folder with cd command based on where project's located on computer system.
```
git clone https://github.com/MathiasTor/MicroServices.git
cd <project-directory>
```
Replace `<project-directory>` with the path of the folder created by cloning.

----
**Prerequisites**

- **Maven:** Required to build the services locally.
- **Node**: For displaying the frontend, node.js must be installed on the computer. Verify installation by running:  
  **`node --version`**

***You can use the provided script to build all services automatically:***  
(**_Ensure the script is executed from the root folder and that you're using a Bash-compatible terminal._**)
```
./build-services.sh
```
---

> [!IMPORTANT]
>  If "Permission denied" run: chmod u+x build-services.sh

---
***2. Alternatively, navigate to the root directory of each service and run:***
```
mvn clean install -DskipTests
```  

### **__Option 1: Container/Docker build and run__**

**Prerequisites**  
PlayPal is fully containerized using Docker, ensuring that it can run seamlessly on any system with Docker installed.
- **Docker:** Ensure Docker is installed and running on your system.
Verify installation by running:

  **`docker --version`**


---

> [!NOTE]
> Since the services are containerized, they should work on any operating system that supports Docker (Linux, macOS, or Windows).

---

**Use the provided script to build all images automatically:**
(**_Ensure the script is executed from the root folder and that you're using a Bash-compatible terminal._**)
```
./build-docker.sh
```
---

> [!IMPORTANT]
>  If "Permission denied" run: chmod u+x build-docker.sh

---

***Alternatively, you can set up manually:***  
Make sure docker engine is running, for example through docker desktop.  
To run these commands, make sure you're in **root** folder of the project.
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
- `docker build -t livesearch-service:1.0 ./playpal-livesearch-service`

**Running the containers:**  
Navigate to docker directory and run docker compose file.

```
cd docker
docker-compose up --build
```

This will run all the docker containers and images according to the configurations.

When this is done, you can access the frontend on `localhost:3000`, and the backend services on their respective ports (addressed at the end of this section).  
Due to the nature of our system, combined with the size, we've created an extensive frontend for testing purposes. Please utilize it as much as possible to test required functionality, and endpoints.


----


### **__Option 2: Local Setup (without docker)__**
Follow these steps to build and run the project without Docker.  
This will require some tools installed on your system.  
**Prerequisites:**
- **Java 21**
- **Maven:** Required to build the services locally (if testing without Docker).
- **RabbitMQ:** For asynchronous communication and messaging if used locally.
- **Postman** (_optional_): For testing API endpoints directly.
- **Consul**: Centralized configuration management and service discovery tool.
- **Node.js**: For displaying the frontend, node.js must be installed on the computer. Verify installation by running:  
  **`node --version`**

Run commands to start RabbitMq (or use app RabbitMQ service - start. You can also use sudo.)
```
rabbitmq-server
```
Run commands to start Consul in its own terminal.
```
consul agent -dev -node playpal-local-dev-node
```

**4. Run the services:**  
Navigate to each service directory and run the service using Maven.
```
cd service-directory
mvn spring-boot:run
```



**5. Run the frontend:**  
Navigate to playpal-frontend and run.
```
cd playpal-frontend
npm install
npm start
```
### **Ports and logins**
These are the initial ports that services start at. You can either access them directly, or as recommended by us through the gateway.  
They are defined by their service names as prefix, such as search service = localhost:8080/search/** , localhost:8080/group/**.
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
- **Dozzle centrallized logging:** `9090`
- **H2 login:** _username:_ sa (jdbc url defined in each service application.properties )

**Post-Setup Verification:**

Check localhost:8500 to see if consul is running, and if all services are "healthy". This means the health checks are "UP" on the actuator/health endpoint, and therefore running.

You can also check Dozzle centrallized logging to check the active running services and their logs.
(localhost:9090)
Dozzle also has a mode called "swarm-mode". 
With this we have created labels that group different parts of our project/services.  
You can either use the swarm mode to see a grouping of the logs, or you can leave it off to see the logs of each individual service in their respective panels.


## **__3. User Stories and Functionality__**
Image of user stories from Arbeidskrav:

![Screenshot of user stories submitted in arbeidskrav](images/User-Stories.jpg)

- [x]  **Story 1: As a user, I want to find people to play with.**
- [x]  **Story 2: As a user, I want to be able to see ones own and other people's profiles.**
- [x]  **Story 3: As a user, I want to be able to communicate with other users.**


**_Implementation Details:_**

**Story 1:** Find People to Play With


Users can search for active users and befriend them, search live for other players, or browse/create static group posts.

*Services Involved:*

- Search Service: Handles creation of posts.
- Live-search Service: Handles live matchmaking with algorithm for matching.
- Runescape Service: Fetches runescape stats for matching algorithm, and profile showcasing.
- User Service: Handles registration and login.
- Profile service: Creation of profiles, which allows for interaction, as well as the voting system.
- Group Service: Creates groups with applicants from the posts, people found through live search or separate group creation.
- Gateway Service: Routes incoming requests to the correct microservices.
- Frontend to show all the functionality.

**Story 2:** View User Profiles

Users can view their own profile, as well as other users profiles. With this they can look at their RuneScape stats if they're linked to assess their skills and experience. 
Upvoting and downvoting system in case someone makes for a bad experience, or vice versa.  
And AI generated profile picture.

*Services Involved:*
- User Service: Handles registration and login.
- Profile Service: Handles creation of profiles, and manages the vote system.
- Runescape Service: Fetches runescape stats for profile showcasing.
- Gateway Service: Routes incoming requests to the correct microservices.
- Frontend to show all the functionality.

**Story 3:** Communicate with other users.

Users can communicate with other users either through private messages, or group messages.
Full functionality for chatting with other users with websockets through gateway.  
Chats are created through the communication service. Either upon group creation, befriending other users, or by creating your own direct message. 
For example, creating a group will create chat for that group. Another example is adding a friend, this will create a DM between the two users.  
Users can also start a chat with other users, without friending them.

*Services Involved:*
- User Service: Handles registration and login.
- Friend Service: Handles befriending and blocking users.
- Group Service: Creates groups, and handles group communication.
- Communication Service: Handles real-time chatting and messaging with websockets.
- Gateway Service: Routes incoming requests to the correct microservices.
- Frontend to show all the functionality.
---
_All of our initial user stories are implemented with full functionality._
We've also expanded upon our initial user stories to include more functionality.  
This includes: 
- Being able to link your profile with RuneScape, doing a GET to RuneScape High Scores, and then showcase the stats with updates.
- Generate AI image for your profile through an external openAI DALL-E api.
- Upvote and downvote system for profiles.
- Leaderboard showcasing both overall raid completions, and a weekly updated leaderboard.
- Live search functionality for matching with other users with an algorithm on RuneScape stats.


_This was to reach the full potential of our project and desired/best case scenarios, and to showcase the full functionality of our services.  
With this added functionality we also get to address the original user stories, in a more interesting way._

### **Additional "user story" test, with full workflow suggestion:**

**_To test all the microservices' functionality , we suggest the following workflow:_** 

"**Story 4:** As a user I want to use a runescape group-finder application to find people to play with."

---

> [!NOTE]
> In general, the frontend should be quite intuitive, so try to click around and use the functionality.
However, below we have a full workflow explaining steps to test functionality detailed. Hopefully it is helpful.

---

> [!WARNING]
> **_This application is meant to be used across different systems. If you test locally, there might be some
interfering issues with cookies. It's therefore very important to make sure that your cookies are individual for each user on page refresh,
to be able to test the functionality._**  
> **_We recommend to open a browser, and an incognito tab of that same browser, and a separate third browser. Or three separate browsers.
Afterward, create the users, and confirm that your profile page/cookie ID remains the same as the correct user you logged in as on each browser._**

---

The first thing you have to do is to register multiple users. Here are some example users you can register:
```
username: bob
email: bob@123.no
password:123
```
```
username: kari
email: kari@123.no
password:123
```

```
username: per
email: per@123.no
password:123
```


Then login to these users on their respective browsers.  
On the home page, choose RuneScape game. Then proceed to create a post. Give it a title, a description and tag.

Here is an example of a post you can use:
```
Title: Cutting Willowtrees
Description: Looking for someone to cut willow trees with me. I have a rune axe.
Tags: woodcutting, willow, axe
```
Press submit, and view your own post. Go on another browser/user, click home page, and enter RuneScape.  
You should now see the post you created. Apply to the group. Go back to the user that created the post, and refresh (no websockets on this page, only communication).
There should be an applicant. Approve it.  
You now have a state of a group, but it hasn't been created yet. To create the group (when you're happy with the amount of users), Stop the Search for participants.
The group will now be created, and you can see it under the group tab. You can also chat with the group.

You can also then add users to this existing group, in the "Add people by Username" field. Add your last user to this group, and check the group tab.
You can also statically create your own group, and invite to it under the group tab. 

On the post page, usernames are clickable, and should navigate to their profile pages. Click one profile to view their profile (not yours).
This should bring you to a URL with the user's ID profile. Here you can see their stats if they are linked with a runescapep account(we will do this later), and you can upvote / downvote.
You can confirm that the upvotes work by entering another browser/user to see if it updated.

In the bottom right, you can add friends, and access direct message chats. Test these buttons with the user's names.  
Generate a chat, add some friends, block, etc. If frontend doesn't update, remember to refresh. 

```
Example: Kari, clicks friends, then "+". Writes bob, and add friend. Enter bob browser, accept.
 Chat is generated(under chat tab) and you can interact.
```
Proceed to your own Profile, by clicking the Profile tab. This is different from when you enter other user's profile, as you can "link runescape account",  
and "Edit Profile".  
Try to Edit Profile, update your bio, and generate an AI picture. When generating an AI picture, it will say "Loading..." for a bit, but then eventually update.
After doing this. Link your account to a RuneScape account.  

**This is important for testing LiveSearch functionality and leaderboard. We therefore recommend you to use the following RuneScape Usernames:**  

_Link each browser's user to a unique RuneScape account._
```
User 1: (bob from our example)
RuneScape Username to Link with: woox

User 2: (kari from our example)
RuneScape Username to Link with: a cold one

User 3: (per from our example)
RuneScape Username to Link with: erntt
```
Now that you've linked the profiles. You can go to the leaderboard tab, and see the leaderboard. If it hasn't updated yet, wait for the 30-second update timer. 
This displays the overall raid completions of each user, and also has a weekly leaderboard, that updates monday 12 o'clock every week, to see who won the most raid completions that week.

_As you just started, the weekly leaderboard doesn't have any raid completions, but we attached some images of functioning weekly leaderboard._

if you click the profiles of other users again, from the post page, you should now see their runescape stats and raid completions on their profile.

Lastly, we have a live-search algorithm using the sum of collected raid completions of the RuneScape accounts, to match the players based on their skill level/experience. 
It will match you with the closest user to your own raid completion count. The threshold will decrease the longer you're in queue, to a max percentage of 50% of the total amount of the user's raid completions.
Because of this, to match two users, you need to be sure that the users you go live with are within each other's 50% range. 

For testing purposes, it will decrease the threshold every 5 seconds, to reach 50% after 25 seconds. In real use it would be higher with more users.

Therefore, to test this functionality, go live on user 2(kari from our example, or the **user linked with the highest score "a cold one")**.  
Then go live on user 1(bob from our example, or the **user linked with the middle score "woox")**.  
Since these users are too far apart, no match will be found.
Proceed to go live on user 3(per from our example, or the **user linked with the lowest score "erntt")**.  
You should now match with the fitting other player, a group should be created, and a conversation should be created.

If you have any issues, remember to refresh the page, as the frontend doesn't always update as only communication service has websockets.

## **__4. Project Architecture__**

![Screenshot of user stories submitted in arbeidskrav](images/Microservices-Architecture.png)

**Architecture overview:**

The architecture of PlayPal is based on a microservices approach, with each service handling a specific concern. Following the SRP (Single Responsibility Principle).

Our **frontend** is a separate service, which communicates with the backend services through the Gateway. 
The **Gateway** routes and handles incoming requests to the correct microservices and distributes load evenly.  
Our **User Service** handles core user data, such as registration and login. This service communicate asynchronously with the **Profile Service** via rabbitMQ messages to create profiles. 
When you register a user, a message is published, and listened to from **Profile Service** to create a profile.  
We then have the **Search Service**, that handles searching for other users to play with. This service handles creation of posts. 
Whenever a post is created, applied to, and then ended - a rabbitMQ message is published, and listened to by the **Group Service** to create a group.  
The **Group Service** handles creation of groups, and when it creates a group, it publishes another async message that is listened to by **communication service**. This flow ensures that no data is lost, and a post is created, and then a group is created, and then a chat is created.  
The **Live Search Service** handles live search functionality, and matches users based on their RuneScape raids completions. This service communicates synchronously with the **Runescape Service** and the **Group Service**, to get the stats and to create the group when matched.  
The **RuneScape Service** handles fetching of RuneScape stats from the official RuneScape high scores.  
The **Leaderboard Service** communicates with the **RuneScape Service** with synchronous communication to get the raid completions, and then creates a leaderboard based on the these stats.
The **Friend Service** handles befriending and blocking of users. It communicates sync with the **Communication Service** to create a conversation upon befriending.

## **__5. Reflections on Architecture Choices__**

We set up our communication patterns to be both synchronous and asynchronous following the Microservices Principles. We also made sure to 
separate concerns, and to have a clear structure of our services. In the services where data loss could be a problem, we made sure to use RabbitMQ to ensure that no data was lost.
  
As an example, if you create a group, it's important that a chat is generated. If not, you have to recreate the entire group to create a new chat.  
If communication service was down and a group was created, the message of a conversation/chat would still be sent and listened to as the communication service started up again.
On the other hand, if you add a friend, it's not strictly necessary to create a conversation between them. This is because you will still have the possibility to create a chat between these two friends at a later time, without having to re-add them.

When a post is stopped, it is crucial that a group is created. If search service and group service communicated synchronously, and the group service was down, no group would be created as a user ended a search post.  
This is not logical in our system, as a post should always result in the creation of a group. Therefore, we made sure that the communication was async. 

This flow results in a post being stopped, then a group being created, and then a chat being created for that group. No matter the health status of other services. 

When a user registers an account, it's important that a profile is created for that user, hence the asynchronous communication. This is because a user should always have a profile.

The leaderboard service communicates with the Runescape service synchronously, as the leaderboard is based on the stats of the Runescape service. This is because the leaderboard should always be up to date with the stats of the users.

The live search service communicates sync to both the runescape service and the group service. This is because the live search service also need the up-to-date runescape stats.  
Our reasoning behind the synchronous communication to the group service, is due to the fact that the group should be created as soon as the users are matched.  
If group service was down when a match was found, creating a group at a later time through an event, would not make logical sense due to the nature of the Live Search Service.  
If the group creation should for some reason fail, we would not want to find some old matched group at a later time with a random person. It's rather that you want to match right now and play.


To prevent coupling between services where it's not strictly needed, we implemented the use of referencing userIDs instead of transferring user objects.  
By using only a userId in services that need user-related data, we avoid tightly coupling services to the user service.  
If the user service changes its Model (for example, renaming fields, adding new fields), other services relying only on the userId remain unaffected. This minimizes the ripple effect of changes.

We made changes to our architecture from the initial proposal in the arbeidskrav, but still stayed true to the original idea and concept.
As our project grew, we realized that a more separated approach would be beneficial.  
We also realized that we had to separate concerns out further, as well as focus on key services, rather than some unecessary front-end ones.  
This resulted in more services, and a more complex architecture, with some changes, but also a more scalable, maintainable and functional one.

One of the key changes was to not create a separate service for the storage, but rather let each individual service have their own database.
This was to ensure that each service could be more independent, and that we could scale them individually if needed.  
It also means that services can do what they are intended to do, even if other services are down.

We also decided to remove notification service, as the focus of this exam was not on frontend. As we already had quite a big frontend, 
we decided to focus on the backend services, and the communication between them. Implementing a notification service would only result in a better frontend, but not actually expand on our functionality. 


We also renamed some of the services to better reflect their purpose, and to make it easier to understand what they do.
The added functionality required some new services, such as leaderboard service, RuneScape service and Live Search Service. 

Due to the changes in architecture, we also had to change the sync/async communication between services. This is displayed in our new architecture diagram.



**Meeting Project Guidelines**
Below is a summary of how PlayPal meets each of the microservice-specific and architectural requirements outlined in the exam:

- Multiple Microservices and clear structure/functionality:
    The system includes multiple services that have separate functionality, clear structure, and communicate with each other.  
User, Profile, Live-Search, Communication, Group, and more. Each has a specific responsibility, adhering to Single-Responsibility Principles and SOLID.

- Synchronous Communication:
    The system includes a lot of synchronous communication as shown in the architecture. 

- Asynchronous Communication:
    The system includes asynchronous communication with RabbitMQ as shown in the architecture. 
For example, search post creation events are published to RabbitMQ, consumed by the Group Service to create groups, then sent to the Communication Service to create chat rooms.

- Consistent Architecture and Documentation:
    Our architecture diagram in Section 4 illustrates all services and their communication patterns. The README details each service’s role and how they connect.

- Single Entry Point & Load Balancing:
    The Gateway Service routes incoming requests to the correct microservices and distributes load evenly.

- Centralized Health Monitoring and configuration:
    Spring Boot Actuator health endpoints (/actuator/health) provide a simple means to check each service’s status.
 Consul manages service configurations. Consul also checks whether a service is "status":"UP", through our actuator health points.
    
- Containerization:
    All services are containerized with Docker, and are running and interacting with each other. Build and run instructions are detailed in Section 2.
We also added centrallized logging for our containers, that was incredibly beneficial for our project as it grew quite large.

## **__6. Contributions__**

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
  - **Responsibility:** Group Service, Leaderboard service, gateway.
  - **Contributions:** Initial creation and work with Group Service, leaderboard service and gateway. Worked on group creation, leaderboards.  
Worked on rabbitMQ and event driven.
  - Helped everyone else with their services/tasks.
- **Mathias:**
  - **Responsibility:** Frontend, RuneScape Service, Friend Service, general overview.
  - **Contributions:** Implemented most of the necessary frontend for our application. Worked on Runescape service and friend service, and also adjusted services as needed for correct system flow/architecture.
Due to having control of frontend, had a good overview of everyone else's work / services.
  - Helped everyone else with their services/tasks.
- **Borse:**
  - **Responsibility:** Search Service, Communication Service, Live Search Service, Documentation.
  - **Contributions:** Implemented Search Service, Communication Service and Live Search service. Added websocket config for chats/messages. Worked on rabbitMQ and event driven.
  - Helped everyone else with their services/tasks. 
- **Ernad:**
  - **Responsibility:** Profile Service, User Service, Logging.
  - **Contributions:** Implemented user and profile service. Assisted Maiwand on docker setup and config. Centralized logging with docker. Added algorithm to matching in Live Search Service.
  - Helped everyone else with their services/tasks.
- **Maiwand:**
  - **Responsibility:** Docker, Cleanup, General assisting others.
  - **Contributions:** Implemented Docker and containerization. Helped with cleanup and general assistance.
  - Helped everyone else with their services/tasks.


## **__7. Relevant-Endpoints__**
If you would like to test some functionality through Postman instead of our frontend, we have listed some relevant endpoints below.
These are just some of the endpoints, and we recommend you to look at the code to see all the endpoints.


All these endpoints should be run through the gateway on localhost:8080/**

`Example: GET localhost:8080/user/api/users/all`


- **User Service:**
    - **POST /user/api/users/:** Register a new user. ** username, email, password json object**
    - **GET /user/api/users/all:** Get all users.
    - **GET /user/api/users/{id} :** Get a specific user by id.
- **Profile Service:**
    - **POST /profile/api/profiles/:** Create a profile. || profile is made automatically when user is created.
    - **GET /profile/api/profiles/all:** Get all profiles. 
- **Search Service:**
    - **POST /search/api/search/posts:** Create a search post.
    - **POST /search/api/search/posts/apply/{postId}/{userId} :** Apply to a search post.
    - **POST /search/api/search/posts/approve/{postId}/{userId} :** Approve an applicant to a search post.
    - **POST /search/api/search/posts/stop/{id} :** Stop search for participants.
    - **GET /search/api/search/all :** Get all search posts.
- **Group Service:**
   - **POST /group/api/group/new:** Create a group.
   - **GET /group/api/group/all :** Get all groups.
   - **GET /group/api/group/id/:id :** Get a specific group by id.
- **RuneScape Service:**
    - **POST /runescape/api/runescape/link-user/{userid}/{runescapeName} :** Link a user to a RuneScape account.
    - **GET /runescape/api/runescape/stats/{username} :** Get RuneScape stats for a user.
    - **GET /runescape/api/runescape/fetch-stats/{runescapeName} :** Fetch RuneScape stats for a user.
- **Leaderboard Service:**
    - these endpoints will only work when an runescape account is linked with user id.
   - **GET /leaderboard/api/leaderboard/overall:** Get overall leaderboard.
   - **GET /leaderboard/api/leaderboard/weekly:** Get weekly leaderboard.
   - **GET /leaderboard/api/leaderboard/weekly/update:** Update weekly leaderboard.
   - **GET /leaderboard/api/leaderboard/overall/update:** Update overall leaderboard.
- **Communication Service:**
   - **POST /communication/api/conversations :** Create a new chat.
   - **POST /communication/api/conversations/dm :** Create a new direct message chat.
   - **GET /communication/api/conversations/all :** Get all conversations.
   - **GET /communication/api/conversations/{conversationId}/details :** Get conversation details.
- **Friend Service:**
   - **POST /friend/api/friends/send-request/{userId}/{friendId} :** Send a friend request.
   - **POST /friend/api/friends/add-friend/{userId}/{friendId} :** Accept a friend request.
   - **POST /friend/api/friends/block/{userId}/{friendId} :** Block a user.
   - **GET /friend/api/friends/get-friends/{userId} :** Get all friends for a user.
- **Livesearch Service:**
   - **POST /livesearch/api/live/new/{userID} :** Creates a livesearch for the user.
   - **GET /livesearch/api/live/all :** Se all the logs for all the users that has created entries.
   - **PUT /livesearch/api/live/stop/{userID} :** Stops the search.
   - **GET /livesearch/api/live/status/{userID} :** Checks if the user is online.
   - **GET /livesearch/api/live/unread-match/{userID} :** Checks if you have a unread notification.
- **Gateway Service:**
    - **All endpoints:** Routes incoming requests to the correct microservices.
- **Frontend:**
    - **All endpoints:** Routes to the correct service endpoints.
- **Consul:**


Thank you for checking out PlayPal!
