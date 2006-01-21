# SQL-Definitionen
# $Id: db_definition.sql,v 1.1 2006/01/21 23:10:09 tbayen Exp $

create table Adressen (
	Name		char(20),
	Vorname		char(20),
	Strasse		char(25),
	PLZ			char(5),
	Ort			char(20),
	# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);

# $Log: db_definition.sql,v $
# Revision 1.1  2006/01/21 23:10:09  tbayen
# Komplette Überarbeitung und Aufteilung als Einzelbibliothek - Version 1.6
#
