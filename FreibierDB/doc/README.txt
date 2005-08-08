README

Dies sind die aus dem Freibier-Projekt von Thomas Bayen und Peter Hormanns
hervorgegangenen Datenbank-Klassen. Sie bieten ein Framework zum Zugriff
auf vorhandene SQL-Datenbanken.



Benutzung in anderen Projekten:

Mittels des bild.xml Ant-Skripts kann eine *.jar-Datei erzeugt werden. Diese
kann dann in ein anderes Projekt kopiert werden. In Eclipse ist es m�glich,
beide Projekte im Workspace zu halten. Man kann dann in den Eclipse-Preferences
einstellen, da� das Applikations-Projekt auf dieses Unterprojekt zugreift. Die
jar-Datei selber wird nicht im Projekt in den Classpath-Einstellungen eingebunden.
So ist es z.B. m�glich, mit Eclipse wie �blich in die Sourcen des Unterprojektes
zu gehen oder auch die Gesamtapplikation zu debuggen.

Dennoch sollte nach einer �nderung das *.jar-Archiv neu erstellt werden und
in die Applikation kopiert werden. Nur dann kann ein build-Skript dieser
Applikation eine aktuelle Distrinution erzeugen. Hierzu kann man in Eclipse
die *.jar-Datei aus dem dist-Verzeichnis in ein entsprechendes lib-Verzeichnis
der Applikation kopieren und diese dann neu build-en.

Die Bibliothek ist von den im lib-Verzeichnis befindlichen Fremdbibliotheken 
abh�ngig. Dieses sind also auch in das Applikations-Projekt zu kopieren und
dort in den Classpath einzubinden.
