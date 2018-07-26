--@formatter:off
-- User
INSERT INTO "user" (id, first_name, last_name, email, role, last_login) VALUES
  -- Utwente users
  ('AA123456-0000-0000-0000-000000000001', 'Lukas', 'Tester', NULL, 'STUDENT', now()),
  ('AA123456-0000-0000-0000-000000000002', 'Jelle', 'Tester', 'jelle@example.com', 'TEACHER', NULL),
  ('AA123456-0000-0000-0000-000000000003', 'Melcher', 'Tester', 'net@example.com', 'TEACHER', NULL),
  ('AA123456-0000-0000-0000-000000000004', 'Alex', 'Tester', NULL, 'TEACHER', now()),

  ('DE301000-0000-0000-0000-000000000100', 'Demo-1257', NULL, 'demo1@example.com', 'STUDENT', now()),
  ('DE301000-0000-0000-0000-000000000101', 'Demo-1258', 'Peterson', 'demo2@example.com', 'STUDENT', now()),
  ('DE301000-0000-0000-0000-000000000102', 'Remco', 'Tester', 'demo3@example.com', 'STUDENT', now()),
  ('DE301000-0000-0000-0000-000000000103', 'Ömer', 'Şakar', 'demo4@example.com', 'TEACHER', now()),
  ('DE301000-0000-0000-0000-000000000104', 'Демо-1261', 'Ларкингсон', 'demo5@example.com', 'TEACHER', now()),

  -- Internal users
  ('DE301000-0000-0000-0000-000000000110', 'Julius', 'Passwordman', 'lukas@example.com', 'STUDENT', now()),
  ('DE301000-0000-0000-0000-000000000111', 'Teacherius', 'Externicus', 'user2@sketchlab.utwente.nl', 'TEACHER', now()),
  ('DE301000-0000-0000-0000-000000000112', 'xXx00Z0Z00xXx', NULL, 'user3@sketchlab.utwente.nl', 'STUDENT', now()),
  ('DE301000-0000-0000-0000-000000000113', 'asdf', NULL, 'user4@sketchlab.utwente.nl', 'STUDENT', now()),
  ('01da4492-03ed-4fde-8849-00504c369492', 'PersonA', 'Alpha', 'personA_alpha@amail.com', 'STUDENT', now()),
  ('422667ef-c628-493d-997a-018190327ff8', 'PersonB', 'Beta', 'personB_beta@bmail.com', 'STUDENT', now()),
  ('b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc', 'PersonC', 'Charlie', 'personC_charlie@cmail.com', 'STUDENT', now()),
  ('bfe2fa9f-ba88-4143-8410-ec70e934a02f', 'PersonD', 'Delta', 'personD_delta@dmail.com', 'STUDENT', now());

INSERT INTO internal_user (user_id, password_hashed, active, suspended) VALUES
  -- all passwords are 'password'
  ('DE301000-0000-0000-0000-000000000110', '$2a$04$TYJjYTqJXO7b4VCr3rEuk.6GqtQsjkyvgv06O6aCNIGsork..nfV6', TRUE, FALSE),
  ('DE301000-0000-0000-0000-000000000111', '$2a$04$T1Ng.BRK9Z/iV.B27DMQUeBqsi7q34FAd01ubh2W8G3yNru146H4G', TRUE, FALSE),
  ('DE301000-0000-0000-0000-000000000112', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, FALSE),
  ('DE301000-0000-0000-0000-000000000113', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, TRUE),
  ('01da4492-03ed-4fde-8849-00504c369492', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, FALSE),
  ('422667ef-c628-493d-997a-018190327ff8', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, FALSE),
  ('b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, FALSE),
  ('bfe2fa9f-ba88-4143-8410-ec70e934a02f', '$2a$04$yhWVy8Fy.IEdikWjJCGpoe9EqhLkSh.GaT4BmmFY6dSH0.NpaTzPa', TRUE, FALSE);

INSERT INTO utwente_user (utwente_id, user_id, active, created, modified) VALUES
  ('s1600885', 'AA123456-0000-0000-0000-000000000001', TRUE, now(), now()),
  ('s1371347', 'AA123456-0000-0000-0000-000000000002', TRUE, now(), now()),
  ('s1560670', 'AA123456-0000-0000-0000-000000000003', TRUE, now(), now()),
  ('s1824945', 'AA123456-0000-0000-0000-000000000004', TRUE, now(), now()),

  ('x1001257', 'DE301000-0000-0000-0000-000000000100', TRUE, now(), now()),
  ('x1001258', 'DE301000-0000-0000-0000-000000000101', TRUE, now(), now()),
  ('x1001259', 'DE301000-0000-0000-0000-000000000102', TRUE, now(), now()),
  ('x1001260', 'DE301000-0000-0000-0000-000000000103', TRUE, now(), now()),
  ('x1001261', 'DE301000-0000-0000-0000-000000000104', TRUE, now(), now());

INSERT INTO user_avatar (user_id, image) VALUES
  ('DE301000-0000-0000-0000-000000000100', file_read('demo_images/avatars/4.jpg')),
  ('DE301000-0000-0000-0000-000000000101', file_read('demo_images/avatars/5.jpg')),
  ('DE301000-0000-0000-0000-000000000102', file_read('demo_images/avatars/6.jpg')),
  ('DE301000-0000-0000-0000-000000000103', file_read('demo_images/avatars/7.jpg')),
  ('DE301000-0000-0000-0000-000000000104', file_read('demo_images/avatars/8.jpg'));

-- Chapters and tasks
INSERT INTO chapter (id, label) VALUES
  (1, 'Desk to Design'),
  (2, 'Design to Delivery'),
  (3, 'Delivery to Destruction'),
  (4, 'Destruction to Decomposition'),
  (5, 'wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww'),
  (6, 'Hi, no chapter groups, but tasks present!');

INSERT INTO task (id, name, track, chapter_id, author_id, slot) VALUES
  -- Desk to Design
  (1, 'First task!', 'BASICS', 1, 'DE301000-0000-0000-0000-000000000104', 0),
  (2, 'Second task!', 'BASICS', 1, 'DE301000-0000-0000-0000-000000000104', 4),
  (3, 'Making clay models', 'BASICS', 1, 'DE301000-0000-0000-0000-000000000104', 5),
  (4, 'Premium clay models', 'BASICS', 1, 'DE301000-0000-0000-0000-000000000104', 6),

  (5, 'THIRD task!', 'FORM', 1, 'DE301000-0000-0000-0000-000000000103', 0),
  (6, 'Fourth task!', 'FORM', 1, 'DE301000-0000-0000-0000-000000000103', 3),

  (7, 'Perfect clay models', 'IDEATION', 1, 'DE301000-0000-0000-0000-000000000104', 1),
  (8, 'What is clay even?', 'IDEATION', 1, 'DE301000-0000-0000-0000-000000000104', 2),

  (9, 'Fifth task!', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 0),
  (10, 'Sixth task!', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 1),
  (11, 'Seventh task!', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 2),
  (12, 'Eight task!', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 3),
  (13, 'I lost count', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 4),
  (14, 'Making dirt models', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 5),
  (15, 'More dirt models', 'COMMUNICATION', 1, 'DE301000-0000-0000-0000-000000000104', 6),

  -- Design to Delivery
  (16, 'First task!', 'BASICS', 2, 'DE301000-0000-0000-0000-000000000104', 0),
  (17, 'Second task!', 'FORM', 2, 'DE301000-0000-0000-0000-000000000104', 1),
  (18, 'Making clay models', 'IDEATION', 2, 'DE301000-0000-0000-0000-000000000104', 1),
  (19, 'Premium clay models', 'COMMUNICATION', 2, 'DE301000-0000-0000-0000-000000000104', 1),
  (20, 'Some other task!', 'BASICS', 2, 'DE301000-0000-0000-0000-000000000104', 1),

  -- Hi, no chapter groups here, tasks present!
  (21, 'Task 1', 'BASICS', 6, 'AA123456-0000-0000-0000-000000000002', 0),
  (22, 'Task 2', 'FORM', 6, 'AA123456-0000-0000-0000-000000000002', 1),
  (23, 'Task 3', 'IDEATION', 6, 'AA123456-0000-0000-0000-000000000002', 2),
  (24, 'Task 4', 'COMMUNICATION', 6, 'AA123456-0000-0000-0000-000000000002', 3);

-- Chapter groups
INSERT INTO chapter_group (id, chapter_id, name, started_at) VALUES
  (1, 1, 'Desk to Design 2016', DATE '2016-09-1'),
  (2, 1, 'Desk to Design 2017', DATE '2017-09-4'),
  (3, 2, 'Design to Delivery 2016', DATE '2016-11-15'),
  (4, 2, 'Design to Delivery 2017', DATE '2017-11-20'),
  (5, 3, 'Orci varius', DATE '2018-01-10'),
  (6, 3, 'Lorem ipsum', DATE '2018-02-10'),
  (7, 4, 'Fusce a est tellus', DATE '2018-03-1'),
  (8, 5, 'Aenean', DATE '2018-04-1'),
  (9, 2, 'Etiam', DATE '2018-05-1');

-- Enrollments
INSERT INTO enrollment (chapter_group_id, user_id, assistant, grade, graded_at, grade_message)
VALUES
  (1, '00000000-0000-0000-0000-000000000000', FALSE, 0.0, now(), 'Splendid!'),
  (1, '01da4492-03ed-4fde-8849-00504c369492', FALSE, 6.7, now(), 'Mediocre!'),
  (1, '422667ef-c628-493d-997a-018190327ff8', TRUE, NULL, NULL, NULL),
  (1, 'b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc', TRUE, NULL, NULL, NULL),
  (1, 'bfe2fa9f-ba88-4143-8410-ec70e934a02f', TRUE, NULL, NULL, NULL),
  (1, 'DE301000-0000-0000-0000-000000000100', FALSE, 4.4, now(), 'AMD heeft zijn jaarlijkse grote driverupdate voor videokaarten uitgebracht. De nieuwe Radeon-software heet ''Adrenalin Edition'' en bevat onder andere een nieuwe overlay en biedt ondersteuning voor AMD''s mobiele Link-app. AMD brengt ieder jaar een grote update voor zijn Radeon Software uit, die functionaliteit toevoegt. De naam wijzigt ook ieder jaar en verwijst naar iets roods en dit jaar is dat de Adrenalin-roos. De belangrijkste nieuwe toevoegingen zijn de Radeon Overlay en de AMD Link-app. De overlay is in de nieuwe driver met de toetsencombinatie alt+r op te roepen en geeft de mogelijkheid om een groot aantal driverfuncties via een in-game overlay te bedienen. Zo kunnen gebruikers bijvoorbeeld FreeSync, Radeon Chill en ReLive in- en uitschakelen, maar ook kleurinstellingen aanpassen en zaken als kloksnelheid, framerate en temperatuur monitoren.'),
  (1, 'DE301000-0000-0000-0000-000000000101', FALSE, 5.3, now(), 'Some message here'),
  (1, 'DE301000-0000-0000-0000-000000000102', FALSE, NULL, NULL, NULL),
  (1, 'DE301000-0000-0000-0000-000000000110', FALSE, NULL, NULL, NULL),
  (1, 'DE301000-0000-0000-0000-000000000112', TRUE, NULL, NULL, NULL),

  (2, '01da4492-03ed-4fde-8849-00504c369492', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, '422667ef-c628-493d-997a-018190327ff8', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, 'b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, 'bfe2fa9f-ba88-4143-8410-ec70e934a02f', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, 'DE301000-0000-0000-0000-000000000100', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, 'DE301000-0000-0000-0000-000000000101', FALSE, 0.0, now(), 'Everybody was Kung Fu fighting'),
  (2, 'DE301000-0000-0000-0000-000000000102', TRUE, NULL, NULL, NULL),
  (2, 'DE301000-0000-0000-0000-000000000110', TRUE, 5.7, now(), 'I gotz dem grade but man am TA fam.'),

  (3, '01da4492-03ed-4fde-8849-00504c369492', FALSE, 10.0, DATE '1973-02-01', 'Blitzkrieg Bop'),
  (3, '422667ef-c628-493d-997a-018190327ff8', FALSE, 10.0, DATE '1973-02-01', 'Blitzkrieg Bop'),
  (3, 'b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc', FALSE, 10.0, DATE '1973-02-01', 'Blitzkrieg Bop'),
  (3, 'bfe2fa9f-ba88-4143-8410-ec70e934a02f', FALSE, 10.0, DATE '1973-02-01', 'Blitzkrieg Bop');

INSERT INTO chapter_subgroup (id, chapter_group_id, name) VALUES
  (1, 1, 'Longhorn fans'),
  (2, 1, 'Bottom Line'),
  (3, 1, 'Gold Miners'),
  (4, 2, 'Quality Control'),
  (5, 2, 'Team ABC (Always Be Closing)'),
  (6, 2, 'Tech Pirates'),
  (7, 2, 'The Think Tank'),
  (8, 3, 'Mind Benders'),
  (9, 3, 'Captivators'),
  (10, 4, 'Business as Usual'),
  (11, 5, 'Blaze Warriors');

INSERT INTO subgroup_enrollment (chapter_subgroup_id, user_id) VALUES
  (1, 'b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc'),
  (1, '01da4492-03ed-4fde-8849-00504c369492'),
  (1, 'DE301000-0000-0000-0000-000000000100'),
  (1, 'DE301000-0000-0000-0000-000000000101'),
  (1, 'DE301000-0000-0000-0000-000000000110'),
  (3, '01da4492-03ed-4fde-8849-00504c369492'),
  (3, '422667ef-c628-493d-997a-018190327ff8'),
  (3, 'b2eeb447-ef29-4d70-b16c-fd7e5b11c2fc'),
  (3, 'bfe2fa9f-ba88-4143-8410-ec70e934a02f'),
  (8, 'DE301000-0000-0000-0000-000000000110'),
  (8, 'DE301000-0000-0000-0000-000000000112');

-- Task pages
INSERT INTO task_page (id, title, text, task_id, author_id, video_url, slot) VALUES
  --First task!
  (1, 'Start with...', 'First we start with alpha', 1, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 1),
  (2, 'Then do...', 'Secondly, we do beta....', 1, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 2),
  (3, 'Followed by...', 'Thirdly we draw gamma', 1, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 3),
  (4, 'End with...', 'And end with kappa', 1, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 4),
  --Second task!
  (5, 'Some first thingy', 'Lorum', 2, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 1),
  (6, 'Some other thingy', 'Ipsum', 2, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 2),
  (7, 'Even more thingies', 'Dolor', 2, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 3),
  (8, 'And a last thing', 'Sit Amet', 2, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 4),
  --THIRD task!
  (9, 'Only one assignment here', 'And it''s awesome!', 5, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 1),
  --Design to delivery
  (10, 'Task page in other task ', '', 16, 'DE301000-0000-0000-0000-000000000104', 'https://player.vimeo.com/video/242021561', 1);

INSERT INTO task_page_image (id, task_page_id, mime_type, data) VALUES
  (1, 1, 'image/jpeg', file_read('demo_images/submissions/cube.jpg')),
  (2, 2, 'image/jpeg', file_read('demo_images/submissions/horse.jpg')),
  (3, 2, 'image/jpeg', file_read('demo_images/submissions/horse2.jpg')),
  (4, 2, 'image/jpeg', file_read('demo_images/submissions/horse3.jpg')),
  (5, 2, 'image/jpeg', file_read('demo_images/submissions/horse4.jpg')),
  (6, 2, 'image/png', file_read('demo_images/submissions/horse5.png'));

-- Example submissions
INSERT INTO example_submission (id, task_id, user_id, comment) VALUES
  (1, 1, 'DE301000-0000-0000-0000-000000000103', 'If you are new to drawing, I would like you to try something like this'),
  (2, 1, 'DE301000-0000-0000-0000-000000000103', 'More experienced drawers can try to work on shadows like so'),
  (3, 1, 'DE301000-0000-0000-0000-000000000103', 'A student last year handed this in. Absolutely stunning, my favorite so far.');

INSERT INTO example_submission_file (example_submission_id, mime_type, data) VALUES
  (1, 'image/png', file_read('demo_images/example_submissions/horse_outline.png')),
  (2, 'image/jpeg', file_read('demo_images/example_submissions/horse_proper.jpg')),
  (3, 'image/jpeg', file_read('demo_images/example_submissions/horse_2.jpg'));

INSERT INTO example_submission_thumbnail (example_submission_id, data) VALUES
  (1, file_read('demo_images/example_submissions/horse_outline_thumbnail.jpg')),
  (2, file_read('demo_images/example_submissions/horse_proper_thumbnail.jpg')),
  (3, file_read('demo_images/example_submissions/horse_2_thumbnail.jpg'));

-- Submissions w/ files and votes from their authors
INSERT INTO submission (id, task_id, chapter_group_id, user_id, best_work) VALUES
  -- task 1
  (1, 1, 1, 'DE301000-0000-0000-0000-000000000100', TRUE),
  (2, 1, 1, 'DE301000-0000-0000-0000-000000000100', FALSE),
  (3, 1, 1, 'DE301000-0000-0000-0000-000000000100', FALSE),
  (4, 1, 1, 'DE301000-0000-0000-0000-000000000101', TRUE),
  (5, 1, 1, 'DE301000-0000-0000-0000-000000000102', TRUE),

  -- task 2
  (6, 2, 1, 'DE301000-0000-0000-0000-000000000101', FALSE),
  (7, 2, 1, 'DE301000-0000-0000-0000-000000000102', FALSE),
  (8, 2, 1, '422667ef-c628-493d-997a-018190327ff8', TRUE);

INSERT INTO submission_file (submission_id, mime_type, data) VALUES
  (1, 'image/jpeg', file_read('demo_images/submissions/horse.jpg')),
  (2, 'image/jpeg', file_read('demo_images/submissions/horse2.jpg')),
  (3, 'image/jpeg', file_read('demo_images/submissions/horse3.jpg')),
  (4, 'image/jpeg', file_read('demo_images/submissions/horse4.jpg')),
  (5, 'image/jpeg', file_read('demo_images/submissions/horse.jpg')),

  (6, 'image/png', file_read('demo_images/submissions/horse5.png')),
  (7, 'image/jpeg', file_read('demo_images/submissions/horse3.jpg')),
  (8, 'image/png', file_read('demo_images/submissions/horse5.png'));

INSERT INTO submission_thumbnail (submission_id, data) VALUES
  (1, file_read('demo_images/submissions/horse-thumbnail.jpg')),
  (2, file_read('demo_images/submissions/horse2-thumbnail.jpg')),
  (3, file_read('demo_images/submissions/horse3-thumbnail.jpg')),
  (4, file_read('demo_images/submissions/horse4-thumbnail.jpg')),
  (5, file_read('demo_images/submissions/horse-thumbnail.jpg')),

  (6, file_read('demo_images/submissions/horse5-thumbnail.jpg')),
  (7, file_read('demo_images/submissions/horse3-thumbnail.jpg')),
  (8, file_read('demo_images/submissions/horse5-thumbnail.jpg'));

INSERT INTO vote (submission_id, user_id) VALUES
  (1, 'DE301000-0000-0000-0000-000000000100'),
  (1, 'DE301000-0000-0000-0000-000000000101'),
  (1, 'DE301000-0000-0000-0000-000000000102'),
  (2, 'DE301000-0000-0000-0000-000000000100'),
  (3, 'DE301000-0000-0000-0000-000000000100'),
  (4, 'DE301000-0000-0000-0000-000000000101'),
  (5, 'DE301000-0000-0000-0000-000000000102'),

  (6, 'DE301000-0000-0000-0000-000000000101'),
  (6, 'DE301000-0000-0000-0000-000000000102'),
  (7, 'DE301000-0000-0000-0000-000000000102');

INSERT INTO annotation (id, submission_id, user_id, drawing, comment, soft_deleted) VALUES
  (1, 1, 'DE301000-0000-0000-0000-000000000102', file_read('demo_images/annotations/annotation1.json'), NULL, FALSE),
  (2, 1, 'DE301000-0000-0000-0000-000000000101', file_read('demo_images/annotations/annotation2.json'), 'Is cool!', FALSE),
  (3, 2, 'DE301000-0000-0000-0000-000000000102', file_read('demo_images/annotations/annotation3.json'), '',FALSE),
  (4, 2, 'DE301000-0000-0000-0000-000000000103', file_read('demo_images/annotations/annotation4.json'), 'This is really inappropriate!',TRUE);

-- Questions and related
INSERT INTO question (id, task_id, user_id, chapter_group_id, text) VALUES
  (1, 1, 'DE301000-0000-0000-0000-000000000100', 1, 'I have a question about this; when I click submit it will not let me choose my video file. i think i need a video but do i need the fideo? It doesnt work when I click all files and upload it anywyas it is broken please help me. thank you ~me'),
  (2, 2, 'DE301000-0000-0000-0000-000000000101', 1, 'How come Demo-1257 get best work and not me?'),
  (3, 3, 'DE301000-0000-0000-0000-000000000102', 1, 'Very unclear task'),

  (4, 1, 'DE301000-0000-0000-0000-000000000100', 2, 'I have a question about this'),
  (5, 2, 'DE301000-0000-0000-0000-000000000101', 2, 'PLZ GIB SOLUTION');

INSERT INTO answer (id, question_id, user_id, text) VALUES
  (1, 1, 'AA123456-0000-0000-0000-000000000003', 'Ok first of all: no, you should not upload a video. Take a picture of your sketch and put that online. There are some apps that you can use to get the picture to your computer if that doesnt work.'),
  (2, 1, 'AA123456-0000-0000-0000-000000000002', 'Yes, that is correct, find more info by watching the intro video. I see your project partner has already uploaded something so its fine for this assignment. Ask a TA next time if you still cant get it working'),
  (3, 2, 'AA123456-0000-0000-0000-000000000003', 'Nah, you should grasp that by now'),
  (4, 4, 'DE301000-0000-0000-0000-000000000100', 'Please respond you lazy turds');

-- Notifications
INSERT INTO notification (id, user_id, date, event_type, submission_id, submission_annotation_id, question_id, question_answer_id, chapter_group_id, task_id)
VALUES
  -- for student DE301000-0000-0000-0000-000000000100
  (1, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-1', 'SUBMISSION_ANNOTATION', 1, 1, NULL, NULL, NULL, NULL),
  (2, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-12', 'SUBMISSION_ANNOTATION', 1, 2, NULL, NULL, NULL, NULL),
  (3, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-13', 'SUBMISSION_BEST_WORK', 1, NULL, NULL, NULL, NULL, NULL),

  (4, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-4', 'SUBMISSION_ANNOTATION', 2, 3, NULL, NULL, NULL, NULL),
  (5, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-5', 'SUBMISSION_ANNOTATION', 2, 4, NULL, NULL, NULL, NULL),

  (6, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-2', 'CHAPTER_GROUP_ENROLL', NULL, NULL, NULL, NULL, 1, NULL),
  (7, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-17', 'CHAPTER_GROUP_GRADE', NULL, NULL, NULL, NULL, 1, NULL),

  (8, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-8', 'QUESTION_ANSWER', NULL, NULL, 1, 1, NULL, NULL),
  (9, 'DE301000-0000-0000-0000-000000000100', DATE '2017-09-8', 'TASK_CREATION', NULL, NULL, NULL, NULL, 1, 1),

  -- for teacher DE301000-0000-0000-0000-000000000103
  (10, 'DE301000-0000-0000-0000-000000000103', DATE '2017-09-1', 'TASK_QUESTION', NULL, NULL, 1, NULL, NULL, NULL),
  (11, 'DE301000-0000-0000-0000-000000000103', DATE '2017-09-2', 'TASK_QUESTION', NULL, NULL, 2, NULL, NULL, NULL),
  (12, 'DE301000-0000-0000-0000-000000000103', DATE '2017-09-3', 'TASK_QUESTION', NULL, NULL, 3, NULL, NULL, NULL),
  (13, 'DE301000-0000-0000-0000-000000000103', DATE '2017-09-4', 'TASK_QUESTION', NULL, NULL, 4, NULL, NULL, NULL),
  (14, 'DE301000-0000-0000-0000-000000000103', DATE '2017-09-5', 'TASK_QUESTION', NULL, NULL, 5, NULL, NULL, NULL);
--@formatter:on
