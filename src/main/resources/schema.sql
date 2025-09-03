
-- This file will initialize the database schema.
-- Spring Boot will automatically run this script on startup.

-- Drop tables if they exist to ensure a clean slate
DROP TABLE IF EXISTS message_vote;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS subthreads;
DROP TABLE IF EXISTS threads;
DROP TABLE IF EXISTS users;

-- Create the 'users' table
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    message TEXT,
    model_id VARCHAR(255),
    role SMALLINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create the 'threads' table
CREATE TABLE threads (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create the 'subthreads' table
CREATE TABLE subthreads (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    thread_id BIGINT NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES threads(id)
);

-- Create the 'messages' table
CREATE TABLE messages (
    id BIGINT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    upvote_count INT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE,
    subthread_id BIGINT NOT NULL,
    FOREIGN KEY (subthread_id) REFERENCES subthreads(id)
);

-- Create the 'message_vote' table
CREATE TABLE message_vote (
    message_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    upvoted BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (message_id, user_id),
    FOREIGN KEY (message_id) REFERENCES messages(id)
);
