create table questions(
    id serial primary key ,
    title varchar(100),
    text varchar(1000) not null,
    surveyId int

)