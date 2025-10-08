CREATE TABLE server_instance_owners
(
    server_instance_id BIGINT NOT NULL,
    user_id            BIGINT NOT NULL,
    CONSTRAINT pk_server_instance_owners PRIMARY KEY (server_instance_id, user_id)
);

ALTER TABLE server_instance_owners
    ADD CONSTRAINT fk_serinsown_on_server_instance FOREIGN KEY (server_instance_id) REFERENCES server_instances (id);

ALTER TABLE server_instance_owners
    ADD CONSTRAINT fk_serinsown_on_user FOREIGN KEY (user_id) REFERENCES users (id);