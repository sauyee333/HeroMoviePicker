# Hero Spin Android Mobile App

An Android mobile app that helps to pick a SuperHero Movie.

  - Randomly get a SuperHero via Marvel API (https://developer.marvel.com/)
  - Search for a hero's movie via OMDB API (http://www.omdbapi.com/)
  - Select from a list of heroes before searching for a movie
  - View the movie details

 
## Version

1.0


## Algorithm

#### Random Movie
  1. Get a small list of characters from Marvel API in order to obtain the total characters available
  2. Get a random character from Marvel API out of the total characters
  3. Search movies for the chosen character
  4. From the returned movie list, choose a random movie and obtain the movie details 

#### Select Hero
  1. Show the list of heroes, sorted by last modified
  2. Allow user to scroll for more, seach by name and select a hero
  3. Search movies for the chosen character
  4. From the returned movie list, choose a random movie and obtain the movie details 


## Limitation

  - OMDB API does not have genre filter, thus the returned movie may not be a SuperHero movie
  - For long hero string or hero that is created some time back, OMDB API tends to "Movie Not Found"


## Screenshots
#### Main Screens

| ![MovieSpinScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/mainScreen.png)  | ![HeroListScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/heroListScreen.png) | ![MovieDetailScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/movieDetailScreen.png) |
|:---:|:---:|:---:|
| Movie Spin Screen | Hero List Screen | Movie Detail Screen |

#### Other Screens

| ![HeroSearchScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/heroSearchScreen.png)  | ![MovieSpinErrorScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/mainErrorScreen.png) | ![MovieDetailErrorScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/movieDetailErrorScreen.png) |
|:---:|:---:|:---:|
| Hero Search Screen | Movie Spin Error Screen | Movie Detail Error Screen |
