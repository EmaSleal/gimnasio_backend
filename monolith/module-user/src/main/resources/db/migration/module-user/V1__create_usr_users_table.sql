CREATE TABLE usr_users (
    usr_user_id             BIGSERIAL    PRIMARY KEY,
    username                VARCHAR(50)  NOT NULL,
    email                   VARCHAR(255) NOT NULL,
    password                VARCHAR(255) NOT NULL,
    role                    VARCHAR(20)  NOT NULL,
    enabled                 BOOLEAN      NOT NULL DEFAULT TRUE,
    account_non_expired     BOOLEAN      NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN      NOT NULL DEFAULT TRUE,
    account_non_locked      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMP,
    updated_at              TIMESTAMP,
    created_by              BIGINT,
    last_modified_by        BIGINT,
    CONSTRAINT uq_usr_username UNIQUE (username),
    CONSTRAINT uq_usr_email    UNIQUE (email)
);
