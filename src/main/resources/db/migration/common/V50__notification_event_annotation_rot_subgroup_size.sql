-- Remove event_type check constraint on notification.
ALTER TABLE "notification" DROP CONSTRAINT IF EXISTS "notification_event_type_check";
-- Do the same thing for H2
ALTER TABLE "notification" DROP CONSTRAINT IF EXISTS "CONSTRAINT_237A88EB_1";

-- Add task_id column and its foreign key constraint
ALTER TABLE "notification"
  ADD COLUMN task_id INTEGER;

ALTER TABLE "notification"
  ADD CONSTRAINT fk_notification_task_id
    FOREIGN KEY (task_id) REFERENCES task(id)
    ON DELETE CASCADE;

-- Add event_type check constrain back with additional item.
ALTER TABLE "notification"
  ADD CONSTRAINT notification_event_type_check CHECK (event_type IN (
  'SUBMISSION_ANNOTATION', 'SUBMISSION_BEST_WORK',
  'CHAPTER_GROUP_GRADE', 'CHAPTER_GROUP_ENROLL',
  'TASK_QUESTION', 'TASK_CREATION', 'QUESTION_ANSWER'));

-- Add rotation fields to the annotation such that we can rotate submissions.
ALTER TABLE "annotation" ADD COLUMN "invert_x" BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE "annotation" ADD COLUMN "invert_y" BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE "annotation" ADD COLUMN "flip_xy" BOOLEAN DEFAULT FALSE NOT NULL;

-- Add subgroup size column to limit the size of subgroups.
ALTER TABLE "chapter_subgroup" ADD COLUMN "size" INTEGER DEFAULT NULL;

