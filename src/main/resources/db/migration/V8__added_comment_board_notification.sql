ALTER TABLE notifications
ADD COLUMN board_id UUID,
ADD COLUMN comment_id UUID,
ADD  CONSTRAINT fk_notifications_board
    FOREIGN KEY (board_id)
    REFERENCES boards(id)
    ON DELETE CASCADE,
ADD CONSTRAINT fk_notifications_comment
    FOREIGN KEY (comment_id)
    REFERENCES comments(id)
    ON DELETE CASCADE;