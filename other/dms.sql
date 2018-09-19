
create table admin
(
  id       int(11) unsigned auto_increment
    primary key,
  password varchar(255) not null
) engine=InnoDB DEFAULT CHARSET=utf8;



create table laboratory
(
  id          int auto_increment
    primary key,
  name        varchar(20)             not null,
  classroom   varchar(20)             not null,
  description varchar(500) default '' null,
  leader_id   char(12)                null,
  constraint laboratory_name_uindex
  unique (name)
) engine=InnoDB DEFAULT CHARSET=utf8;



create table student
(
  id         char(12)                not null
    primary key,
  name       varchar(20)             not null,
  password   varchar(255)            not null,
  email      varchar(30)             not null,
  phone      varchar(11) default ''  null,
  grade      smallint(6)             null,
  college    varchar(20) default ''  null,
  major      varchar(20) default ''  null,
  introduce  varchar(255) default '' null,
  punish_end timestamp               null,
  constraint student_email_uindex
  unique (email)
) engine=InnoDB DEFAULT CHARSET=utf8;



create table project
(
  id          int(11) unsigned auto_increment
    primary key,
  name        varchar(20)                      not null,
  description varchar(255) default ''          null,
  start_time  timestamp                        null,
  end_time    timestamp                        null,
  duration    int unsigned default '2592000'   not null,
  submit_time timestamp                        not null,
  coach_id    varchar(20) default ''           null,
  opt_status  smallint(5) unsigned default '0' not null,
  lab_name    varchar(20)                           not   null,
  leader_id   char(12)                         not null,
  aim         varchar(2000)                    not null,
  type        varchar(20) default ''           null,
  deleted     int(1) default '0'               not null,
  constraint project_laboratory_name_fk
  foreign key (lab_name) references laboratory (name)
    on update cascade
    on delete cascade
) engine=InnoDB DEFAULT CHARSET=utf8;



create table participate
(
  id  int(11) unsigned auto_increment
    primary key,
  pid int(11) unsigned not null,
  sid char(12)         not null,
  constraint participate_ibfk_1
  foreign key (pid) references project (id)
    on update cascade
    on delete cascade,
  constraint participate_ibfk_2
  foreign key (sid) references student (id)
    on update cascade
    on delete cascade
) engine=InnoDB DEFAULT CHARSET=utf8;

create index pid
  on participate (pid);

create index sid
  on participate (sid);


create table modification
(
  id          int(11) unsigned auto_increment
    primary key,
  sid         char(12)                         not null,
  pid         int(11) unsigned                 not null,
  name        varchar(20)                      not null,
  description varchar(255) default ''          null,
  start_time  timestamp                        null,
  end_time    timestamp                        null,
  duration    int unsigned default '2592000'   not null,
  submit_time timestamp                        not null,
  coach_id    varchar(20) default ''           null,
  opt_status  smallint(5) unsigned default '0' not null,
  lab_name    varchar(20)                      not null,
  leader_id   char(12)                         not null,
  aim         varchar(1000)                    not null,
  type        varchar(20) default ''           null,
  deleted     int(1) default '0'               not null,
  constraint modification_project_id_fk
  foreign key (pid) references project (id)
    on update cascade
    on delete cascade,
  constraint modification_student_id_fk
  foreign key (sid) references student (id)
    on update cascade
    on delete cascade
) engine=InnoDB DEFAULT CHARSET=utf8;
