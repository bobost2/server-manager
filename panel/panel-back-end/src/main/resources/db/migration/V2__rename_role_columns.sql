ALTER TABLE roles
    RENAME COLUMN permission1 TO perm_instance_lifecycle;

ALTER TABLE roles
    RENAME COLUMN permission2 TO perm_instance_files;

ALTER TABLE roles
    RENAME COLUMN permission3 TO perm_instance_console;