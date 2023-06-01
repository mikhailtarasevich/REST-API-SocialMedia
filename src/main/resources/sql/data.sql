INSERT INTO users (name, email, password)
VALUES ('John Smith', 'john.smith@example.com', '$2a$10$9LBpAplZfA8SLaD1iLKtcedvzpPWdRD/aIKzCqlWZcZhL8hZ8KgQK'),
       ('Emily Johnson', 'emily.johnson@example.com', '$2a$10$9LBpAplZfA8SLaD1iLKtcedvzpPWdRD/aIKzCqlWZcZhL8hZ8KgQK'),
       ('Michael Brown', 'michael.brown@example.com', '$2a$10$9LBpAplZfA8SLaD1iLKtcedvzpPWdRD/aIKzCqlWZcZhL8hZ8KgQK'),
       ('Emma Davis', 'emma.davis@example.com', '$2a$10$9LBpAplZfA8SLaD1iLKtcedvzpPWdRD/aIKzCqlWZcZhL8hZ8KgQK'),
       ('Christopher Miller', 'christopher.miller@example.com', '$2a$10$9LBpAplZfA8SLaD1iLKtcedvzpPWdRD/aIKzCqlWZcZhL8hZ8KgQK');

INSERT INTO subscribers (user_id, sub_user_id, is_accepted)
VALUES (1, 2, 'ACCEPTED'),
       (1, 3, 'ACCEPTED'),
       (1, 5, 'REJECTED'),
       (1, 4, 'PENDING'),
       (2, 1, 'ACCEPTED'),
       (2, 3, 'REJECTED'),
       (2, 5, 'ACCEPTED'),
       (3, 1, 'ACCEPTED'),
       (3, 4, 'ACCEPTED'),
       (4, 3, 'ACCEPTED'),
       (4, 5, 'REJECTED'),
       (5, 2, 'ACCEPTED');

INSERT INTO posts (user_id, header, content, created_at)
VALUES (1, 'First post', 'This is my first post.', '2023-05-26 09:45:00.184705'),
       (1, 'Hello, world!', 'Just saying hello to everyone.', '2023-05-26 09:49:54.772022'),
       (2, 'Check out this photo', 'I took this photo yesterday.', '2023-05-26 09:50:54.772022'),
       (3, 'Thoughts on the new movie', 'I watched the new movie and here are my thoughts...', '2023-05-29 10:49:54.7'),
       (4, 'Funny meme', 'This meme made me laugh so hard!', '2023-05-22 22:22:22.22'),
       (5, 'Travel memories', 'Throwback to my amazing trip last year.', '2023-05-22 23:22:22.22');

INSERT INTO messages (from_user_id, to_user_id, message, created_at)
VALUES (1, 2, 'Hey, how are you?', '2023-05-26 09:45:00.184705'),
       (2, 1, 'I''m good, thanks! How about you?', '2023-05-26 09:50:00.184705'),
       (1, 3, 'Do you want to hang out this weekend?', '2023-05-25 09:45:00.184705'),
       (3, 1, 'Sure, let''s meet on Saturday.', '2023-05-26 09:40:00.0'),
       (2, 4, 'Did you watch the new episode?', '2023-05-26 22:45:00.0'),
       (4, 2, 'Yes, it was amazing!', '2023-05-26 23:22:00.0'),
       (4, 5, 'Happy birthday! Have a great day!', '2023-05-26 10:10:00.10'),
       (5, 4, 'Thank you so much!', '2023-05-26 10:11:00.11');