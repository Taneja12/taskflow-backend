ALTER TABLE activity_logs
DROP CONSTRAINT fk_activity_logs_workspace;

ALTER TABLE activity_logs
    ALTER COLUMN workspace_id DROP NOT NULL;

ALTER TABLE activity_logs
    ADD CONSTRAINT fk_activity_logs_workspace
        FOREIGN KEY (workspace_id)
            REFERENCES workspaces(id)
            ON DELETE SET NULL;