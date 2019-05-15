create database autocheck;
create user 'siemens'@'localhost' identified by 'siemens';
grant all on autocheck.* to 'siemens'@'localhost';

use autocheck;
INSERT INTO parameter (name, value) values ("Check new sentences", 1);
INSERT INTO parameter (name, value) values ("Similarity Algorithm", 12);