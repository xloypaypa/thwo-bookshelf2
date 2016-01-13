drop table if exists wo_category;
create table wo_category (
  code varchar(10) primary key not null,
  name varchar(30) not null unique,
  description varchar(255) null
);

insert into wo_category(code, name) values('g623.58', 'it'), ('g623.5', 'mathematics'), ('b01', 'philosophy');