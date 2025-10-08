CREATE SEQUENCE IF NOT EXISTS server_instances_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE server_instances
(
    id            BIGINT       NOT NULL,
    container_id  VARCHAR(255) NOT NULL,
    name          VARCHAR(255) NOT NULL,
    friendly_name VARCHAR(255) NOT NULL,
    jwt_secret    VARCHAR(255) NOT NULL,
    CONSTRAINT pk_server_instances PRIMARY KEY (id)
);

ALTER TABLE server_instances
    ADD CONSTRAINT uc_server_instances_name UNIQUE (name);