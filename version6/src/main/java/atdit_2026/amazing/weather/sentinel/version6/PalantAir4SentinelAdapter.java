package atdit_2026.amazing.weather.sentinel.version6;

import atdit_2026.palantair.PalantAir;

public class PalantAir4SentinelAdapter implements WindProvider, TemperatureProvider {
    private final PalantAir palantair;

    public PalantAir4SentinelAdapter(PalantAir palantAir) {
        this.palantair = palantAir;
    }

    @Override
    public int getTemperature() {
        return palantair.getAirTemperature();
    }

    @Override
    public int getWind() {
        return palantair.getWind();
    }
}
