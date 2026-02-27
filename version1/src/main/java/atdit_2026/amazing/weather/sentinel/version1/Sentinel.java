package atdit_2026.amazing.weather.sentinel.version1;

import atdit_2026.weather.oracle.WeatherOracle;
import atdit_2026.weather.oracle.WeatherOracleFactoryProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class Sentinel {

  private static final Logger log = LoggerFactory.getLogger( Sentinel.class );

  static void main( ) throws AWTException {
    log.info( "Starting Amazing Weather Sentinel" );

    // get WeatherOracle service
    log.debug( "Connecting to WeatherOracle Service" );
    WeatherOracle weatherOracle = new WeatherOracleFactoryProduction( ).get( );

    // prepare tray for output
    log.debug( "preparing tray for message display" );
    SystemTray tray = SystemTray.getSystemTray( );
    URL imageURL = Sentinel.class.getResource( "/AWSentinelLogo.png" );
    @SuppressWarnings( "DataFlowIssue" ) Image image = new ImageIcon( imageURL ).getImage( );
    TrayIcon trayIcon = new TrayIcon( image, "AWSentinel" );
    trayIcon.setImageAutoSize( true );
    tray.add( trayIcon );

    // check for wind warning
    log.debug( "Check for wind warning" );
    if( weatherOracle.getWind( ) > 40 ) {
      log.debug( "Wind warning identified: {} ", weatherOracle.getWind( ) );
      String windWarning = "Wind: %d km/h".formatted( weatherOracle.getWind( ) );

      // issue warning
      log.debug( "displaying wind warning message" );
      trayIcon.displayMessage( "Wind Warning", windWarning, TrayIcon.MessageType.WARNING );
    }
  }
}
