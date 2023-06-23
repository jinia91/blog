alter table articles
    add constraint FK_articles_category_id foreign key (category_id) references categories (category_id);
