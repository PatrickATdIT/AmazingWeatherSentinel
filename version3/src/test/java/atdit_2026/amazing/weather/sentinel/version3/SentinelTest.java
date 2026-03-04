package atdit_2026.amazing.weather.sentinel.version3;

import atdit_2026.weather.oracle.WeatherOracle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SentinelTest {
  @Test
  void verifySentinelOrchestration( ) {
    //assemble
    //festwerte und doubles definieren
    final int wind = 60;
    final String message = "XXX";

    //erstellen eines stubs für das WeatherOracle, der bei getWind immer den oben definierten wert *wind* liefert.
    var weatherOracleStub = new WeatherOracle( ) {
      @Override
      public int getTemperature( ) {
        throw new UnsupportedOperationException( "Method not implemented" );
      }

      @Override
      public int getWind( ) {
        return wind;
      }

      @Override
      public int getHumidity( ) {
        throw new UnsupportedOperationException( "Method not implemented" );
      }

      @Override
      public int getPrecipitation( ) {
        throw new UnsupportedOperationException( "Method not implemented" );
      }
    };

    //erstellen eines spys für WeatherCheck, der den empfangenen windwert protokolliert
    //und die oben definierte *message* zurückliefert
    var weatherCheckSpy = new WeatherCheck( ) {
      private Number receivedWind;

      @Override
      public String check( Number n ) {
        receivedWind = n;
        return message;
      }
    };

    //erstellen eines spys für WeatherReport, der die empfangene nachricht protokolliert
    var weatherReportSpy = new WeatherReport( ) {
      private String receivedMessage;

      @Override
      public void report( String msg ) {
        receivedMessage = msg;
      }
    };

    //act
    //einen Sentinel mit den doubles erstellen und laufen lassen
    var cut = new Sentinel(
      weatherOracleStub,
      weatherReportSpy,
      weatherCheckSpy );
    cut.run( );

    //assert
    //die protokollierten werte müssen den definierten festwerten entsprechen
    Assertions.assertEquals( wind, weatherCheckSpy.receivedWind );
    Assertions.assertEquals( message, weatherReportSpy.receivedMessage );
  }
}