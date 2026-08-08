CREATE INDEX idx_projects_workspace
    ON projects(workspace_id);

CREATE INDEX idx_boards_project
    ON boards(project_id);

CREATE INDEX idx_tasks_board
    ON tasks(board_id);

CREATE INDEX idx_tasks_assigned_member
    ON tasks(assigned_member_id);

CREATE INDEX idx_workspace_members_user
    ON workspace_members(user_id);
