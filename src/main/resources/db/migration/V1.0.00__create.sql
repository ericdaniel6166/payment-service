BEGIN;

CREATE TABLE payment
(
    id                 BIGSERIAL
        PRIMARY KEY,
    order_id           BIGINT         NOT NULL,
    total_amount       NUMERIC(19, 4) NOT NULL,
    status             VARCHAR(255)   NOT NULL,
    payment_detail     TEXT,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6)
);

CREATE UNIQUE INDEX payment_order_id_uindex ON payment (order_id);

CREATE TABLE payment_status_history
(
    id                 BIGSERIAL
        PRIMARY KEY,
    payment_id         BIGINT       NOT NULL,
    status             VARCHAR(255) NOT NULL,
    payment_detail     TEXT,
    created_by         VARCHAR(255),
    created_date       TIMESTAMP(6),
    last_modified_by   VARCHAR(255),
    last_modified_date TIMESTAMP(6)
);

ALTER TABLE payment_status_history
    OWNER TO postgres;

CREATE INDEX payment_status_history_payment_id_index
    ON payment_status_history (payment_id);

COMMIT;
