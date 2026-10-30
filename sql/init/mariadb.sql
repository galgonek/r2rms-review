create database nextprot;
grant all privileges on nextprot.* TO 'nextprot'@'%' identified by 'password';
grant grant option on nextprot.* TO 'nextprot'@'%';
create user 'sparql'@'%' identified by 'password';
flush privileges;
