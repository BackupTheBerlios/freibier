# SQL-Definitionen
# $Id: db_definition.sql,v 1.1 2006/01/24 00:26:00 tbayen Exp $

create table Konten(
	Bezeichnung			char(30),
	Kurzname			char(10),
	Inhaber				char(27),
	Kontonummer			char(10),
	BLZ					char(8),
	Sicherheitsmedium	char(10),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Pool (
	Bezeichnung			char(60),
	Dateiname			char(20),
	DTAUS				mediumblob,
	Ergebniscode		text,
	Versandzeit			datetime,
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Zahlungsarten (
	Bezeichnung		char(20),
	Textschluessel	char(2),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Ausgangskoerbe (
	Bezeichnung		char(30),
	Konto			int,
	Zahlungsart		int,
	Dauerauftraege	bool,
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Transaktionen (
	Empfaenger		char(27),
	BLZ				char(8),
	Kontonummer		char(10),
	Ausgangskorb	int,
	Betrag			decimal(11,2),
	Vwz1			char(27),
	Vwz2			char(27),
	Vwz3			char(27),
	Vwz4			char(27),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



# Vorgegebene Daten
###################
insert into Konten (Bezeichnung,Kurzname,Inhaber,Kontonummer,BLZ,Sicherheitsmedium,id)
    values("Testkonto","Test","Niemand","123456789","32050000","PinTan",1);

insert into Zahlungsarten (Bezeichnung, Textschluessel, id) values("Überweisung","51",1);
insert into Zahlungsarten (Bezeichnung, Textschluessel, id) values("Abbuchung (sofort)","04",2);
insert into Zahlungsarten (Bezeichnung, Textschluessel, id) values("Einzug (6 Wochen)","05",3);

insert into Ausgangskoerbe (Bezeichnung,Konto,Zahlungsart,Dauerauftraege)
    values("Standardkorb",1,1,0);



# $Log: db_definition.sql,v $
# Revision 1.1  2006/01/24 00:26:00  tbayen
# Erste eigenständige Version (1.6beta)
# sollte funktional gleich sein mit banking-Modul aus WebDatabase/FreibierWeb 1.5
#
# Revision 1.2  2005/08/07 16:56:14  tbayen
# Produktionsversion 1.5
#
# Revision 1.1  2005/04/05 21:34:47  tbayen
# WebDatabase 1.4 - freigegeben auf Berlios
#
# Revision 1.4  2005/04/05 21:14:11  tbayen
# HBCI-Banking-Applikation fertiggestellt
#
# Revision 1.3  2005/03/28 15:53:02  tbayen
# Boolean-Typen eingeführt
#
# Revision 1.2  2005/03/28 03:09:46  tbayen
# Binärdaten (BLOBS) in der Datenbank und im Webinterface
#
# Revision 1.1  2005/03/26 23:07:29  tbayen
# Auswahl mehrerer Konten für Auszug
#
