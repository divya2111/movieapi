# MovieAPI

This project is a backend movie streaming platform where the administrator can add movies. Users can watch movies by registering and logging themselves. The motive is to provide a seamless experience for users to watch their favorite movies. 

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Features](#features)

## Installation

Follow these steps to download and set up the project on your system:

1. **Clone the repository**:
    ```bash
    git clone https://github.com/divya2111/movieapi.git
    ```
    This command will download the project to your local machine.

2. **Navigate to the project directory**:
    ```bash
    cd movieapi
    ```
    This command will change the current directory to the project's directory.

3. **Install the dependencies**:
    ```bash
    npm install
    ```
    This command will install all the required dependencies for the project.

4. **Start the application**:
    ```bash
    npm start
    ```
    This command will start the application, and you can now access it in your browser.

**Note:** Create the Database "movies" and also give your respected username and password in application properties.

## Usage

Users can register and log in to watch movies. Admins have additional privileges to add new movies to the platform.

## API Endpoints

- `POST   /api/v1/auth/register` - Register a new user.
- `POST   /api/v1/auth/login` - Authenticate a user.
- `POST   /api/v1/movie/add-movie` - Add The Movies (Only for Admins)
- `PUT    /api/v1/movies/update/{movieId}` - Update the movie details (only for Admins)
- `DELETE /api/v1/movies/delete/{moviesId}` - Delete the movie (only for Admins)
- `GET    /api/v1/movies/{moviesId}` - To get particular Movie
- `GET    /api/v1/movies/all` - To get the movies
- `GET    /api/v1/movies/allMoviepage` - To get movies in pages
- `GET    /api/v1/movies/allMoviePageSort` - To get movies in pages and sorted 


## Features

- User registration and login
- Movie streaming
- Admin privileges for managing movies
- Responsive user interface
