[![Codacy Badge](https://api.codacy.com/project/badge/Grade/9d25900626ea40e2bd371c9668e0e41a)](https://app.codacy.com/app/quetza1coatl/minsk-infobot?utm_source=github.com&utm_medium=referral&utm_content=quetza1coatl/minsk-infobot&utm_campaign=Badge_Grade_Dashboard)
## Minsk information Telegram bot (@MinskInfoBot)

### Desciption
A simple bot that provides information about:  
-  exchange rates
-  weather forecast
-  news
-  movies in cinemas.  
  
Based on parsing json, rss, html, uses caching.  
Designed for the Republic of Belarus, and Minsk in particular, but can be easily adapted to a specific location
by analogy or through a change of parameters.  
 
## Authorization data
The bot uses personalized data necessary for the operation of the bot itself and part of its functions.
This data is loaded into the application as environment variables.   
To start the bot, you need to register your new bot via Telegram bot @FatherBot, get the name and bot token and use the
data in the methods `MinskInfoBot#getBotUsername` and `MinskInfoBot#getBotToken` instead of an existing reference to
 the environment variable.  
For the weather service to work, you need to get a token at the [weather service](https://openweathermap.org/)
and add this token to the variable `WeatherForecastHandlerImpl.WEATHER_URL` instead of an existing reference
 to the environment variable.
 