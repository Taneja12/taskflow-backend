CREATE TABLE comments (
    id UUID NOT NULL ,
    created_at TIMESTAMP(6) NOT NULL ,
    updated_at TIMESTAMP(6) NOT NULL ,

    task_id UUID NOT NULL ,
    user_id UUID NOT NULL ,

    content TEXT NOT NULL ,

    CONSTRAINT pk_comments
                      PRIMARY KEY (id),

    CONSTRAINT fk_comments_task
                      FOREIGN KEY (task_id)
                      REFERENCES tasks(id)
                      ON DELETE CASCADE ,

    CONSTRAINT fk_comments_user
                      FOREIGN KEY (user_id)
                      REFERENCES users(id)
);

CREATE INDEX idx_comments_task_created_at
    ON comments(task_id, created_at DESC );