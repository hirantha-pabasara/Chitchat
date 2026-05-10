# Chitchat

A Java Servlet + Hibernate based chat backend project with MySQL persistence, user profile handling, and basic one-to-one messaging APIs.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Core Data Model](#core-data-model)
- [API Endpoints](#api-endpoints)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [How to Run](#how-to-run)
- [Notes](#notes)

## Overview

Chitchat provides backend functionality for a simple chat application.  
It includes:

- User registration and login
- Profile update with avatar upload
- User list and conversation summary loading
- Real-time-style chat fetch/send endpoints
- Chat and user status tracking

## Features

- **Sign up** with mobile, name, password, and optional avatar image
- **Sign in** with credential validation
- **Update profile** details and avatar image
- **Load home/chat list** with last message previews
- **Load conversation** between two users
- **Send messages** between users
- **Get avatar initials** fallback for users without profile image

## Technology Stack

- **Language:** Java
- **Web Layer:** Java Servlet API (`@WebServlet`, multipart upload)
- **ORM:** Hibernate (JPA annotations + Criteria API)
- **Database:** MySQL
- **JSON Processing:** Gson
- **Server Target:** GlassFish-compatible deployment

## Project Structure

```text
Chitchat/
├── src/
│   ├── java/
│   │   ├── controller/        # Servlet endpoints
│   │   ├── entity/            # Hibernate entity classes
│   │   ├── model/             # Hibernate utility and validations
│   │   └── hibernate.cfg.xml  # Hibernate + DB configuration
│   └── conf/
│       └── MANIFEST.MF
├── web/
│   ├── index.html
│   └── WEB-INF/
│       └── glassfish-web.xml
└── lib/                       # Required JAR dependencies
```

## Core Data Model

- **User** (`user`)
  - `id`, `mobile`, `first_name`, `last_name`, `password`, `registered_date_time`, `user_status_id`
- **User_Status** (`user_status`)
  - `id`, `name`
- **Chat** (`chat`)
  - `id`, `from_user_id`, `to_user_id`, `message`, `date_time`, `chat_status_id`
- **Chat_Status** (`chat_status`)
  - `id`, `name`

## API Endpoints

### 1) `POST /SignUp`
Registers a new user.

- Content type: `multipart/form-data`
- Parameters:
  - `mobile`
  - `firstName`
  - `lastName`
  - `password`
  - `avatarImage` (optional file)

### 2) `POST /SignIn`
Authenticates a user.

- Content type: `application/json`
- Body:
  - `mobile`
  - `password`

### 3) `POST /UpdateUser`
Updates user profile data and optional avatar image.

- Content type: `multipart/form-data`
- Parameters:
  - `mobile`
  - `first_name`
  - `last_name`
  - `avatarImage` (optional file)

### 4) `GET /LoadHomeData`
Loads chat-list/home data for the logged-in user.

- Query params:
  - `id` (logged-in user ID)

### 5) `GET /LoadChat`
Loads chat history between two users.

- Query params:
  - `logged_user_id`
  - `other_user_id`

### 6) `GET /SendChat`
Sends a new chat message.

- Query params:
  - `logged_user_id`
  - `other_user_id`
  - `message`

### 7) `GET /GetLetters`
Returns initials for a user by mobile number.

- Query params:
  - `mobile`

## Prerequisites

- JDK 8+ (or compatible with your servlet container)
- MySQL server
- GlassFish/Payara (or another compatible Java EE servlet container)
- The required dependency JARs available in `lib/`

## Configuration

Database and Hibernate settings are in:

- `src/java/hibernate.cfg.xml`

Update these values for your environment before running:

- `hibernate.connection.url`
- `hibernate.connection.username`
- `hibernate.connection.password`

Also ensure the database schema includes the required tables:

- `user`, `user_status`, `chat`, `chat_status`

## How to Run

1. Configure MySQL and create the `chit_chat` database.
2. Create required tables and initial status records.
3. Update DB connection settings in `hibernate.cfg.xml`.
4. Build/package the project in your Java IDE or build tool setup.
5. Deploy to GlassFish/Payara.
6. Invoke servlet endpoints from your frontend/client.

## Notes

- This repository currently does not include a complete frontend implementation (only a placeholder `web/index.html`).
- There is no automated test suite or single standard build command included in the repository root.
- Avatar images are stored under the deployed web app path in an `AvatarImages` directory.
