package atdit_2026.amazing.weather.sentinel.version5;

import atdit_2026.palantair.PalantAir;
import atdit_2026.weather.oracle.WeatherOracle;

public class PalantAir2WeatherOracleAdapter implements WeatherOracle {
  private final PalantAir palantAir;

  public PalantAir2WeatherOracleAdapter( PalantAir palantAir ) {
    this.palantAir = palantAir;
  }

  @Override
  public int getTemperature( ) {
    return palantAir.getAirTemperature( );
  }

  @Override
  public int getWind( ) {
    return palantAir.getWind( );
  }

  @Override
  public int getHumidity( ) {
    throw new UnsupportedOperationException( "Humidity is not supported" );
  }

  @Override
  public int getPrecipitation( ) {
    throw new UnsupportedOperationException( "Precipitation is not supported" );
  }
}
