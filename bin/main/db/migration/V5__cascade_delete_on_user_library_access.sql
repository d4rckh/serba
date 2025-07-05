-- Drop existing foreign key constraints (adjust constraint names if needed)
ALTER TABLE user_library_access
  DROP CONSTRAINT IF EXISTS fk_user;

ALTER TABLE user_library_access
  DROP CONSTRAINT IF EXISTS fk_library;

-- Add foreign keys with ON DELETE CASCADE
ALTER TABLE user_library_access
  ADD CONSTRAINT fk_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE user_library_access
  ADD CONSTRAINT fk_library
    FOREIGN KEY (library_id) REFERENCES libraries(id) ON DELETE CASCADE;
