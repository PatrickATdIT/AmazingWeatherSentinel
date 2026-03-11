package atdit_2026.amazing.weather.sentinel.version6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class WindCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Wind check" );
    return checkWind( weatherParameters.wind( ).intValue( ) );
  }

  private String checkWind( int wind ) {
    String result;
    if( wind > 40 ) {
      result = "Wind warning: %d km/h".formatted( wind );
    } else {
      result = "Wind within limits: %d km/h".formatted( wind );
    }
    log.info( result );
    return result;
  }
}