CREATE TABLE password_reset_tokens (
                                       id UUID PRIMARY KEY,

                                       user_id UUID NOT NULL,

                                       token_hash VARCHAR(64) NOT NULL UNIQUE,

                                       expiry_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,

                                       used BOOLEAN NOT NULL DEFAULT FALSE,

                                       created_at TIMESTAMP WITHOUT TIME ZONE,

                                       updated_at TIMESTAMP WITHOUT TIME ZONE,

                                       CONSTRAINT fk_password_reset_tokens_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(id)
                                               ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_tokens_user_id
    ON password_reset_tokens(user_id);