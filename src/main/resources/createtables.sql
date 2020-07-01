create table if not exists ConquestPoints(
	name            varchar(36) not null,
	location        varchar(64),
    id              int(8) not null auto_increment,
    radius          int not null default 0,
    minPlayers      int not null default 0,
    holder          varchar(16),
	rewards         varchar(128),



    PRIMARY key(id),
    Unique Index (name)
);