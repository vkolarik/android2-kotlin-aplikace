# CarExpense - Android aplikace pro správu nákladů na automobil

## Popis
Android aplikace v Kotlinu pro komplexní správu nákladů na automobil včetně výdajů, plánování údržby a statistik. Aplikace využívá ML Kit pro automatické čtení účtenek, Google Maps pro zobrazení čerpacích stanic a Overpass API pro geografická data.

## Klíčové funkce
- Správa výdajů s kategorizací (palivo, údržba, pojištění, technická kontrola)
- Plánování opakované údržby podle kilometrů nebo času
- Automatické rozpoznávání textu z účtenek pomocí ML Kit
- Statistiky nákladů a výpočet amortizace vozidla
- Integrace s Google Maps pro zobrazení čerpacích stanic
- Generování QR kódů pro sdílení nákladů na cestu

## Technologie
- **Architektura**: MVVM pattern, Jetpack Compose, Hilt DI
- **Databáze**: Room SQLite s Repository pattern, DataStore pro nastavení
- **Síť**: Retrofit, Moshi JSON, OkHttp
- **UI**: Material Design 3, dark/light theme
- **Pokročilé**: ML Kit, CameraX, Google Maps API, Location Services

## Testování
Unit testy, UI testy, repository testy s MockWebServer pro síťové operace.

## Požadavky
Android 8.0+ (API 26), Kotlin 1.8+, Google Play Services
