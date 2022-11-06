create table users
(
    id       bigserial
        constraint users_pk
            primary key,
    login    varchar(45)  not null,
    password varchar(255) not null,
    role     varchar(6)   not null
);

alter table users
    owner to postgres;

create unique index users_id_uindex
    on users (id);

create unique index users_login_uindex
    on users (login);
