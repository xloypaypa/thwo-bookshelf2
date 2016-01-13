drop table if exists wo_book;
create table wo_book (
  isbn varchar(30) primary key,
  name varchar(100) not null,
  author varchar(50) not null,
  price double not null
);

insert into wo_book(isbn, name, author, price) values('isbn', 'book name', 'author', 55.20);