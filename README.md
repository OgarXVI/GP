# Genetic Programming - symbolic regression

CZ:
Verze 1.0:
Program umožňuje načíst data ze souborů xlsx či cvs na základě zadaných parametrů uživatele nalézt odpovídající popisnou funkci v datech, tedy provede symbolickou regresi.

Příklad vstupních dat: \n
\nX | Y | F
\n1 | 2 | 3
\n2 | 3 | 5

Program data načte jako X,Y terminály, poslední sloupec je považován za výsledný sloupec a používá se pro poorovnání vypočtených vzorců.
Výsledkem programu je co nejpřesnější odpovídající funkce, která se rovná výsledného vztahu posledního sloupce vůči ostatním řádkům.



Mezi podporované terminály patří:
<-5, 5> + proměnné z dat
Mezi podporované funkce patří:
+, -, *, /, sin(), cos(), tg(), abs(), sqrt(), log(), exp(), negation().




Nedostatky:
 - Chybí zobrazení stromového grafu výsledného programu
 - GUI - pravý sloupec s parametry se nesprávně škáluje vůči různým rozlišením
 - Chybí javadoc

