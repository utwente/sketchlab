-- Remove UNIQUE constraint on task page, where (task_page.title,task.id) should be unique (why?!?).
ALTER TABLE "task_page" DROP CONSTRAINT IF EXISTS "task_page_title_task_id_key";
-- Do the same thing for H2.
ALTER TABLE "task_page" DROP CONSTRAINT IF EXISTS "CONSTRAINT_AC69"
