package atdit_2026.amazing.weather.sentinel._working;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class Sentinel {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final WeatherService WeatherService;
  private final BalloonWarning balloonWarning;

  public Sentinel( ) {
    WeatherService = new WeatherService( );
    balloonWarning = new BalloonWarning( );
  }

  public void run( ) {
    log.debug( "Check for wind warning" );
    var wind = WeatherService.getWind( );

    if( wind > 40 ) {
      log.info( "Wind warning identified: {} km/h", wind );
      String windWarning = "Wind: %d km/h".formatted( wind );
      log.debug( "displaying wind warning message" );
      balloonWarning.issue( "Wind Warning", windWarning, BalloonWarning.MessageType.WARNING );
    } else {
      log.info( "Wind within limits: {} km/h", wind );
      String windMessage = "Wind: %d km/h".formatted( wind );
      log.debug( "displaying wind message" );
      balloonWarning.issue( "Weather within limits", windMessage, BalloonWarning.MessageType.INFO );
    }
  }
}
