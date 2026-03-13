package atdit_2026.amazing.weather.sentinel.version6;

import atdit_2026.palantair.PalantAir;
import atdit_2026.palantair.PalantAirFactory;
import atdit_2026.palantair.ProductivePalantAirFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static void main() {
        log.info("Starting Amazing Weather Sentinel");

        // PalantAir-Instanz erzeugen
        PalantAirFactory palantAirFactory = new ProductivePalantAirFactory();
        PalantAir palantAir = palantAirFactory.getInstance();

        // Neuen Adapter für den Sentinel erzeugen
        var weatherServiceAdapter = new PalantAir4SentinelAdapter(palantAir);

        // Adapter an den Sentinel übergeben
        var sentinel = new Sentinel(
                weatherServiceAdapter,
                weatherServiceAdapter,
                new TrayReport(),
                List.of(new WindCheck(), new TemperatureCheck(), new WindAndFrostCheck()));
        sentinel.run();
    }
}
