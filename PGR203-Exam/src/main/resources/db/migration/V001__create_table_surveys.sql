create table surveys
(
    id    serial primary key,
    title varchar(100) not null,
    description  varchar(800)
)