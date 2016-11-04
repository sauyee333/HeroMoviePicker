# Hero Spin Android Mobile App

An Android mobile app that helps to pick a SuperHero Movie.

  - Randomly get a SuperHero via Marvel API (https://developer.marvel.com/)
  - Search for a hero's movie via OMDB API (http://www.omdbapi.com/)
  - Select from a list of heroes before searching for a movie
  - View the movie details

 
## Version

1.0


## Algorithm

  - Get the total number of characters from Marvel API, sorted by last modified since 2006
  - Get a random character from Marvel API out of the total characters
  - Search the movie for this character
  - From the returned movie list, get the random movie and obtain the movie details 


## Limitation

  - OMDB API does not have genre filter, thus the returned movie may not be a SuperHero movie
  - For long hero name, the rate of OMDB API returning "No Movie Found" is hight


## Screenshots
### Main Screens

| ![MovieSpinScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/mainScreen.png)  | ![HeroListScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/heroListScreen.png) | ![MovieDetailScreen](https://github.com/sauyee333/HeroMoviePicker/blob/master/screenshot/movieDetailScreen.png) |
|:---:|:---:|:---:|
| Movie Spin Screen | Hero List Screen | Movie Detail Screen |

### Other Screens

| ![HeroSearchScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/heroSearchScreen.png)  | ![MovieSpinErrorScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/mainErrorScreen.png) | ![MovieDetailErrorScreen](https://github.com/sauyee333/HeroMoviePicker/blob/feature/cleanup/screenshot/movieDetailErrorScreen.png) |
|:---:|:---:|:---:|
| Hero Search Screen | Movie Spin Error Screen | Movie Detail Error Screen |
