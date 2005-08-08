README

Dies sind die aus dem Freibier-Projekt von Thomas Bayen und Peter Hormanns
hervorgegangenen Datenbank-Klassen. Sie bieten ein Framework zum Zugriff
auf vorhandene SQL-Datenbanken.



Benutzung in anderen Projekten:

Mittels des bild.xml Ant-Skripts kann eine *.jar-Datei erzeugt werden. Diese
kann dann in ein anderes Projekt kopiert werden. In Eclipse ist es möglich,
beide Projekte im Workspace zu halten. Man kann dann in den Eclipse-Preferences
einstellen, daß das Applikations-Projekt auf dieses Unterprojekt zugreift. Die
jar-Datei selber wird nicht im Projekt in den Classpath-Einstellungen eingebunden.
So ist es z.B. möglich, mit Eclipse wie üblich in die Sourcen des Unterprojektes
zu gehen oder auch die Gesamtapplikation zu debuggen.

Dennoch sollte nach einer Änderung das *.jar-Archiv neu erstellt werden und
in die Applikation kopiert werden. Nur dann kann ein build-Skript dieser
Applikation eine aktuelle Distrinution erzeugen. Hierzu kann man in Eclipse
die *.jar-Datei aus dem dist-Verzeichnis in ein entsprechendes lib-Verzeichnis
der Applikation kopieren und diese dann neu build-en.

Die Bibliothek ist von den im lib-Verzeichnis befindlichen Fremdbibliotheken 
abhängig. Dieses sind also auch in das Applikations-Projekt zu kopieren und
dort in den Classpath einzubinden.
