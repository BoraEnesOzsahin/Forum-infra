-- This file will initialize the database schema.
-- Spring Boot will automatically run this script on startup.

-- Users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    message TEXT,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User model ids (multi-model)
CREATE TABLE IF NOT EXISTS user_model_ids (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    model_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, model_id)
);

-- Threads
CREATE TABLE IF NOT EXISTS threads (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    type VARCHAR(255),
    model_id VARCHAR(255),
    title VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- SubThreads
CREATE TABLE IF NOT EXISTS subthreads (
    id BIGSERIAL PRIMARY KEY,
    thread_id BIGINT NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    title VARCHAR(255),
    user_id VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Messages
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    subthread_id BIGINT NOT NULL REFERENCES subthreads(id) ON DELETE CASCADE,
    user_id VARCHAR(255),
    body TEXT,
    upvote_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ
);

-- Message votes (composite PK)
CREATE TABLE IF NOT EXISTS message_vote (
    message_id BIGINT NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    upvoted BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    PRIMARY KEY (message_id, user_id)
);

-- Tags (ElementCollection join tables)
CREATE TABLE IF NOT EXISTS thread_tags (
    thread_id BIGINT NOT NULL REFERENCES threads(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (thread_id, tag)
);

CREATE TABLE IF NOT EXISTS subthread_tags (
    subthread_id BIGINT NOT NULL REFERENCES subthreads(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (subthread_id, tag)
);

CREATE TABLE IF NOT EXISTS message_tags (
    message_id BIGINT NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (message_id, tag)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_threads_user_title ON threads(user_id, title);
CREATE UNIQUE INDEX IF NOT EXISTS ux_subthreads_thread_title ON subthreads(thread_id, title);
CREATE UNIQUE INDEX IF NOT EXISTS ux_messages_unique_seed ON messages(subthread_id, user_id, body);

