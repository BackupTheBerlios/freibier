# $Id: test.sql,v 1.1 2005/08/07 21:18:49 tbayen Exp $

drop table if exists adressen;
create table adressen (
	vorname char(20), 
	nachname char(20), 
	lebensalter int,
	sprache int,
	lieblingstag int,
	id int auto_increment, 
	PRIMARY KEY(id)
);

drop table if exists programmiersprachen;
create table programmiersprachen (
	id int auto_increment,
	name char(10),
	PRIMARY KEY(id)
);

drop table if exists feiertage;
create table feiertage (
	id int auto_increment,
	name char(15),
	datum date,
	PRIMARY KEY(id)
);

drop table if exists adressen2;
create table adressen2 (vorname char(20), nachname char(20));

# Mit Daten füllen:
insert into programmiersprachen (name) values ('BASIC');
insert into programmiersprachen (name) values ('Assembler');
insert into programmiersprachen (name) values ('C');
insert into programmiersprachen (name) values ('C++');
insert into programmiersprachen (name) values ('Java');
insert into programmiersprachen (name) values ('Rexx');
insert into programmiersprachen (name) values ('Python');
insert into programmiersprachen (name) values ('Perl');
insert into programmiersprachen (name) values ('Ruby');
insert into programmiersprachen (name) values ('Lisp');

insert into feiertage (name,datum) values ('Weihnachten','2004-12-24');
insert into feiertage (name,datum) values ('Tag der deutschen Einheit','2004-10-03');

insert into adressen (vorname,nachname,lebensalter,sprache,lieblingstag) values ('Peter','Hormanns',null,5,1);
insert into adressen (vorname,nachname,lebensalter,sprache,lieblingstag) values ('Thomas','Bayen',33,8,2);

# Diese werden im GUI-Test benutzt und müssen hier weggeräumt werden, damit die JUnit-Tests alle durchlaufen
drop table if exists GrosseTabelle;
drop table if exists appointments;

# $Log: test.sql,v $
# Revision 1.1  2005/08/07 21:18:49  tbayen
# Version 1.0 der Freibier-Datenbankklassen,
# extrahiert aus dem Projekt WebDatabase V1.5
#
# Revision 1.1  2005/04/05 21:34:47  tbayen
# WebDatabase 1.4 - freigegeben auf Berlios
#
# Revision 1.1  2005/03/23 18:03:10  tbayen
# Sourcecodebaum reorganisiert und
# Javadoc-Kommentare überarbeitet
#
# Revision 1.1  2005/02/21 16:11:53  tbayen
# Erste Version mit Tabellen- und Datensatzansicht
#
# Revision 1.11  2004/11/01 12:06:46  tbayen
# Erste Kalenderversion mit Datenbank-Zugriff
#
# Revision 1.10  2004/10/24 13:10:20  tbayen
# Merken des Typs des Zielwertes eines Foreign Keys
# formatierte Ausgabe von Foreign Keys und Test hierzu
#
# Revision 1.9  2004/10/18 12:16:56  tbayen
# Log-Einträge auch in nicht-Java-Files
#
