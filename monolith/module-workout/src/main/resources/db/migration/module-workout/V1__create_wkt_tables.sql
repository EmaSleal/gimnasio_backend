CREATE TABLE wkt_muscular_groups (
    wkt_mg_id  BIGSERIAL    PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    CONSTRAINT uq_wkt_mg_name UNIQUE (name)
);

CREATE TABLE wkt_workouts (
    wkt_workout_id    BIGSERIAL    PRIMARY KEY,
    name              VARCHAR(200) NOT NULL,
    muscular_group_id BIGINT       NOT NULL
        REFERENCES wkt_muscular_groups(wkt_mg_id),
    muscular_load     VARCHAR(20)  NOT NULL,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        BIGINT,
    last_modified_by  BIGINT
);

CREATE TABLE wkt_workout_specifications (
    wkt_spec_id        BIGSERIAL    PRIMARY KEY,
    workout_id         BIGINT       NOT NULL
        REFERENCES wkt_workouts(wkt_workout_id),
    description        VARCHAR(500),
    reps_number        INTEGER,
    sets_number        INTEGER      NOT NULL,
    recommended_weight NUMERIC(6,2) NOT NULL,
    trainer_rating     NUMERIC(3,1) NOT NULL,
    is_time_based      BOOLEAN      NOT NULL DEFAULT FALSE,
    time_seconds       INTEGER,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    created_by         BIGINT,
    last_modified_by   BIGINT
);
