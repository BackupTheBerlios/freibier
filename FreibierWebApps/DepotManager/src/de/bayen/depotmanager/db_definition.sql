# SQL-Definitionen
# $Id: db_definition.sql,v 1.2 2006/01/24 21:59:28 tbayen Exp $

create table Portfolios (
	Name		char(20),
	# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

create table Wertpapiere (
	Name		char(40),
	ISIN		char(12),
	# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

create table Bewegungen (
    Portfolio	int,
    Wertpapier	int,
	Datum 		date,
	Anzahl		decimal(11,2),
	Kurs		decimal(11,2),
	# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

create table Kursdaten (
	Wertpapier 	int,
	Datum 		date,
	Schlusskurs decimal(11,2),
	# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

# $Log: db_definition.sql,v $
# Revision 1.2  2006/01/24 21:59:28  tbayen
# Prozentangabe bei Gewinn/Verlust
#
# Revision 1.1  2006/01/21 23:20:50  tbayen
# Erste Version 1.0 des DepotManagers
# erste FreibierWeb-Applikation im eigenen Paket
#
