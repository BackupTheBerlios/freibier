-- $Id: db_definition.sql,v 1.8 2005/11/24 16:50:11 tbayen Exp $
--
-- phpMyAdmin SQL Dump
-- version 2.6.2
-- http://www.phpmyadmin.net
-- 
-- Host: localhost
-- Erstellungszeit: 08. August 2005 um 03:35
-- Server Version: 4.0.24
-- PHP-Version: 4.3.10-15
-- 
-- Datenbank: `fibu`
-- 

-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `Buchungen`
-- 

CREATE TABLE `Buchungen` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Journal` int(10) unsigned NOT NULL default '0',
  `Belegnummer` char(8) NOT NULL default '',
  `Buchungstext` char(50) NOT NULL default '',
  `Valutadatum` date NOT NULL default '0000-00-00',
  `Erfassungsdatum` datetime NOT NULL default '0000-00-00',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten f�r Tabelle `Buchungen`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `Buchungszeilen`
-- 

CREATE TABLE `Buchungszeilen` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Buchung` int(10) unsigned NOT NULL default '0',
  `Konto` int(10) unsigned NOT NULL default '0',
  `Betrag` decimal(10,2) NOT NULL default '0.00',
  `SH` char(1) NOT NULL default '',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten f�r Tabelle `Buchungszeilen`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `Journale`
-- 

CREATE TABLE `Journale` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Journalnummer` int(10) unsigned NOT NULL default 0,
  `Startdatum` date NOT NULL default '0000-00-00',
  `Buchungsjahr` char(4) NOT NULL default '',
  `Buchungsperiode` char(2) NOT NULL default '',
  `absummiert` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten f�r Tabelle `Journale`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `Konten`
-- 

CREATE TABLE `Konten` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Kontonummer` char(8) NOT NULL default '',
  `Bezeichnung` char(100) NOT NULL default '',
  `MwSt` int(1) unsigned default NULL,
  `Oberkonto` int(10) unsigned default NULL,
  `Soll` tinyint(1) NOT NULL default '0',
  `Gewicht` int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten f�r Tabelle `Konten`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `MwSt`
-- 

CREATE TABLE `MwSt` (
  `id` int(10) NOT NULL auto_increment,
  `MwStSatz` decimal(3,1) NOT NULL default '0.0',
  `Bezeichnung` char(15) NOT NULL default '',
  `KtoVSt` int(10) default NULL,
  `KtoMwSt` int(10) default NULL,
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=4 ;

-- 
-- Daten f�r Tabelle `MwSt`
-- 

INSERT INTO `MwSt` VALUES (1, 0.0, 'keine', NULL, NULL);
INSERT INTO `MwSt` VALUES (2, 7.0, 'vermindert', NULL, NULL);
INSERT INTO `MwSt` VALUES (3, 16.0, 'voll', NULL, NULL);



-- --------------------------------------------------------

-- 
-- Tabellenstruktur f�r Tabelle `Firmenstammdaten`
-- 

CREATE TABLE `Firmenstammdaten` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Firma` varchar(30) NOT NULL default '',
  `Bilanzkonto` int(10) unsigned NOT NULL default '0',
  `GuVKonto` int(10) unsigned NOT NULL default '0',
  `JahrAktuell` char(4) NOT NULL default '2005',
  `PeriodeAktuell` char(2) NOT NULL default '01',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten f�r Tabelle `Firmenstammdaten`
-- 

-- $Log: db_definition.sql,v $
-- Revision 1.8  2005/11/24 16:50:11  tbayen
-- MwSt- und VSt-Konten in Beschreibung des MWSt-Satzes
--
-- Revision 1.7  2005/09/08 06:27:44  tbayen
-- Buchhaltung.getBilanzkonto() �berarbeitet
--
-- Revision 1.6  2005/08/30 20:16:20  tbayen
-- einige Textfelder verbreitert
--
-- Revision 1.5  2005/08/17 21:13:22  tbayen
-- Erstellungsdatum mit Uhrzeit speichern
--
