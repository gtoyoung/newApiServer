insert into tb_user_role values (1, 'ADMIN');
insert into tb_user_role values (2, 'USER');
insert into tb_users values ('userA', 'Ban', '{bcrypt}$2a$10$3t3/ZKBL/kzy0uhVp0UZTuaKVAU31kIoydi6qRTo92r4LeO5BOjje');
insert into tb_user_role_mapping values (1,'userA', 2);