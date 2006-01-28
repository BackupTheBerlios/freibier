;Update von WebDatabase 1.5 auf BayenBanking 1.6:
;
;Die Tabellenstruktur hat sich leicht ge�ndert, deshalb muss eine neue
;Datenbank angelegt werden. Die folgenden Befehle k�nnen benutzt werden,
;um aus einer vorhandenen Datenbank "banking" eine neue Datenbank
;"banking16" zu machen.
;
; danach kann man in /etc/freibierweb/banking.properties eine Zeile
; "database.name=banking16" einf�gen und fertig!

per PHPMyAdmin:
- Datenbank "banking16" anlegen
- Benutzerrechte erteilen
- Datenbank "banking" per SQL-Export und SQL-Import kopieren


alter table Transaktionen change column blz BLZ char(8);
alter table Transaktionen add column Zahlungsart int after Ausgangskorb;

update Transaktionen, Ausgangskoerbe 
  set Transaktionen.Zahlungsart=Ausgangskoerbe.Zahlungsart
  where Transaktionen.Ausgangskorb=Ausgangskoerbe.id;
