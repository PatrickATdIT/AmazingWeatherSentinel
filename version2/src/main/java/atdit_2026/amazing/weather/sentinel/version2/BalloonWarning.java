package atdit_2026.amazing.weather.sentinel.version2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;

public class BalloonWarning {
  public enum MessageType {
    INFO( TrayIcon.MessageType.INFO ),
    WARNING( TrayIcon.MessageType.WARNING );

    private final TrayIcon.MessageType messageType;

    MessageType( TrayIcon.MessageType messageType ) {
      this.messageType = messageType;
    }

    private TrayIcon.MessageType getMessageType( ) {
      return messageType;
    }
  }

  private static final Logger log = LoggerFactory.getLogger( MethodHandles.lookup( ).lookupClass( ) );
  private final TrayIcon trayIcon;

  public BalloonWarning( ) {
    try {
      log.debug( "preparing tray for balloon message display" );
      trayIcon = makeTrayIcon( );
      SystemTray.getSystemTray( ).add( trayIcon );
    } catch( AWTException e ) {
      log.error( "Error creating tray for balloon message display", e );
      throw new RuntimeException( e );
    }
  }

  public void issue( String caption, String message, MessageType messageType ) {
    log.debug( "issuing balloon warning message" );
    log.debug( "caption: {}", caption );
    log.debug( "message: {}", message );
    log.debug( "messageType: {}", messageType.name( ) );
    trayIcon.displayMessage( caption, message, messageType.getMessageType( ) );
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
