# SQL-Definitionen
# $Id: db_definition.sql,v 1.1 2005/04/05 21:34:48 tbayen Exp $

# Kontaktdaten für Kunden

create table Kunden (

# Durst-Daten
	Kundennr char(5),
	Durstname char(30),
	Betreuer int,

# Objekt
	Objektname char(25),
	Strasse char(25),
	PLZ char(5),
	Ort char(25),
	Sortiment char(25),
	Bindung char(25),

# Betriebsdaten
	Oeffnungszeit char(15),
	Ruhetage char(10),
	Liefertag int,
	Anruftag int,
	Betriebstyp int,
	Bierkuehlung int,
	Plaetze_Gastraum int(4),
	Plaetze_Saal int(4),
	Plaetze_Biergarten int(4),
	Plaetze_Kegelbahn int(4),
	Lieferstatus int,
	
# Betreiber
	Betreiber char(25),
	Privat_Strasse char(25),
	Privat_PLZ char(5),
	Privat_Ort char(25),
	Privat_Geb char(10),
	Betreiber_seit char(10),
	Steuernr char(14),

#Hauseigentümer
	Hauseigentuemer char(25),
	HE_Strasse char(25),
	HE_PLZ char(5),
	HE_Ort char(25),

#Sonstiges
	Bemerkungen text,

# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table DurstTelefonliste (
	Bemerkung char(42),
	Telefon char(22),
	Kunde int,

# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Telefonliste (
	Bemerkung char(42),
	Telefon char(22),
	Kunde int,

# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Betreuer (
	Name char(25),
	Nummer int,
	
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Wochentage (
	Name char(10),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Betriebstypen (
	Name char(20),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Bierkuehlungen (
	Name char(20),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



create table Lieferstatus (
	Name char(20),
# ID-Feld
	id int auto_increment, 
	PRIMARY KEY(id)
);



# Daten

insert into Wochentage (Name) values ("keiner");
insert into Wochentage (Name) values ("Montag");
insert into Wochentage (Name) values ("Dienstag");
insert into Wochentage (Name) values ("Mittwoch");
insert into Wochentage (Name) values ("Donnerstag");
insert into Wochentage (Name) values ("Freitag");
insert into Wochentage (Name) values ("Samstag");
insert into Wochentage (Name) values ("Sonntag");

insert into Betriebstypen (Name) values ("Sonstiges");
insert into Betriebstypen (Name) values ("Kneipe");
insert into Betriebstypen (Name) values ("Gaststätte");
insert into Betriebstypen (Name) values ("Erlebnisgastronomie");
insert into Betriebstypen (Name) values ("Cafe/Bistro");
insert into Betriebstypen (Name) values ("Diskothek");
insert into Betriebstypen (Name) values ("Restaurant");
insert into Betriebstypen (Name) values ("gehobenes Restaurant");
insert into Betriebstypen (Name) values ("Italiener");
insert into Betriebstypen (Name) values ("Veranstaltungen");
insert into Betriebstypen (Name) values ("Systemgastronomie");

insert into Bierkuehlungen (Name) values ("keine Angabe");
insert into Bierkuehlungen (Name) values ("keine Kühlung");
insert into Bierkuehlungen (Name) values ("Bierkeller");
insert into Bierkuehlungen (Name) values ("gekühltes Lager");
insert into Bierkuehlungen (Name) values ("nicht gekühltes Lager");

insert into Lieferstatus (Name) values ("keine Angabe");
insert into Lieferstatus (Name) values ("immer liefern");
insert into Lieferstatus (Name) values ("maximal eine Lieferung");
insert into Lieferstatus (Name) values ("Barzahlung vereinbart");
insert into Lieferstatus (Name) values ("streng bar");
insert into Lieferstatus (Name) values ("Sondervereinbarung");

insert into Betreuer (Name,Nummer) values("Ralf Günther",61);



# Testdaten

insert into kontakte (name,strasse) values ("Kurt Kunde", "Kundenweg 12");
insert into kontakte (name,strasse) values ("Thomas Bayen", "Bleichpfad 22");
insert into kontakte (name,strasse) values ("Michaela Mustermann", "Musterweg 3");


# $Log: db_definition.sql,v $
# Revision 1.1  2005/04/05 21:34:48  tbayen
# WebDatabase 1.4 - freigegeben auf Berlios
#
# Revision 1.11  2005/03/21 02:06:16  tbayen
# Komplette Überarbeitung des Web-Frameworks
# insbes. Modularisierung von URIParser und Actions
#
# Revision 1.10  2005/03/02 18:02:56  tbayen
# Probleme mit leeren Tabellen behoben
# Einige Änderungen wie mit Günther besprochen
#
# Revision 1.9  2005/02/28 01:28:48  tbayen
# Import-Funktion eingebaut
# Diese Version erstmalig auf dem Produktionsserver installiert
#
# Revision 1.8  2005/02/27 18:38:22  tbayen
# Kunden-Formuar wie mit Günther besprochen
#
# Revision 1.7  2005/02/26 13:16:52  tbayen
# Datenbank-Deklaration und Properties geändert
#
# Revision 1.6  2005/02/26 08:52:25  tbayen
# Probleme mit Character Encoding behoben
#
# Revision 1.5  2005/02/24 21:20:28  tbayen
# Umlaute können benutzt werden
#
# Revision 1.4  2005/02/24 15:47:43  tbayen
# Probleme mit der Neuanlage bei ForeignKeys behoben
#
# Revision 1.3  2005/02/24 00:35:28  tbayen
# Listen funktionieren, Datensätze anlegen funktioniert
#
# Revision 1.2  2005/02/23 14:25:14  tbayen
# automatisches Anlegen der Datenbank geht nun wieder
#