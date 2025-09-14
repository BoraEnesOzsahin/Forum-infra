# Forum Application

A Spring Boot-based forum application providing REST API endpoints for threads, subthreads, messages, and voting, with model-based access control.

---

## 🛠️ Technologies

- **Spring Boot 3.1.5**
- **Java 21**
- **Maven**
- **Spring Data JPA**
- **PostgreSQL**
- **Hibernate**
- **Spring Security**
- **SpringDoc OpenAPI 3 (Swagger UI)**
- **Lombok**
- **Spring Boot Actuator**
- **Spring Boot DevTools**

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Database Setup

1. Create a PostgreSQL database named `forum`.
2. Update credentials in `src/main/resources/application.yml`.
3. Tables and dummy data are auto-created on startup.

### Running the Application

1. Clone the repository.
2. Open in VS Code or your preferred IDE.
3. Run `ForumApplication.java`.
4. Access at `http://localhost:8080`.

---

## 📚 API Documentation (Swagger UI)

Access interactive docs at:  
**[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 📋 API Endpoints

### 🧵 Thread Management

- `GET /threads/getAllThreads` — List all threads
- `POST /threads/createThread` — Create a thread
- `GET /threads/{id}` — Get thread by ID
- `DELETE /threads/deleteThread/{id}` — Delete a thread

### 🌿 SubThread Management

- `GET /subThreads/allSubThreadsByThreadId/{threadId}` — List subthreads for a thread
- `POST /subThreads/createSubThread` — Create a subthread
- `GET /subThreads/{id}` — Get subthread by ID
- `DELETE /subThreads/deleteSubThread/{id}` — Delete a subthread

### 💬 Message Management

- `GET /messages/allMessagesBySubThreadId/{subThreadId}` — List messages for a subthread, ordered by upvotes
- `POST /messages/createMessage` — Create a message
- `GET /messages/{id}` — Get message by ID
- `DELETE /messages/deleteMessage/{id}` — Delete a message

### 👍 Message Voting

- `POST /messageVotes/createMessageVote` — Upvote a message
- `DELETE /messageVotes/deleteMessageVote/{id}` — Remove a vote

---

## 🎯 Using Swagger UI

### ⚠️ Input Field Guidelines

**Only include required fields in your request body.**  
Do **not** include fields like `id`, `createdAt`, `updatedAt`, or `upvoteCount`—these are auto-generated.

---

### 📝 Input Examples

#### Create Thread

```json
{
  "username": "alice_johnson",
  "role": 0,
  "modelId": "BMW",
  "title": "Best BMW maintenance tips"
}
```

#### Create SubThread

```json
{
  "username": "bob_smith",
  "title": "Engine maintenance discussion",
  "threadId": 1
}
```

#### Create Message

```json
{
  "username": "charlie_brown",
  "body": "This is my message content about car maintenance.",
  "subThreadId": 1
}
```

#### Create Message Vote

```json
{
  "username": "diana_wilson",
  "messageId": 1,
  "upvoted": true
}
```

---

## 💬 Message Query Logic

Messages are linked to subthreads via a relationship, not a raw `subThreadId` field.  
The repository uses either:

```java
List<Message> findBySubThreadOrderByUpvoteCountDesc(SubThread subThread);
```
or, for ID-based queries:
```java
@Query("SELECT m FROM Message m WHERE m.subThread.id = :subThreadId ORDER BY m.upvoteCount DESC")
List<Message> findBySubThreadIdOrderByUpvoteCountDesc(@Param("subThreadId") Long subThreadId);
```

---

## 👥 Test Users

| Username        | Model ID | Role (0=Admin, 1=User) | Description                        |
|-----------------|----------|------------------------|------------------------------------|
| alice_johnson   | BMW      | 0                      | Admin, BMW enthusiast              |
| bob_smith       | Mercedes | 1                      | User, Mercedes fan                 |
| charlie_brown   | Audi     | 1                      | User, Audi helper                  |
| diana_wilson    | Tesla    | 1                      | User, EV advocate                  |
| eve_davis       | BMW      | 1                      | User, BMW meets                    |
| frank_miller    | Mercedes | 0                      | Admin, automotive expert           |
| grace_lee       | Audi     | 1                      | User, German engineering fan       |
| henry_taylor    | Tesla    | 1                      | User, sustainable transport        |

Use the **username** field in API calls.

---

## 🔒 Access Control

- Users can only create threads/subthreads for their assigned `modelId`.
- Cross-model posting is blocked (e.g., BMW user cannot post in Mercedes thread).
- Admins (role=0) can bypass model restrictions.

---

## 🛡️ Error Handling

All errors return:

```json
{
  "status": false,
  "message": "Error description",
  "data": null
}
```

Common errors:
- `UserNotFoundException` (404)
- `IdMismatchException` (400)
- `MissingRelationException` (400)

---

## 🗄️ Database Schema

- **users** — User accounts
- **threads** — Main topics
- **subthreads** — Sub-discussions
- **messages** — Posts
- **message_vote** — Upvotes

Key relationships:
- Thread → SubThread (one-to-many)
- SubThread → Message (one-to-many)
- User → MessageVote (many-to-many)

---

## 🧪 Testing Workflow

1. Create a thread with a test user.
2. Add subthreads to your thread.
3. Post messages to subthreads.
4. Upvote messages.
5. Test access control by attempting cross-model actions.

---

## 📊 Response Format

All API responses:

```json
{
  "status": true,
  "message": "Operation result",
  "data": { ... }
}
```

---

## Minimal Required Fields

- **Thread:** `username`, `role`, `modelId`, `title`
- **SubThread:** `username`, `title`, `threadId`
- **Message:** `username`, `body`, `subThreadId`
- **Vote:** `username`, `messageId`, `upvoted`

---

## cURL Examples

Create message:
```sh
curl -X POST http://localhost:8080/messages/createMessage ^
  -H "Content-Type: application/json" ^
  -d "{\"subThreadId\":1,\"body\":\"Check torque specs.\",\"username\":\"john_doe\"}"
```

View messages (by upvotes):
```sh
curl http://localhost:8080/messages/allMessagesBySubThreadId/1
```

Vote:
```sh
curl -X POST http://localhost:8080/messageVotes/createMessageVote ^
  -H "Content-Type: application/json" ^
  -d "{\"messageId\":1,\"username\":\"john_doe\",\"upvoted\":true}"
  ```

---

## Notes

- Do not include system-generated fields in Swagger or API requests.
- Admins can act across model boundaries.
- All relationships are managed via entity references, not raw IDs except where specified.

---