CREATE TABLE ratings (
    id         BIGSERIAL PRIMARY KEY,
    score      INTEGER   NOT NULL,
    user_id    BIGINT    NOT NULL,
    series_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ratings_user       FOREIGN KEY (user_id)   REFERENCES users (id),
    CONSTRAINT fk_ratings_series     FOREIGN KEY (series_id) REFERENCES series (id),
    CONSTRAINT chk_ratings_score     CHECK (score >= 1 AND score <= 5),
    CONSTRAINT uk_ratings_user_series UNIQUE (user_id, series_id)
);
