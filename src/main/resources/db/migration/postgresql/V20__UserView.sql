-- Creates a special view with UPDATE and INSERT support for managing UT users externally

-- Make sure uuid generation is available
-- CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create the view
CREATE VIEW utwente_user_view AS
  SELECT ut.utwente_id, u.first_name, u.last_name, u.email, ut.active, ut.created, ut.modified
  FROM utwente_user ut
  JOIN "user" u ON ut.user_id = u.id
  ORDER BY ut.utwente_id ASC;

-- Insert trigger
CREATE FUNCTION utwente_user_view_insert() RETURNS TRIGGER AS $$
  DECLARE
    -- Generate a new user id
    user_id UUID := gen_random_uuid();
  BEGIN
    INSERT INTO "user"(id, first_name, last_name, email, role)
      VALUES (user_id, NEW.first_name, NEW.last_name, NEW.email, 'STUDENT');
    INSERT INTO utwente_user(utwente_id, user_id, active, created, modified)
      VALUES (NEW.utwente_id, user_id, NEW.active, NEW.created, NEW.modified);
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql VOLATILE SECURITY DEFINER;

CREATE TRIGGER utwente_user_view_insert
  INSTEAD OF INSERT ON utwente_user_view
  FOR EACH ROW EXECUTE PROCEDURE utwente_user_view_insert();

-- Update trigger
CREATE FUNCTION utwente_user_view_update() RETURNS TRIGGER AS $$
  DECLARE
    _user_id UUID;
  BEGIN
    -- Fetch the user id
    SELECT user_id
      INTO _user_id
      FROM utwente_user
      WHERE utwente_id = OLD.utwente_id;

    -- Update both tables
    UPDATE "user"
      SET
        first_name = NEW.first_name,
        last_name = NEW.last_name,
        email = NEW.email
      WHERE id = _user_id;

    UPDATE utwente_user
      SET
        utwente_id = NEW.utwente_id,
        active = NEW.active,
        created = NEW.created,
        modified = NEW.modified
      WHERE user_id = _user_id;
    RETURN NEW;
  END;
$$ LANGUAGE plpgsql VOLATILE SECURITY DEFINER;

CREATE TRIGGER utwente_user_view_update
  INSTEAD OF UPDATE ON utwente_user_view
  FOR EACH ROW EXECUTE PROCEDURE utwente_user_view_update();