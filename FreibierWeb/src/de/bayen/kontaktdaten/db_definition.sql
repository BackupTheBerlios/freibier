# SQL-Definitionen
# $Id: db_definition.sql,v 1.1 2005/04/05 21:34:48 tbayen Exp $

create table Telefonbuch (
	Bemerkung char(42),
	Telefon char(22),
	Kategorie int,

# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

create table Kategorien (
	Name char(25),
	
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



# $Log: db_definition.sql,v $
# Revision 1.1  2005/04/05 21:34:48  tbayen
# WebDatabase 1.4 - freigegeben auf Berlios
#
# Revision 1.1  2005/04/05 21:14:11  tbayen
# HBCI-Banking-Applikation fertiggestellt
#
# Revision 1.1  2005/03/22 19:28:27  tbayen
# Telefonbuch als zweite Applikation
# Behebung einiger Bugs; jetzt Version 1.2
#
