create sequence instance_config_seq start with 1 increment by 50;
create table instance_config (selected_java_version integer, id bigint not null, selected_minecraft_version varchar(255), selected_minecraft_version_type enum ('BUKKIT','FABRIC','FORGE','NEOFORGE','NONE','PAPER','QUILT','SPIGOT','VANILLA'), primary key (id));
create table java_versions (version integer not null, primary key (version));
