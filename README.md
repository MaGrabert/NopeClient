# NopeClient
 
# Kurzbeschreibung

    Nopeclient ist eine Semester Projekt, in dem eine KI das Kartenspiel Nope
    gegen andere KIs spielen soll. Die anderen KI sind in unterschiedlichen
    Computer verteilt, so das eine Verbindung über einen Server nötig ist. Diese
    Verbindung wird mit HTTP Anfragen und Sockets realisiert. Die KI reagiert,
    sobald sie von dem Server auf einen Zug angesprochen wird und versucht
    daraufhin mit der oder den richtigen Karten zu antworten.

# Installation 

    Die Software nutzt das Buildtool Gradle, welches alle Abhängigkeiten in der
    build.gradle.kts stehen hat. 
    
# Verwendung 

    Wenn das Projekt in einer Entwicklungsumgebung gestartet wird, dann erscheint
    eine Login-Fenster und darin enthalten die Möglichkeit auf das 
    Registrierungsfenster zu wechseln. Zunächst muss ein Profil angelegt werden,
    mit dem ein Login möglich ist. Nach dem Login erscheinen alle offenen Matches 
    zu welchen man beitreten kann, wenn man diese mit der Maus auswählt und 
    beitritt. Das erstellen eines Matches kann über den Knopf unterhalb des 
    Fensters ermöglicht werden. Nach dem drücken erscheint ein Popup Fenster in
    welches die zu spielenden Matches eingetragen werden muss. Nach der
    Bestätigung befindet man sich in dem Match, welches der Host aktivieren kann.
    Man kann das Match mit dem Knopf am ende des Fensters verlassen und die
    Verbindung des Sockets trennen in dem man oben über die Navigation diese 
    Beendet.   

# Hinweise 
    
    Ein Match kann nur gespielt werden, wenn mindestens zwei Spieler in dem Match 
    sind. 
    Alle informationen werden in JSON-Format über die Konsole einer 
    Entwicklungsumgebung oder ähnlichen ausgegeben.  
    
# Struktur
    
    |
    |—-app
    |   |—-MyApp.kt
    |   |—-Profile.kt
    |   |—-Styles.kt
    |—-game
    |   |—-AI.kt
    |   |—-Card.kt
    |   |—-Player.kt
    |   |—-Tournament.kt
    |   |—-TournamentInfo.kt
    |—-socket
    |   |—-HTTPClient.kt
    |   |—-SocketHandler.kt
    |—-view
    |   |—-MainView.kt
    |   |—-PopOutView.kt
    |   |—-ProfileView.kt
    |   |—-SignInView.kt
    |   |—-SignUpView.kt
    |   |—-TournamentView.kt
