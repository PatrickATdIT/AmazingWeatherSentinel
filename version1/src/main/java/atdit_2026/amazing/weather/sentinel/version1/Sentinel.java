package atdit_2026.amazing.weather.sentinel.version1;

import atdit_2026.weather.oracle.WeatherOracle;
import atdit_2026.weather.oracle.WeatherOracleFactoryProduction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;


public class Sentinel {

    private static final Logger log = LoggerFactory.getLogger(Sentinel.class);

    public static void main(String[] args) throws AWTException {
        log.info("Starting Amazing Weather Sentinel");

        // get WeatherOracle service
        log.debug("Connecting to WeatherOracle Service");
        WeatherOracle weatherOracle = new WeatherOracleFactoryProduction().get();

        // collect warnings
        log.debug("Collecting weather warnings");
        List<String> warnings = new LinkedList<>();
        if (weatherOracle.getTemperature() > 30) {
            warnings.add("Temperature: %d °C".formatted(weatherOracle.getTemperature()));
            log.debug("Temperature warning added");
        }
        if (weatherOracle.getWind() > 40) {
            warnings.add("Wind: %d km/h".formatted(weatherOracle.getWind()));
            log.debug("Wind warning added");
        }

        // send warning
        if (!warnings.isEmpty()) {
            log.debug("preparing tray for message display");
            // prepare tray for output
            SystemTray tray = SystemTray.getSystemTray();
            URL imageURL = Sentinel.class.getResource("/AWSentinelLogo.png");
            @SuppressWarnings("DataFlowIssue") Image image = new ImageIcon(imageURL).getImage();
            TrayIcon trayIcon = new TrayIcon(image, "AWSentinel");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            // issue warnings
            log.debug("displaying messages");
            String warningMessage = String.join("\n", warnings);
            trayIcon.displayMessage("Weather Warning", warningMessage, TrayIcon.MessageType.WARNING);
        }
    }
}
