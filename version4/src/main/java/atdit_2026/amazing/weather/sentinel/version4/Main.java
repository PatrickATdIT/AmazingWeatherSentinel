package atdit_2026.amazing.weather.sentinel.version4;

import atdit_2026.weather.oracle.WeatherOracleFactoryProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class Main {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );

  static void main( ) {
    log.info( "Starting Amazing Weather Sentinel" );
    var sentinel = new Sentinel(
      new WeatherOracleFactoryProduction( ).get( ),
      new TrayReport( ),
      List.of( new WindCheck( ), new TemperatureCheck( ), new WindAndFrostCheck( ) ) );
    sentinel.run( );
  }
}
