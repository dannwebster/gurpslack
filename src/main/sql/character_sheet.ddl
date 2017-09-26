create table character_sheet (
   id INTEGER not null PRIMARY KEY AUTO_INCREMENT,
   user_name VARCHAR(50),
   character_key VARCHAR(255) not null,
   character_xml LONGTEXT not null
)
