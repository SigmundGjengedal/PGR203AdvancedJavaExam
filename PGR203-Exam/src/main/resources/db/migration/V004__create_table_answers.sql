create table answers(

    answer_id serial,
    question_id int,
    option_id int,
    primary key (answer_id),
    constraint fk_answers
        foreign key(question_id)
            references questions(id)
            on delete cascade
)