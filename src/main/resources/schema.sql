-- This file will initialize the database schema.
-- Spring Boot will automatically run this script on startup.

-- Drop tables if they exist to ensure a clean slate with CASCADE
DROP TABLE IF EXISTS message_vote CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS subthreads CASCADE;
DROP TABLE IF EXISTS posts CASCADE;
DROP TABLE IF EXISTS threads CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create the 'users' table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    message TEXT,
    model_id VARCHAR(255),
    role VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create the 'threads' table
CREATE TABLE IF NOT EXISTS threads (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create the 'subthreads' table
CREATE TABLE IF NOT EXISTS subthreads (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thread_id BIGINT NOT NULL,
    FOREIGN KEY (thread_id) REFERENCES threads(id)
);

-- Create the 'messages' table
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    upvote_count INT NOT NULL DEFAULT 0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE,
    subthread_id BIGINT NOT NULL,
    FOREIGN KEY (subthread_id) REFERENCES subthreads(id)
);

-- Create the 'message_vote' table
CREATE TABLE IF NOT EXISTS message_vote (
    message_id BIGINT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    upvoted BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (message_id, user_id),
    FOREIGN KEY (message_id) REFERENCES messages(id)
);