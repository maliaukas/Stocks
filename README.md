# Stocks
## Description
Mobile application for monitoring stock prices on the stock exchange.

## Technologies
 - **Retrofit** + **Moshi** for API calls
 - **Room** to store cached data locally
 - **Glide** to display images
 - **ViewBinding** with **ViewBindingPropertyDelegate**
 - **Jetpack Navigation** + **SafeArgs** for navigation
 - **Kotlin Flow** + **Coroutines** for asynchronous operations
 - **WorkManager** for repetitive tasks, such as updating data in background
 - **Dagger Hilt** for dependency injection
 - other libraries: **MPAndroidChart** to draw charts, **TextDrawable** for logo placeholders
 - **MVVM** architecture
## Screenshots and functionality
-   The start screen displays a list of stocks. Each stock has a ticker, company name, current price and price change over the day. At the first start of the application, a list of [the most active](https://site.financialmodelingprep.com/developer/docs/most-actives-stock-market-data-free-api) stocks is displayed. Later, this list is extended by the stocks found as a search result.

-   The user can add stocks to favorites and view the favorite list separately.

All stocks | Favorite stocks
:----------:|:------------: 
<img src="https://github.com/maliaukas/Stocks/blob/master/img/list.jpg" alt="All stocks" height="700"/>  |  <img src="https://github.com/maliaukas/Stocks/blob/master/img/favorite.jpg" alt="Favorite stocks" height="700"/>  

-   The user can search stocks by ticker or company name and add them to favorites.

Search with hints | Search result
:----------:|:------------: 
<img src="https://github.com/maliaukas/Stocks/blob/master/img/search.jpg" alt="Search with hints" height="700"/>  |  <img src="https://github.com/maliaukas/Stocks/blob/master/img/search_result.jpg" alt="Search result" height="700"/>  

-   The user can go to a screen with details for a specific stock. This screen displays a price chart for the stock for different time intervals: week, month, half-year, year, all time.

Portrait orientation | Landscape orientation
:----------:|:------------: 
<img src="https://github.com/maliaukas/Stocks/blob/master/img/detail.jpg" alt="Portrait orientation" height="700"/>    |  <img src="https://github.com/maliaukas/Stocks/blob/master/img/detail_land.jpg" alt="Landscape orientation" width="700"/>   

- The user can choose a color theme: light, dark, system default.

Theme switching | Dark theme
:----------:|:------------: 
<img src="https://github.com/maliaukas/Stocks/blob/master/img/theme_switch.jpg" alt="Theme switching" height="700"/>     |  <img src="https://github.com/maliaukas/Stocks/blob/master/img/dark_theme.jpg" alt="Dark theme" height="700"/>  
## Acknowledgements
- [Mockup](https://www.figma.com/file/bfd6MTBekSVfUYBXWYnj1U/%D0%A8%D0%9C%D0%A0-%D0%A2%D0%B5%D1%81%D1%82%D0%BE%D0%B2%D0%BE%D0%B5) provided by [Yandex Mobile School](https://academy.yandex.ru/schools/mobile).
    
- Data provided by  [Financial Modeling Prep](https://financialmodelingprep.com/developer/docs/).
- This project was implemented during the [Android Development Course](https://rs.school/android/) 2021 from [The Rolling Scopes School](https://rs.school/).
