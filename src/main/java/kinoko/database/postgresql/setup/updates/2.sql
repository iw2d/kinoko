-- 2.sql
-- Contains the schema and initial data setup for the Family system.
-- This file defines all tables, indexes, defaults, and relationships
-- required for Family functionality within the server.


BEGIN;

CREATE TABLE player.family (
    character_id INT PRIMARY KEY
        REFERENCES player.characters(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- The 'parent' or 'senior' of this character. NULL if this character is a root/leader.
    parent_id INT REFERENCES player.characters(id)
        ON DELETE SET NULL  -- If a parent is deleted, the child becomes a root of their own branch.
        ON UPDATE CASCADE,

    reputation INT NOT NULL DEFAULT 0,
    total_reputation INT NOT NULL DEFAULT 0,
    reps_to_senior INT NOT NULL DEFAULT 0,

    CONSTRAINT self_parent_check CHECK (character_id <> parent_id) -- A character cannot be their own parent.

);

CREATE INDEX idx_family_parent_id ON player.family(parent_id);


CREATE OR REPLACE VIEW player.family_hierarchy AS
WITH RECURSIVE family_tree AS (
    SELECT
        character_id,
        parent_id,
        reputation,
        character_id AS root_id,
        0 AS level
    FROM player.family
    WHERE parent_id IS NULL

    UNION ALL

    SELECT
        f.character_id,
        f.parent_id,
        f.reputation,
        ft.root_id,
        ft.level + 1
    FROM player.family f
    JOIN family_tree ft ON f.parent_id = ft.character_id
)
SELECT * FROM family_tree;


COMMIT;
