package atdit_2026.amazing.weather.sentinel.version6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class WindAndFrostCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Wind/Frost check" );

    final var wind = weatherParameters.wind( ).intValue( );
    final var temp = weatherParameters.temperature( ).intValue( );
    String result;

    if( wind > 30 && temp < 0 ) {
      result = "Wind/Frost warning: %d km/h and %d °C ".formatted( wind, temp );
    } else {
      result = "Wind/Frost indicators within limits: %d km/h and %d °C ".formatted( wind, temp );
    }
    log.info( result );

    return result;
  }
}