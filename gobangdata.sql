create database test_gobang;
use restful_gobang;

create table if not exists t_users
(
    u_id        int auto_increment,
    u_name      varchar(255) unique not null,
    u_nickname  varchar(255)        not null,
    u_pwd       varchar(255)        not null,
    u_gender    varchar(255),
    u_admin     boolean default false,
    u_level     int,
    u_image_url varchar(255) default 'http://localhost:8080/images/system/默认头像.png',
    primary key (u_id)
);

create table if not exists t_game_rooms
(
    g_id            int auto_increment,
    g_roomMaster    varchar(255),
    g_player        varchar(255),
    g_status        int,
    g_stepTime      int,
    g_chessData     text,
    g_chessStepData text,
    g_gameTime      int,
    g_winner        varchar(255),
    primary key (g_id)
);

create table if not exists t_players
(
    p_id           int auto_increment,
    p_name         varchar(255),
    p_gender       varchar(255),
    p_kind         int,
    p_level        int,
    p_restStepTime int,
    p_restGameTime int,
    gameRoom_id    int,
    user_id        int,
    primary key (p_id),
    foreign key (gameRoom_id) references t_game_rooms (g_id),
    foreign key (user_id) references t_users (u_id)
);

insert into t_users(u_name, u_nickname, u_pwd, u_gender, u_admin, u_level)
values ('123', '123', '123', '女', true, 10),
       ('张三', '光头强', '123456', '男', false, 5),
       ('李四', '熊大', '123456', '男', true, 0);