package atdit_2026.amazing.weather.sentinel.version3;

import atdit_2026.weather.oracle.WeatherOracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherService;
  private final WeatherReport weatherReport;
  private final WeatherCheck weatherCheck;

  public Sentinel( WeatherOracle weatherService, WeatherReport weatherReport, WeatherCheck weatherCheck ) {
    this.weatherService = weatherService;
    this.weatherReport = weatherReport;
    this.weatherCheck = weatherCheck;
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var wind = weatherService.getWind( );
    String message = weatherCheck.check( wind );
    weatherReport.report( message );
  }
}
