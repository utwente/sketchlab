-- Remove unique naming constraint on subgroup.
ALTER TABLE "chapter_subgroup" DROP CONSTRAINT IF EXISTS "chapter_subgroup_name_key";
-- Do the same thing for H2
ALTER TABLE "chapter_subgroup" DROP CONSTRAINT IF EXISTS "CONSTRAINT_3837";


-- Add sorting method for task pages.
ALTER TABLE "task_page" ADD COLUMN slot INT NOT NULL DEFAULT 1;
