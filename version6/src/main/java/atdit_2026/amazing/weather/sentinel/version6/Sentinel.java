package atdit_2026.amazing.weather.sentinel.version6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;


public class Sentinel {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final WindProvider windProvider;
    private final TemperatureProvider temperatureProvider;
    private final WeatherReport weatherReport;
    private final List<WeatherCheck> weatherChecks;

    public Sentinel(WindProvider windProvider, TemperatureProvider temperatureProvider,
                    WeatherReport weatherReport, List<WeatherCheck> weatherChecks) {
        this.windProvider = windProvider;
        this.temperatureProvider = temperatureProvider;
        this.weatherReport = weatherReport;
        this.weatherChecks = weatherChecks;
    }

    public void run() {
        log.debug("Creating Weather Report");
        var weather = new WeatherCheck.WeatherParameters(
                windProvider.getWind(),
                temperatureProvider.getTemperature());
        var message = executeWeatherChecks(weather);
        weatherReport.report(message);
    }

    private String executeWeatherChecks(WeatherCheck.WeatherParameters weather) {
        List<String> results = new LinkedList<>();
        for (var wc : weatherChecks) {
            var checkResult = wc.check(weather);
            results.add(checkResult);
        }
        return String.join(System.lineSeparator(), results);
    }
}
