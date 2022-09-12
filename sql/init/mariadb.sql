create database nextprot;
grant all privileges on nextprot.* TO 'nextprot'@'localhost' identified by 'password';
grant grant option on nextprot.* TO 'nextprot'@'localhost';
create user 'sparql'@'localhost' identified by 'password';
flush privileges;
