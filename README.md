# CarExpense - Aplikace pro správu nákladů na automobil

## 📱 O aplikaci

**CarExpense** je komplexní Android aplikace napsaná v Kotlinu pro správu a sledování všech nákladů spojených s vlastnictvím automobilu. Aplikace umožňuje uživatelům efektivně spravovat výdaje, plánovat údržbu a sledovat statistiky nákladů.

## ✨ Klíčové funkce

### 💰 Správa výdajů
- **Kategorizace výdajů**: Palivo, údržba, pojištění, technická kontrola, parkování, splátky a další
- **Automatické čtení účtenek**: Použití ML Kit pro rozpoznávání textu z fotografií účtenek
- **Historie výdajů**: Kompletní přehled všech zaznamenaných nákladů

### 🛠️ Plánování údržby
- **Opakované úkony**: Výměna oleje, kontrola brzd, výměna filtrů, geometrie a další
- **Dvojí plánování**: Podle kilometrů nebo času
- **Upozornění**: Automatické notifikace o nadcházejících úkonech

### 📊 Statistiky a analýzy
- **Roční přehledy**: Celkové náklady, průměrné náklady na kilometr
- **Amortizace**: Automatický výpočet roční amortizace vozidla
- **Grafy a trendy**: Vizuální zobrazení vývoje nákladů

### 🗺️ Integrované mapy
- **Čerpací stanice**: Zobrazení nejbližších čerpacích stanic pomocí Google Maps
- **Overpass API**: Integrace s OpenStreetMap pro aktuální data

### 💳 Platební funkce
- **Rozpočítání nákladů**: Sdílení nákladů na cestu mezi více lidmi
- **QR kódy**: Generování QR kódů pro platby
- **Bankovní účty**: Validace českých bankovních čísel

### 📏 Sledování nájezdu
- **Historie tachometru**: Zaznamenávání stavu tachometru v čase
- **Automatické výpočty**: Průměrný roční nájezd a související statistiky

## 🛠️ Technologie

### Architektura
- **MVVM Pattern**: Model-View-ViewModel architektura
- **Jetpack Compose**: Moderní deklarativní UI framework
- **Hilt**: Dependency injection framework
- **Navigation Component**: Řízení navigace v aplikaci

### Databáze a ukládání
- **Room Database**: Lokální SQLite databáze s ORM
- **DataStore**: Ukládání nastavení a preferencí
- **Repository Pattern**: Abstrakce datových zdrojů

### Síťové komunikace
- **Retrofit**: HTTP klient pro API komunikaci
- **Moshi**: JSON serializace/deserializace
- **OkHttp**: HTTP stack s podporou intercepting

### UI/UX
- **Material Design 3**: Moderní design systém
- **Dark/Light Theme**: Podpora obou témat
- **Responsive Design**: Adaptivní rozhraní pro různé velikosti obrazovek

### Pokročilé funkce
- **ML Kit**: Rozpoznávání textu z obrázků
- **CameraX**: Pokročilá práce s kamerou
- **Google Maps**: Integrace mapových služeb
- **Location Services**: Práce s GPS a polohou

## 🧪 Testování

Aplikace obsahuje komplexní testovací pokrytí:

- **Unit testy**: Testování business logiky a validací
- **UI testy**: Automatizované testování uživatelského rozhraní
- **Repository testy**: Testování datových vrstev
- **MockWebServer**: Testování síťových operací

## 📱 Požadavky

- **Android 8.0+** (API level 26)
- **Kotlin 1.8+**
- **Google Play Services** (pro mapy a ML Kit)

## 🚀 Instalace

1. Klonujte repository
2. Otevřete projekt v Android Studio
3. Nakonfigurujte `local.properties` s potřebnými API klíči
4. Sestavte a spusťte aplikaci

## 📋 Funkce pro CV

Tento projekt demonstruje:

- **Pokročilé Android vývojářské dovednosti** v Kotlinu
- **Moderní architekturní vzory** (MVVM, Repository, DI)
- **Integrace třetích služeb** (Google Maps, ML Kit, Overpass API)
- **Komplexní databázové operace** s Room
- **Testování** na všech úrovních aplikace
- **Material Design** a moderní UI/UX
- **Asynchronní programování** s Coroutines
- **Správa stavu** aplikace
- **Lokalizace** (čeština/angličtina)

## 🎯 Cíl projektu

Aplikace byla vyvinuta jako semestrální projekt pro demonstraci pokročilých Android vývojářských technik a moderních přístupů k vývoji mobilních aplikací. Kombinuje praktické užitečné funkce s technickou excelencí.

---

*Projekt vytvořen jako součást studia na Mendelově univerzitě v Brně*
