package atdit_2026.amazing.weather.sentinel.version4;

public interface WeatherCheck {
  record WeatherParameters( Number wind, Number temperature ) { }

  String check( WeatherParameters weatherParameters );
}