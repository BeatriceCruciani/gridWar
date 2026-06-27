# 📌 GridWar

## Breve descrizione
GridWar è un RPG tattico a turni in stile Fire Emblem, sviluppato in JavaFX. Gestisci il tuo party, domina la griglia di gioco e guida le tue unità alla vittoria.

---
## 🕹️ Come si gioca

Le meccaniche principali che devi conoscere per iniziare:
- Turni: Il gioco procede a fasi alterne. Durante il tuo turno, puoi muovere le tue unità e impartire ordini.
- Movimento: Ogni unità ha una distanza massima di spostamento basata sulla propria classe.
- Combattimento: Quando un'unità attacca, il risultato dipende dalle statistiche, dalla classe e dall'arma equipaggiata.
- Progressione: Sconfiggi i nemici per ottenere esperienza (EXP). Accumulando abbastanza esperienza, le unità salgono di livello, diventando più forti.
- Condizione di vittoria: Elimina le unità nemiche.

---
## 🎮 Funzionalità principali
- Mappa di battaglia a griglia con terreni differenti (pianura, foresta, montagna, muri distruttibili), ognuno con costi di movimento e bonus difensivi propri.
- Selezione di un'unità con evidenziazione delle celle raggiungibili (movimento) e delle celle attaccabili.
- Movimento, attacco, cura e utilizzo di oggetti dall'inventario, con gestione del turno dell'unità.
- Combattimento a turni con triangolo delle armi, contrattacco del difensore, bonus difensivi del terreno e danno calcolato in base alle statistiche.
- Progressione delle unità tramite esperienza, con level up automatico e bonus statistici per classe.
- IA per il turno delle unità nemiche.
- Condizioni di vittoria/sconfitta a fine turno.
- - Salvataggio e caricamento della partita in formato JSON, con gestione dei salvataggi multipli.
- Interfaccia grafica realizzata con JavaFX.

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- Java 25 (LTS)
- Gradle 

### Istruzioni
```bash
git clone https://github.com/BeatriceCruciani/gridWar.git
cd gridWar
```

### Build del progetto
Da terminale, nella root del repository:
```bash
./gradlew build
```
```
./gradlew run
```

Su Windows:
```bash
.\gradlew.bat build
```
```
.\gradlew.bat run
```

---

## 💾 Persistenza dei dati
Il progetto utilizza una persistenza su file JSON tramite **Gson**. Ogni partita salvata viene memorizzata come file `.json` separato all'interno di una cartella dedicata, identificato dal nome scelto per il salvataggio.

Lo stato salvato include la mappa di battaglia, le unità (con relative statistiche, livello, esperienza, posizione e inventario) e l'avanzamento del turno corrente, permettendo di riprendere la partita esattamente da dove era stata interrotta. La serializzazione/deserializzazione polimorfica degli oggetti dell'inventario (es. armi e consumabili, sottoclassi di `Item`) è gestita tramite un type adapter Gson dedicato, in modo da ricostruire correttamente il tipo concreto di ogni oggetto al caricamento.

Sono supportate anche le operazioni di elenco e cancellazione dei salvataggi esistenti.

---

## 🤖 Uso di strumenti di AI
Durante lo sviluppo è stato utilizzato **Claude (Anthropic)** come supporto alla programmazione, in particolare per:

* **Supporto alla documentazione**
* **Debug e individuazione di bug logici** 
* **Refactoring per evitare duplicazione di codice**
* **Supporto nella comprensione dell'implementazione di alcune funzionalità.**

In ogni caso, il codice proposto dall'AI è stato:
* **letto e compreso** riga per riga prima di essere integrato;
* **discusso e adattato** alla struttura del progetto;
* **testato manualmente** 
* 
L'AI è stata quindi usata come supporto alla comprensione, al debug e al refactoring del codice, non come sostituto della progettazione o della scrittura autonoma del codice.

Per una descrizione più dettagliata dell'uso dell'AI, consultare la **Wiki del repository**.

---

## 📚 Nota sulla Wiki
La Wiki del repository contiene una descrizione più dettagliata delle funzionalità implementate, delle responsabilità delle classi principali (mappa, unità, servizi di movimento/combattimento/turni, controller e rendering della UI) e dei meccanismi previsti per integrare nuove funzionalità.