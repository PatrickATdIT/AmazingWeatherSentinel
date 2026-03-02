package atdit_2026.amazing.weather.sentinel.version2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherService weatherService;
  private final BalloonWarning balloonWarning;
  private final WindCheck windCheck;

  public Sentinel( ) {
    weatherService = new WeatherService( );
    balloonWarning = new BalloonWarning( );
    windCheck = new WindCheck( );
  }

  public void run( ) {
    log.debug( "Creating Weather Report" );
    var wind = weatherService.getWind( );
    String message = windCheck.checkWind( wind );
    balloonWarning.issue( message );
  }
}
