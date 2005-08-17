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
-- Tabellenstruktur für Tabelle `Buchungen`
-- 

CREATE TABLE `Buchungen` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Journal` int(10) unsigned NOT NULL default '0',
  `Belegnummer` char(8) NOT NULL default '',
  `Buchungstext` char(20) NOT NULL default '',
  `Valutadatum` date NOT NULL default '0000-00-00',
  `Erfassungsdatum` date NOT NULL default '0000-00-00',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `Buchungen`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Buchungszeilen`
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
-- Daten für Tabelle `Buchungszeilen`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Journale`
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
-- Daten für Tabelle `Journale`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Konten`
-- 

CREATE TABLE `Konten` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Kontonummer` char(8) NOT NULL default '',
  `Bezeichnung` char(20) NOT NULL default '',
  `MwSt` int(1) unsigned default NULL,
  `Oberkonto` int(10) unsigned default NULL,
  `Gewicht` int(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `Konten`
-- 


-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `MwSt`
-- 

CREATE TABLE `MwSt` (
  `id` int(10) NOT NULL auto_increment,
  `MwStSatz` decimal(3,1) NOT NULL default '0.0',
  `Bezeichnung` char(15) NOT NULL default '',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=4 ;

-- 
-- Daten für Tabelle `MwSt`
-- 

INSERT INTO `MwSt` VALUES (1, 0.0, 'keine');
INSERT INTO `MwSt` VALUES (2, 7.0, 'vermindert');
INSERT INTO `MwSt` VALUES (3, 16.0, 'voll');



-- --------------------------------------------------------

-- 
-- Tabellenstruktur für Tabelle `Firmenstammdaten`
-- 

CREATE TABLE `Firmenstammdaten` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `Firma` varchar(30) NOT NULL default '',
  `Bilanzkonto` int(10) unsigned NOT NULL default '0',
  `JahrAktuell` char(4) NOT NULL default '2005',
  `PeriodeAktuell` char(2) NOT NULL default '01',
  PRIMARY KEY  (`id`)
) TYPE=InnoDB AUTO_INCREMENT=1 ;

-- 
-- Daten für Tabelle `Firmenstammdaten`
-- 
