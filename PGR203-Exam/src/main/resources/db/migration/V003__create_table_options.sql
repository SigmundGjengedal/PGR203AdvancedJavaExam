create table options
(
    option_id   serial,
    option_text varchar(100),
    question_id int,
    primary key (option_id),
    constraint fk_options
        foreign key (question_id)
            references questions(id)
            on delete cascade

)