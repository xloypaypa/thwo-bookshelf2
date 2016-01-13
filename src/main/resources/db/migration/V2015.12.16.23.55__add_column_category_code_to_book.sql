alter table wo_book
add column category_code varchar(10) null;
alter table wo_book
add constraint fk_category_code foreign key(category_code) references wo_category(code);