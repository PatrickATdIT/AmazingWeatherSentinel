1. Single Responsibility  
   Klassen ableiten

```mermaid
classDiagram
    namespace service1 {
        class WeatherOracle {
            <<interface>>
            +getTemperature() int
            +getWind() int
            +getHumidity() int
            +getPrecipitation() int
        }
        class WeatherOracleFactory {
            <<interface>>
            +get() WeatherOracle
        }
        class WeatherOracleProduction {
        }
        class WeatherOracleFactoryProduction {
        }
    }
    WeatherOracleFactoryProduction ..> WeatherOracleProduction: «creates»
    WeatherOracleProduction ..|> WeatherOracle
    WeatherOracleFactoryProduction ..|> WeatherOracleFactory
    WeatherOracleFactory ..> WeatherOracle

    namespace AmazingWeatherSentinel {
        class Main {
            +main()$
        }

        class Sentinel {
            +run()
        }

        class BalloonWarning {
            +addWarning(String warning)
            +issue()
        }
        
        class WindCheck {
            +check() String
        }
    }

    Main ..> Sentinel
    Sentinel ..> WeatherOracle
    Sentinel ..> WeatherOracleFactoryProduction
    Sentinel ..> WindCheck
    Sentinel ..> BalloonWarning

```

2. Dependency Inversion  
   Im nächsten Schritt werden die Dependencies ausgetauscht umgekehrt; die dependencies werde von der Main-Methode
   zusammengefügt. Das lässt sich jetzt auch unittesten.

```mermaid
classDiagram
    namespace service1 {
        class WeatherOracle {
            <<interface>>
            +getTemperature() int
            +getWind() int
            +getHumidity() int
            +getPrecipitation() int
        }
        class WeatherOracleFactory {
            <<interface>>
            +get() WeatherOracle
        }
        class WeatherOracleProduction {
        }
        class WeatherOracleFactoryProduction {
        }
    }
    WeatherOracleFactoryProduction ..> WeatherOracleProduction: «creates»
    WeatherOracleProduction ..|> WeatherOracle
    WeatherOracleFactoryProduction ..|> WeatherOracleFactory
    WeatherOracleFactory ..> WeatherOracle

    namespace AmazingWeatherSentinel {
        class Main {
            +main()$
        }

        class Sentinel {
            +Sentinel(WeatherOracle, WeatherCheck, Warning)
            +run()
        }

        class Warning {
            <<interface>>
            +addWarning(String warning)
            +issue()
        }

        class BalloonWarning {
        }

        class WindCheck {
            <<interface>>
            +check() String
        }

        class WeatherCheck {
        }
    }
    Warning <|.. BalloonWarning
    WeatherCheck <|.. WindCheck
    Main ..> BalloonWarning: «creates»
    Main ..> WindCheck: «creates»
    Main ..> WeatherOracleFactoryProduction
    Main ..> WeatherOracle
    Main ..> Sentinel
    Sentinel ..> WeatherOracle
    Sentinel ..> WeatherCheck
    Sentinel ..> Warning
```

3. Open-Closed
Wir erstellen eine neue Klasse WeatherRules mit Schnittstelle
