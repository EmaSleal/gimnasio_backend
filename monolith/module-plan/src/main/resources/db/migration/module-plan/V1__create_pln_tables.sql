CREATE TABLE pln_workout_plans (
    pln_plan_id      BIGSERIAL    PRIMARY KEY,
    user_id          BIGINT       NOT NULL REFERENCES usr_users(usr_user_id),
    trainer_id       BIGINT       NOT NULL REFERENCES usr_users(usr_user_id),
    description      TEXT,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    start_date       DATE         NOT NULL,
    end_date         DATE         NOT NULL,
    is_template      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       BIGINT,
    last_modified_by BIGINT
);

CREATE TABLE pln_daily_routines (
    pln_routine_id  BIGSERIAL   PRIMARY KEY,
    workout_plan_id BIGINT      NOT NULL
        REFERENCES pln_workout_plans(pln_plan_id) ON DELETE CASCADE,
    day_of_week     VARCHAR(10) NOT NULL,
    CONSTRAINT uq_pln_plan_day UNIQUE (workout_plan_id, day_of_week)
);

CREATE TABLE pln_routine_specifications (
    routine_id BIGINT NOT NULL
        REFERENCES pln_daily_routines(pln_routine_id) ON DELETE CASCADE,
    spec_id    BIGINT NOT NULL
        REFERENCES wkt_workout_specifications(wkt_spec_id),
    PRIMARY KEY (routine_id, spec_id)
);
