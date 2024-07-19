# User Service<p align="center">
<img src="https://img.shields.io/badge/Version-1.0.0-blue" alt="Version">

This is a simple user service of my project. 

The project is written in Spring Boot and uses a MongoDB database.

The user data includes:
- Personal information: name, email, phone number, date of birth
- Marketplaces that the user registered.

The marketplaces are:
- Xbox
- Playstation
- Steam

The marketplace data related to the user includes:
- Username
- Token
- Date of registration
- Date of last update
- Level
- Games that the user has

The game data related to the user includes:
- Name
- Genres
- Thumbnail
- Achievements

The achievement data related to the user includes:
- Name
- Description
- Date of achievement
- Thumbnail
- State (locked or unlocked)

The project has the following endpoints:
- User:
  - Create a user
  - Get all users
  - Get a user by id
  - Update a user
  - Delete a user

- Marketplace:
  - Register a marketplace account to a user
  - Get all accounts of a user
  - Update an account of a user
  - Delete an account of a user

- Game:
  - Get all games of a user 
  - Add a game to a marketplace user account
  - Get all games of a marketplace user account
  - Update a game of a marketplace user account
  - Delete a game of a marketplace user account

- Achievement:
  - Get all achievements of a user
  - Add an achievement to a game of the user
  - Get all game achievements of the user
  - Update a game achievement of the user
  - Delete a game achievement of the user