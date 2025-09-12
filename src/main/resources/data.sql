-- Insert dummy data for testing the forum application
-- This ensures there are users available to create threads and messages

-- The 'created_at' column is intentionally omitted from these INSERT statements.
-- The database schema defines 'DEFAULT CURRENT_TIMESTAMP' for this column,
-- so the database will automatically populate it with the current time upon insertion.

-- Minimal seed (idempotent)

-- Users
INSERT INTO users (username, message, role) VALUES
('admin', 'built-in admin', 'ADMIN'),
('admin_user', 'Welcome to the forum! I am the administrator.', 'ADMIN'),
('john_doe', 'Hi everyone! Happy to be part of this community.', 'CITIZEN'),
('jane_smith', 'Looking forward to great discussions!', 'CITIZEN'),
('alice', 'Alice here! Excited to join.', 'CITIZEN'),
('bob', 'Bob reporting for duty.', 'CITIZEN'),
('carol', 'Carol loves cars and tech.', 'CITIZEN'),
('dave', 'Dave is your admin.', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- User model ids
INSERT INTO user_model_ids (user_id, model_id)
SELECT id, 'M1' FROM users WHERE username IN ('admin','admin_user','alice','bob')
ON CONFLICT DO NOTHING;
INSERT INTO user_model_ids (user_id, model_id)
SELECT id, 'M2' FROM users WHERE username IN ('bob','carol') ON CONFLICT DO NOTHING;
INSERT INTO user_model_ids (user_id, model_id)
SELECT id, 'M3' FROM users WHERE username = 'alice' ON CONFLICT DO NOTHING;
INSERT INTO user_model_ids (user_id, model_id)
SELECT id, 'M4' FROM users WHERE username = 'dave' ON CONFLICT DO NOTHING;

-- Threads (user_id is stored as VARCHAR in schema)
INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(u.id AS VARCHAR), 'PERSONAL', 'model_admin_001', 'Welcome to the Forum'
FROM users u WHERE u.username = 'admin_user'
ON CONFLICT (user_id, title) DO NOTHING;

INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(u.id AS VARCHAR), 'COMMERCIAL', 'model_user_002', 'Best Practices for Vehicle Maintenance'
FROM users u WHERE u.username = 'john_doe'
ON CONFLICT (user_id, title) DO NOTHING;

INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(id AS VARCHAR), 'PERSONAL', 'M1', 'Alice''s Thread' FROM users WHERE username = 'alice'
ON CONFLICT DO NOTHING;
INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(id AS VARCHAR), 'PERSONAL', 'M2', 'Bob''s First Thread' FROM users WHERE username = 'bob'
ON CONFLICT DO NOTHING;
INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(id AS VARCHAR), 'COMMERCIAL', 'M1', 'Bob''s Second Thread' FROM users WHERE username = 'bob'
ON CONFLICT DO NOTHING;
INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(id AS VARCHAR), 'PERSONAL', 'M2', 'Carol''s Thread' FROM users WHERE username = 'carol'
ON CONFLICT DO NOTHING;
INSERT INTO threads (user_id, type, model_id, title)
SELECT CAST(id AS VARCHAR), 'PERSONAL', 'M4', 'Admin Thread' FROM users WHERE username = 'dave'
ON CONFLICT DO NOTHING;

-- Subthreads (resolve thread by (creator, title))
INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'Forum Rules and Guidelines', t.id
FROM users u
JOIN threads t ON t.title = 'Welcome to the Forum' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'admin_user'
ON CONFLICT (thread_id, title) DO NOTHING;

INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'General Discussion', t.id
FROM users u JOIN threads t ON t.title = 'Alice''s Thread' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'alice'
ON CONFLICT DO NOTHING;

INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'Tips', t.id
FROM users u JOIN threads t ON t.title = 'Bob''s First Thread' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'bob'
ON CONFLICT DO NOTHING;

INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'Questions', t.id
FROM users u JOIN threads t ON t.title = 'Bob''s First Thread' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'bob'
ON CONFLICT DO NOTHING;

INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'Showcase', t.id
FROM users u JOIN threads t ON t.title = 'Carol''s Thread' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'carol'
ON CONFLICT DO NOTHING;

INSERT INTO subthreads (user_id, title, thread_id)
SELECT CAST(u.id AS VARCHAR), 'Admin Announcements', t.id
FROM users u JOIN threads t ON t.title = 'Admin Thread' AND t.user_id = CAST(u.id AS VARCHAR)
WHERE u.username = 'dave'
ON CONFLICT DO NOTHING;

-- Messages (resolve subthread by title and creator)
INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Welcome everyone! Please read the forum rules before posting.', 5, st.id
FROM users u
JOIN threads  t  ON t.title = 'Welcome to the Forum' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Forum Rules and Guidelines'
WHERE u.username = 'admin_user'
ON CONFLICT (subthread_id, user_id, body) DO NOTHING;

INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Welcome to Alice''s thread!', 2, st.id
FROM users u
JOIN threads t ON t.title = 'Alice''s Thread' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'General Discussion'
WHERE u.username = 'alice'
ON CONFLICT DO NOTHING;

INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Bob shares his first tip.', 1, st.id
FROM users u
JOIN threads t ON t.title = 'Bob''s First Thread' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Tips'
WHERE u.username = 'bob'
ON CONFLICT DO NOTHING;

INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Another tip from Bob.', 3, st.id
FROM users u
JOIN threads t ON t.title = 'Bob''s First Thread' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Tips'
WHERE u.username = 'bob'
ON CONFLICT DO NOTHING;

INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Carol shows her car.', 5, st.id
FROM users u
JOIN threads t ON t.title = 'Carol''s Thread' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Showcase'
WHERE u.username = 'carol'
ON CONFLICT DO NOTHING;

INSERT INTO messages (user_id, body, upvote_count, subthread_id)
SELECT CAST(u.id AS VARCHAR), 'Admin says: Welcome!', 10, st.id
FROM users u
JOIN threads t ON t.title = 'Admin Thread' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Admin Announcements'
WHERE u.username = 'dave'
ON CONFLICT DO NOTHING;

-- Tags: threads
INSERT INTO thread_tags (thread_id, tag)
SELECT t.id, 'welcome'
FROM threads t WHERE t.title IN ('Alice''s Thread','Admin Thread')
ON CONFLICT DO NOTHING;

INSERT INTO thread_tags (thread_id, tag)
SELECT t.id, 'tips'
FROM threads t WHERE t.title = 'Bob''s First Thread'
ON CONFLICT DO NOTHING;

INSERT INTO thread_tags (thread_id, tag)
SELECT t.id, 'showcase'
FROM threads t WHERE t.title = 'Carol''s Thread'
ON CONFLICT DO NOTHING;

INSERT INTO thread_tags (thread_id, tag)
SELECT t.id, 'admin'
FROM threads t WHERE t.title = 'Admin Thread'
ON CONFLICT DO NOTHING;

-- Tags: subthreads
INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'guidelines'
FROM users u
JOIN threads t ON t.title = 'Welcome to the Forum' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Forum Rules and Guidelines'
WHERE u.username = 'admin_user'
ON CONFLICT DO NOTHING;

INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'general'
FROM subthreads st WHERE st.title = 'General Discussion'
ON CONFLICT DO NOTHING;

INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'tips'
FROM subthreads st WHERE st.title = 'Tips'
ON CONFLICT DO NOTHING;

INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'questions'
FROM subthreads st WHERE st.title = 'Questions'
ON CONFLICT DO NOTHING;

INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'showcase'
FROM subthreads st WHERE st.title = 'Showcase'
ON CONFLICT DO NOTHING;

INSERT INTO subthread_tags (subthread_id, tag)
SELECT st.id, 'announcement'
FROM subthreads st WHERE st.title = 'Admin Announcements'
ON CONFLICT DO NOTHING;

-- Tags: messages
INSERT INTO message_tags (message_id, tag)
SELECT m.id, 'intro'
FROM users u
JOIN threads  t  ON t.title = 'Welcome to the Forum' AND t.user_id = CAST(u.id AS VARCHAR)
JOIN subthreads st ON st.thread_id = t.id AND st.title = 'Forum Rules and Guidelines'
JOIN messages m ON m.subthread_id = st.id
WHERE u.username = 'admin_user'
  AND m.body = 'Welcome everyone! Please read the forum rules before posting.'
ON CONFLICT DO NOTHING;

INSERT INTO message_tags (message_id, tag)
SELECT m.id, 'greeting'
FROM messages m WHERE m.body LIKE '%Welcome%'
ON CONFLICT DO NOTHING;

INSERT INTO message_tags (message_id, tag)
SELECT m.id, 'tip'
FROM messages m WHERE m.body LIKE '%tip%'
ON CONFLICT DO NOTHING;

INSERT INTO message_tags (message_id, tag)
SELECT m.id, 'show'
FROM messages m WHERE m.body LIKE '%shows her car%'
ON CONFLICT DO NOTHING;

DELETE FROM threads WHERE type = 'ADMIN';
