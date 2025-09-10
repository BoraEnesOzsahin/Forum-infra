-- Insert dummy data for testing the forum application
-- This ensures there are users available to create threads and messages

-- The 'created_at' column is intentionally omitted from these INSERT statements.
-- The database schema defines 'DEFAULT CURRENT_TIMESTAMP' for this column,
-- so the database will automatically populate it with the current time upon insertion.

-- Insert dummy users
INSERT INTO users (username, message, model_id, role) VALUES 
('admin_user', 'Welcome to the forum! I am the administrator.', 'model_admin_001', 'ADMIN'),
('john_doe', 'Hi everyone! Happy to be part of this community.', 'model_user_002', 'CITIZEN'),
('jane_smith', 'Looking forward to great discussions!', 'model_user_003', 'CITIZEN'),
('car_enthusiast', 'Love talking about vehicles and technology.', 'model_user_004', 'CITIZEN'),
('tech_guru', 'Here to help with technical questions.', 'model_user_005', 'CITIZEN'),
('developer_mike', 'Passionate about coding and software development.', 'model_user_006', 'CITIZEN'),
('auto_expert', 'Professional mechanic with 15 years of experience.', 'model_user_007', 'CITIZEN'),
('forum_moderator', 'Here to keep discussions friendly and helpful.', 'model_mod_001', 'ADMIN');

-- Insert dummy threads (user_id now refers to the auto-generated ID from the users table)
INSERT INTO threads (user_id, type, model_id, title) VALUES 
(1, 'PERSONAL', 'model_thread_001', 'Welcome to the Forum'),
(2, 'COMMERCIAL', 'model_thread_002', 'Best Practices for Vehicle Maintenance'),
(3, 'PERSONAL', 'model_thread_003', 'New Technology Trends in Automotive'),
(4, 'PERSONAL', 'model_thread_004', 'Share Your Car Photos'),
(5, 'COMMERCIAL', 'model_thread_005', 'Programming Tips and Tricks');

-- Insert dummy subthreads
INSERT INTO subthreads (user_id, title, thread_id) VALUES 
(1, 'Forum Rules and Guidelines', 1),
(2, 'Regular Maintenance Tips', 2),
(3, 'Electric Vehicle Updates', 3),
(4, 'Classic Cars Showcase', 4),
(5, 'Technical Support', 1),
(6, 'Code Review Guidelines', 5);

-- Insert dummy messages
INSERT INTO messages (user_id, body, upvote_count, subthread_id) VALUES 
(1, 'Welcome everyone! Please read the forum rules before posting.', 5, 1),
(2, 'Remember to change your oil every 5000 miles for optimal performance.', 3, 2),
(3, 'The new EV models are really impressive this year. What do you think?', 7, 3),
(4, 'Here is my 1967 Mustang restoration project. What do you think?', 12, 4),
(5, 'If you have any technical issues, feel free to ask here.', 2, 5),
(6, 'Always remember to follow clean code principles!', 8, 6),
(7, 'For brake maintenance, check your pads every 12,000 miles.', 4, 2);
