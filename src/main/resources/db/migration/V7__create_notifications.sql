-- V7__create_notifications.sql

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- User who receives the notification
    recipient_id UUID NOT NULL,

    -- User who caused the notification
    actor_id UUID,

    -- Notification type
    type VARCHAR(50) NOT NULL,

    -- Human-readable notification message
    message TEXT NOT NULL,

    -- Related entities
    task_id UUID,
    project_id UUID,
    workspace_id UUID,

    -- NULL means unread
    read_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_recipient
       FOREIGN KEY (recipient_id)
           REFERENCES users(id)
           ON DELETE CASCADE,

    CONSTRAINT fk_notifications_actor
       FOREIGN KEY (actor_id)
           REFERENCES users(id)
           ON DELETE SET NULL,

   CONSTRAINT fk_notifications_task
       FOREIGN KEY (task_id)
           REFERENCES tasks(id)
           ON DELETE CASCADE,

   CONSTRAINT fk_notifications_project
       FOREIGN KEY (project_id)
           REFERENCES projects(id)
           ON DELETE CASCADE,

   CONSTRAINT fk_notifications_workspace
       FOREIGN KEY (workspace_id)
           REFERENCES workspaces(id)
           ON DELETE CASCADE
);

-- Fast lookup of a user's notifications
CREATE INDEX idx_notifications_recipient_id
    ON notifications(recipient_id);

-- Fast lookup of unread notifications
CREATE INDEX idx_notifications_recipient_unread
    ON notifications(recipient_id, read_at);

-- Newest notifications first
CREATE INDEX idx_notifications_recipient_created
    ON notifications(recipient_id, created_at DESC);

-- Useful when querying notifications related to a task
CREATE INDEX idx_notifications_task_id
    ON notifications(task_id);

-- Useful when querying notifications related to a project
CREATE INDEX idx_notifications_project_id
    ON notifications(project_id);