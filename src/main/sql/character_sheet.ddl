create table character_sheet (
   id INTEGER not null PRIMARY KEY AUTO_INCREMENT,
   user_name VARCHAR(50),
   character_key VARCHAR(255) not null,
   character_xml LONGTEXT not null
);

create table tracked_stat (
   id INTEGER not null PRIMARY KEY AUTO_INCREMENT,
   character_key VARCHAR(255) not null,
   stat_name VARCHAR(255) not null,
   value INTEGER not null
);

create table tracked_value (
   id INTEGER not null PRIMARY KEY AUTO_INCREMENT,
   character_key VARCHAR(255) not null,
   abbreviation VARCHAR(255) not null,
   order INTEGER not null,
   name VARCHAR(255) not null,
   notes VARCHAR(255) not null,
   max_value INTEGER not null,
   current_value INTEGER not null
);
