CREATE TABLE activity_logs (
   id UUID NOT NULL,
   created_at TIMESTAMP(6) NOT NULL,
   updated_at TIMESTAMP(6) NOT NULL,

   user_id UUID NOT NULL,
   workspace_id UUID NOT NULL,

   action VARCHAR(255) NOT NULL,
   entity_type VARCHAR(255) NOT NULL,
   entity_id UUID NOT NULL,

   description VARCHAR(500),

   CONSTRAINT pk_activity_logs
       PRIMARY KEY (id),

   CONSTRAINT fk_activity_logs_user
       FOREIGN KEY (user_id)
           REFERENCES users(id),

   CONSTRAINT fk_activity_logs_workspace
       FOREIGN KEY (workspace_id)
           REFERENCES workspaces(id)

);
CREATE INDEX idx_activity_logs_workspace_id
    ON activity_logs(workspace_id);

CREATE INDEX idx_activity_logs_workspace_created_at
    ON activity_logs(workspace_id, created_at DESC);