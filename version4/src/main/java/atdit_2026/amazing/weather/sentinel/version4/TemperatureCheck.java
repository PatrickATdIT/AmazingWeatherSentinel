package atdit_2026.amazing.weather.sentinel.version4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class TemperatureCheck implements WeatherCheck {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  @Override
  public String check( WeatherParameters weatherParameters ) {
    log.info( "Temperature check" );

    String result;
    final var temp = weatherParameters.temperature( ).intValue( );

    if( temp > 30 )
      result = "Temperature warning: %d °C".formatted( temp );
    else
      return "Temperature within limits: %d °C".formatted( temp );

    log.info( result );
    return result;
  }
}