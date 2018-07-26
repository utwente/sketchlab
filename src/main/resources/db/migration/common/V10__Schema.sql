-- Schema
-- Note that whatever is put here has to be both valid for H2 and PostgreSQL
-- H2 runs in PostgreSQL compatibility mode

----------------------------------
-- User-related tables
----------------------------------
CREATE TABLE "user" (
  id UUID PRIMARY KEY, -- id '00000000000000000000000000000000' is special for ANONYMOUS, the rest is random
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) DEFAULT NULL,
  email VARCHAR(200) DEFAULT NULL UNIQUE,
  role VARCHAR(255) NOT NULL,
  last_login TIMESTAMP DEFAULT NULL,
  CHECK ((role IN ('STUDENT', 'TEACHER') AND id != '00000000000000000000000000000000')
         OR (role = 'ANONYMOUS' AND id = '00000000000000000000000000000000'))
);

CREATE TABLE user_avatar (
  user_id UUID PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE,
  image BYTEA NOT NULL
  -- mime type is always image/jpeg
);

-- The anonymous user is an actual user in the system, holding the elusive 'ANONYMOUS' role.
INSERT INTO "user"(id, first_name, role)
VALUES ('00000000000000000000000000000000', 'Anonymous', 'ANONYMOUS');

CREATE TABLE internal_user (
  user_id UUID PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE ,
  password_hashed VARCHAR(250) NOT NULL, -- the hash, the algorithm used and the salt are in a single string
  active BOOLEAN DEFAULT FALSE, -- Whether the user has been activated through the activation mail
  suspended BOOLEAN DEFAULT FALSE -- Whether a teacher has disabled this account.
);

CREATE TABLE internal_user_activation_token (
  user_id UUID PRIMARY KEY REFERENCES "user"(id) ON DELETE CASCADE ,
  token VARCHAR(100) NOT NULL
);

CREATE TABLE utwente_user ( -- special join table for utwente users
  utwente_id VARCHAR(10) PRIMARY KEY NOT NULL, -- s/m/x number
  user_id UUID UNIQUE NOT NULL REFERENCES "user"(id),
  active BOOLEAN NOT NULL,
  created TIMESTAMP NOT NULL,
  modified TIMESTAMP NOT NULL
);

----------------------------------
-- Chapter-related tables
----------------------------------
CREATE TABLE chapter (
  id SERIAL PRIMARY KEY,
  label VARCHAR(100) NOT NULL
);

CREATE TABLE task (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  track VARCHAR(13) NOT NULL,
  chapter_id INTEGER NOT NULL REFERENCES chapter(id) ON DELETE CASCADE,
  author_id UUID NOT NULL REFERENCES "user"(id),
  slot INTEGER NOT NULL,
  CHECK (track IN ('BASICS', 'FORM', 'IDEATION', 'COMMUNICATION'))
);

CREATE TABLE chapter_group (
  id SERIAL PRIMARY KEY,
  chapter_id INTEGER REFERENCES chapter(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL,
  started_at DATE NOT NULL DEFAULT now()
);

CREATE TABLE enrollment (
  chapter_group_id INTEGER NOT NULL REFERENCES chapter_group(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  grade NUMERIC(19,2),
  graded_at TIMESTAMP,
  grade_message TEXT,
  assistant BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (chapter_group_id, user_id)
);

CREATE TABLE chapter_subgroup (
  id SERIAL PRIMARY KEY,
  chapter_group_id INTEGER NOT NULL REFERENCES chapter_group(id) ON DELETE CASCADE,
  name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE subgroup_enrollment (
  chapter_subgroup_id INTEGER NOT NULL REFERENCES chapter_subgroup(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  PRIMARY KEY(chapter_subgroup_id, user_id)
);

----------------------------------
-- Task-related tables
----------------------------------
-- Create task page table. Note that the video URL may be up to 8000 octets according to RFC7230.
-- However, (older versions of) IE only accepts 2083 characters. Let's just make it a TEXT field
-- to be sure...
CREATE TABLE task_page (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  text TEXT DEFAULT NULL,
  author_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE ,
  task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE ,
  video_url TEXT DEFAULT NULL,
  UNIQUE (title, task_id)
);

CREATE TABLE task_page_image (
  id SERIAL PRIMARY KEY,
  task_page_id INTEGER NOT NULL REFERENCES task_page(id) ON DELETE CASCADE ,
  mime_type VARCHAR(30) NOT NULL,
  data BYTEA NOT NULL,
  CHECK (mime_type IN ('image/png', 'image/jpeg'))
);

----------------------------------
-- Task Example Submission-related tables
----------------------------------
-- These tables closely match the submission tables, but relate to a task instead of a chapter group
-- Additionally, they cannot be voted, annotated or soft deleted.
CREATE TABLE example_submission (
  id SERIAL PRIMARY KEY,
  task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  comment TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE example_submission_file (
  example_submission_id INTEGER PRIMARY KEY REFERENCES example_submission(id) ON DELETE CASCADE,
  mime_type VARCHAR(30) NOT NULL,
  data BYTEA NOT NULL,
  CHECK (mime_type IN ('image/png', 'image/jpeg'))
);

CREATE TABLE example_submission_thumbnail (
  example_submission_id INTEGER PRIMARY KEY REFERENCES example_submission(id) ON DELETE CASCADE,
  data BYTEA NOT NULL
  -- mime type is always image/jpeg
);

----------------------------------
-- Question & Answer-related tables
----------------------------------
CREATE TABLE question (
  id SERIAL PRIMARY KEY,
  task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  chapter_group_id INTEGER NOT NULL REFERENCES chapter_group(id) ON DELETE CASCADE,
  text TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE answer (
  id SERIAL PRIMARY KEY,
  question_id INTEGER NOT NULL REFERENCES question(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  text TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

----------------------------------
-- Submission-related tables
----------------------------------
CREATE TABLE submission (
  id SERIAL PRIMARY KEY,
  task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  chapter_group_id INTEGER NOT NULL REFERENCES chapter_group(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  best_work BOOLEAN NOT NULL DEFAULT FALSE,
  soft_deleted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (chapter_group_id, user_id) REFERENCES enrollment(chapter_group_id, user_id) ON DELETE CASCADE
);

CREATE TABLE submission_file (
  submission_id INTEGER PRIMARY KEY REFERENCES submission(id) ON DELETE CASCADE,
  mime_type VARCHAR(30) NOT NULL,
  data BYTEA NOT NULL,
  CHECK (mime_type IN ('image/png', 'image/jpeg'))
);

CREATE TABLE submission_thumbnail (
  submission_id INTEGER PRIMARY KEY REFERENCES submission(id) ON DELETE CASCADE,
  data BYTEA NOT NULL
  -- mime type is always image/jpeg
);

CREATE TABLE vote (
  submission_id INTEGER NOT NULL REFERENCES submission(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  PRIMARY KEY (submission_id, user_id)
);

CREATE TABLE annotation (
  id SERIAL PRIMARY KEY,
  submission_id INTEGER REFERENCES submission(id) ON DELETE CASCADE,
  user_id UUID REFERENCES "user"(id) ON DELETE CASCADE,
  drawing TEXT,
  comment TEXT,
  created_at TIMESTAMP DEFAULT NOW() NOT NULL,
  last_updated_at TIMESTAMP DEFAULT NOW() NOT NULL,
  soft_deleted BOOLEAN DEFAULT FALSE NOT NULL
);


----------------------------------
-- Notification table
----------------------------------
CREATE TABLE notification (
  id SERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
  event_type VARCHAR(30) NOT NULL,
  date TIMESTAMP NOT NULL DEFAULT NOW(),

  submission_id INTEGER REFERENCES submission(id) ON DELETE CASCADE,
  submission_annotation_id INTEGER REFERENCES annotation(id) ON DELETE CASCADE,

  question_id INTEGER REFERENCES question(id) ON DELETE CASCADE,
  question_answer_id INTEGER REFERENCES answer(id) ON DELETE CASCADE,

  chapter_group_id INTEGER REFERENCES chapter_group(id) ON DELETE CASCADE,

  CHECK (event_type IN (
  	'SUBMISSION_ANNOTATION', 'SUBMISSION_BEST_WORK',
  	'CHAPTER_GROUP_GRADE', 'CHAPTER_GROUP_ENROLL',
  	'TASK_QUESTION', 'QUESTION_ANSWER'))
);
