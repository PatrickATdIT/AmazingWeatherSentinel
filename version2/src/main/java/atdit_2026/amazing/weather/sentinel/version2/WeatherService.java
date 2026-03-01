package atdit_2026.amazing.weather.sentinel.version2;

import atdit_2026.weather.oracle.WeatherOracle;
import atdit_2026.weather.oracle.WeatherOracleFactoryProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class WeatherService {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherOracle;

  public WeatherService( ) {
    log.debug( "Connecting to WeatherOracle Service" );
    weatherOracle = new WeatherOracleFactoryProduction( ).get( );
  }

  public int getWind( ) {
    return weatherOracle.getWind( );
  }
}