-- Insert dummy data for testing the forum application
-- This ensures there are users available to create threads and messages

-- Insert dummy users (role: 0=ADMIN, 1=REGULAR)
INSERT INTO users (username, message, model_id, role, created_at) VALUES 
('admin_user', 'Welcome to the forum! I am the administrator.', 'model_admin_001', 0, NOW()),
('john_doe', 'Hi everyone! Happy to be part of this community.', 'model_user_002', 1, NOW()),
('jane_smith', 'Looking forward to great discussions!', 'model_user_003', 1, NOW()),
('car_enthusiast', 'Love talking about vehicles and technology.', 'model_user_004', 1, NOW()),
('tech_guru', 'Here to help with technical questions.', 'model_user_005', 1, NOW()),
('developer_mike', 'Passionate about coding and software development.', 'model_user_006', 1, NOW()),
('auto_expert', 'Professional mechanic with 15 years of experience.', 'model_user_007', 1, NOW()),
('forum_moderator', 'Here to keep discussions friendly and helpful.', 'model_mod_001', 0, NOW());

-- Insert dummy threads (role: COMMERCIAL or PERSONAL)
INSERT INTO threads (user_id, role, model_id, title, created_at) VALUES 
('admin_user', 'COMMERCIAL', 'model_thread_001', 'Welcome to the Forum', NOW()),
('john_doe', 'PERSONAL', 'model_thread_002', 'Best Practices for Vehicle Maintenance', NOW()),
('jane_smith', 'COMMERCIAL', 'model_thread_003', 'New Technology Trends in Automotive', NOW()),
('car_enthusiast', 'PERSONAL', 'model_thread_004', 'Share Your Car Photos', NOW()),
('tech_guru', 'PERSONAL', 'model_thread_005', 'Programming Tips and Tricks', NOW());

-- Insert dummy subthreads
INSERT INTO subthreads (user_id, title, created_at, thread_id) VALUES 
('admin_user', 'Forum Rules and Guidelines', NOW(), 1),
('john_doe', 'Regular Maintenance Tips', NOW(), 2),
('jane_smith', 'Electric Vehicle Updates', NOW(), 3),
('car_enthusiast', 'Classic Cars Showcase', NOW(), 4),
('tech_guru', 'Technical Support', NOW(), 1),
('developer_mike', 'Code Review Guidelines', NOW(), 5);

-- Insert dummy messages
INSERT INTO messages (user_id, body, created_at, upvote_count, deleted, subthread_id) VALUES 
('admin_user', 'Welcome everyone! Please read the forum rules before posting.', NOW(), 5, false, 1),
('john_doe', 'Remember to change your oil every 5000 miles for optimal performance.', NOW(), 3, false, 2),
('jane_smith', 'The new EV models are really impressive this year. What do you think?', NOW(), 7, false, 3),
('car_enthusiast', 'Here is my 1967 Mustang restoration project. What do you think?', NOW(), 12, false, 4),
('tech_guru', 'If you have any technical issues, feel free to ask here.', NOW(), 2, false, 5),
('developer_mike', 'Always remember to follow clean code principles!', NOW(), 8, false, 6),
('auto_expert', 'For brake maintenance, check your pads every 12,000 miles.', NOW(), 4, false, 2);
