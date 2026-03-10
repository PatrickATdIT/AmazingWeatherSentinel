package atdit_2026.amazing.weather.sentinel.version5;

import atdit_2026.weather.oracle.WeatherOracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;


public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherOracle weatherService;
  private final WeatherReport weatherReport;
  private final List<WeatherCheck> weatherChecks;

  public Sentinel( WeatherOracle weatherService, WeatherReport weatherReport, List<WeatherCheck> weatherChecks ) {
    this.weatherService = weatherService;
    this.weatherReport = weatherReport;
    this.weatherChecks = weatherChecks;
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var weather = new WeatherCheck.WeatherParameters(
      weatherService.getWind( ),
      weatherService.getTemperature( ) );
    var message = executeWeatherChecks( weather );
    weatherReport.report( message );
  }

  private String executeWeatherChecks( WeatherCheck.WeatherParameters weather ) {
    List<String> results = new LinkedList<>( );
    for( var wc : weatherChecks ) {
      var checkResult = wc.check( weather );
      results.add( checkResult );
    }
    return String.join( System.lineSeparator( ), results );
  }
}
