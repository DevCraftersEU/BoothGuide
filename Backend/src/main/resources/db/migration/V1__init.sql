create sequence if not exists application_info_seq start with 1 increment by 1;
create sequence if not exists application_user_seq start with 1 increment by 1;
create sequence if not exists exhibitor_seq start with 1 increment by 1;

create table if not exists application_info (
    id bigint not null,
    info_name varchar(255) not null,
    info_value varchar(4096) not null,
    info_type varchar(255) not null,
    primary key (id)
);

create table if not exists application_user (
    id bigint not null,
    username varchar(255),
    password varchar(255),
    primary key (id),
    constraint uk_application_user_username unique (username)
);

create table if not exists exhibitor (
    id bigint not null,
    name varchar(255),
    offers text,
    room_number varchar(255),
    primary key (id)
);

create table if not exists user_roles (
    application_user_id bigint not null,
    roles varchar(255) not null,
    primary key (application_user_id, roles),
    constraint fk_user_roles_application_user
        foreign key (application_user_id) references application_user (id)
);
