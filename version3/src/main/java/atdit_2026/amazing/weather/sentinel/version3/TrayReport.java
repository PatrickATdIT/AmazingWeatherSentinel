package atdit_2026.amazing.weather.sentinel.version3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;

public class TrayReport implements WeatherReport {
  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final TrayIcon trayIcon;

  public TrayReport( ) {
    try {
      log.debug( "preparing tray for balloon message display" );
      trayIcon = makeTrayIcon( );
      SystemTray.getSystemTray( ).add( trayIcon );
    } catch( AWTException e ) {
      log.error( "Error creating tray for balloon message display", e );
      throw new RuntimeException( e );
    }
  }

  @Override
  public void report( String message ) {
    issue( message );
  }

  private void issue( String message ) {
    log.debug( "issuing balloon warning message" );
    log.debug( "message: {}", message );
    trayIcon.displayMessage( "Weather Report", message, TrayIcon.MessageType.INFO );
  }

  private TrayIcon makeTrayIcon( ) {
    final Image image = loadTrayIcon( );
    final TrayIcon trayIcon = new TrayIcon( image, "AWSentinel" );
    trayIcon.setImageAutoSize( true );
    return trayIcon;
  }

  @SuppressWarnings( "DataFlowIssue" )
  private Image loadTrayIcon( ) {
    URL imageURL = Sentinel.class.getResource( "/AWSentinelLogo.png" );
    if( imageURL == null )
      log.warn( "Unable to load AWSentinelLogo.png" );
    Image image = new ImageIcon( imageURL ).getImage( );
    return image;
  }
}
