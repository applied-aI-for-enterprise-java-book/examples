drop table if exists Movie cascade;
drop sequence if exists Movie_SEQ;
drop extension if exists vector;

create extension vector;

---create table Movie (id bigint not null, title varchar(512), plot varchar(65535), director varchar(256), embedded vector(384), primary key (id));

---create sequence Movie_SEQ start with 1 increment by 50;

------