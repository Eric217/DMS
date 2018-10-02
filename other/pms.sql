
create table if not exists admin
(
  id int(11) unsigned not null
    primary key,
  password varchar(255) not null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists bulletin
(
  id int(11) unsigned auto_increment
    primary key,
  time timestamp default CURRENT_TIMESTAMP not null,
  title varchar(20) default '' not null,
  content varchar(2095) default '' not null,
  `from` varchar(20) default '' null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists notification
(
  id int(11) unsigned auto_increment
    primary key,
  type varchar(20) default '' null,
  content varchar(140) not null,
  `from` varchar(20) default '' null,
  time timestamp not null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists student
(
  id char(12) not null
    primary key,
  name varchar(20) not null,
  password varchar(255) not null,
  email varchar(30) not null,
  phone varchar(11) default '' null,
  grade smallint(6) null,
  college varchar(20) default '' null,
  major varchar(20) default '' null,
  introduce varchar(255) default '' null,
  punish_end timestamp null,
  constraint student_email_uindex
  unique (email)
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists laboratory
(
  id int(11) unsigned auto_increment
    primary key,
  name varchar(20) not null,
  classroom varchar(20) not null,
  description varchar(500) default '' not null,
  leader_id char(12) null,
  create_time timestamp default CURRENT_TIMESTAMP not null,
  constraint laboratory_name_uindex
  unique (name),
  constraint laboratory_student_id_fk
  unique (leader_id),
  constraint laboratory_student_id_fk
  foreign key (leader_id) references student (id)
    on update cascade on delete set null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists notify
(
  id int(11) unsigned auto_increment
    primary key,
  nid int(11) unsigned not null,
  sid char(12) not null,
  constraint notify_notification_id_fk
  foreign key (nid) references notification (id),
  constraint notify_student_id_fk
  foreign key (sid) references student (id)
    on update cascade on delete cascade
)ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists project
(
  id int(11) unsigned auto_increment
    primary key,
  name varchar(20) not null,
  description varchar(255) default '' null,
  start_time timestamp null,
  end_time timestamp null,
  duration int unsigned default '2592000' not null,
  submit_time timestamp default CURRENT_TIMESTAMP not null,
  coach_id varchar(20) default '' null,
  opt_status smallint(5) unsigned default '0' not null,
  lab_name varchar(20) not null,
  leader_id char(12) null,
  aim varchar(1000) not null,
  type varchar(20) default '普通项目' null,
  deleted int(1) default '0' not null,
  constraint project_laboratory_name_fk
  foreign key (lab_name) references laboratory (name)
    on update cascade on delete cascade,
  constraint project_student_id_fk
  foreign key (leader_id) references student (id)
    on update cascade on delete set null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists modification
(
  id int(11) unsigned auto_increment
    primary key,
  pid int(11) unsigned not null,
  name varchar(20) not null,
  description varchar(255) default '' null,
  duration int unsigned default '2592000' not null,
  coach_id varchar(20) default '' null,
  opt_status smallint(5) unsigned default '0' not null,
  leader_id char(12) null,
  aim varchar(1000) not null,
  type varchar(20) default '' null,
  members varchar(127) not null,
  constraint modification_project_id_fk
  foreign key (pid) references project (id)
    on update cascade on delete cascade,
  constraint modification_student_id_fk
  foreign key (leader_id) references student (id)
    on update cascade on delete set null
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create table if not exists participate
(
  id int(11) unsigned auto_increment
    primary key,
  pid int(11) unsigned not null,
  sid char(12) not null,
  constraint participate_ibfk_1
  foreign key (pid) references project (id)
    on update cascade on delete cascade,
  constraint participate_ibfk_2
  foreign key (sid) references student (id)
    on update cascade on delete cascade
)
  ENGINE=InnoDB DEFAULT CHARSET=utf8
;

create index pid
  on participate (pid)
;

create index sid
  on participate (sid)
;

 