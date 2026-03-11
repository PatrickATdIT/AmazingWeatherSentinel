package atdit_2026.amazing.weather.sentinel.version6;

import atdit_2026.weather.oracle.WeatherOracle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SentinelTest {
  @Test
  void verifySentinelOrchestration( ) {
    //assemble
    //festwerte und doubles definieren
    final var weatherParameters = new WeatherCheck.WeatherParameters( 60, 17 );
    final String message = "XXX";
    final String expectedMessage = message + System.lineSeparator( ) + message;

    //erstellen eines stubs für das WeatherOracle, der bei getWind immer den oben definierten wert *wind* liefert.
    var weatherOracleStub = new WeatherOracle( ) {
      @Override
      public int getTemperature( ) {
        return weatherParameters.temperature( ).intValue( );
      }

      @Override
      public int getWind( ) {
        return weatherParameters.wind( ).intValue( );
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
    class WeatherCheckSpy implements WeatherCheck {
      private WeatherParameters receivedWeatherParameters;

      @Override
      public String check( WeatherParameters weatherParameters ) {
        receivedWeatherParameters = weatherParameters;
        return message;
      }
    }
    var weatherCheckSpy1 = new WeatherCheckSpy( );
    var weatherCheckSpy2 = new WeatherCheckSpy( );

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
      List.of( weatherCheckSpy1, weatherCheckSpy2 ) );
    cut.run( );

    //assert
    //die protokollierten werte müssen den definierten festwerten entsprechen
    Assertions.assertEquals( weatherParameters, weatherCheckSpy1.receivedWeatherParameters );
    Assertions.assertEquals( weatherParameters, weatherCheckSpy2.receivedWeatherParameters );
    Assertions.assertEquals( expectedMessage, weatherReportSpy.receivedMessage );
  }
}