<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

<!-- PROJECT LOGO -->
<br />

<h3 align="center">Mastermind app</h3>

  <p align="center">
    A web based mastermind game for you and your friends! 
    <br />
    <a href="https://github.com/JLv10B/Mastermind_Game_2025"><strong>Explore the docs »</strong></a>
    <br />
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#code-structure">Codestructure</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project


The Mastermind app is a web-based implementation of the classic code-breaking game where players attempt to crack a randomly generated number combination, known as the master code. After each guess, the computer provides helpful feedback indicating how many numbers are correct and how many are in the exact right position. Players have 10 attempts to guess the correct combination and win the game.

The application features single-player gameplay against the computer with multiple difficulty levels to choose from. Additionally, it includes leaderboard functionality that tracks and displays the players with the most wins at each difficulty level.

This project focuses on building robust backend infrastructure for a Mastermind game, providing a well-structured API that enables frontend engineers to easily integrate and build user interfaces on top of it. The codebase emphasizes readability and modularity, making it straightforward to debug and extend with additional features. By prioritizing maintainability, the architecture allows new developers to contribute effectively with minimal onboarding time. Beyond the core game mechanics, a competitive scoreboard tracks user wins by difficulty level, adding an engaging element to the classic gameplay. The project also includes a comprehensive API specification that documents all available endpoints for easy integration


<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

**Core Framework:**
* Java 21
* Spring Boot 4.0.0-M1

**Database & Storage:**
* PostgreSQL
* Redis
* H2 Database (testing)

**Documentation & Testing:**
* SpringDoc OpenAPI
* JUnit 5

**Build & Deployment:**
* Maven
* Docker

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
### Prerequisites

* Java 21 or higher - required for Spring Boot 4.0.0-M1
* Maven 3.6+ - Building and managing dependencies
* PostgreSQL - Primary database
* H2 - Testing database
* Redis - Session management
* Docker - Containerization for database services

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/JLv10B/Mastermind_Game_2025.git
   ```
2. Ensure docker is running
3. docker-compose up
4. mvn clean install
5. mvn spring-boot:run
6. Access the Application through your browser: http://localhost:8080

* The application uses the following default ports:
  * Spring Boot: 8080
  * PostgreSQL: 5432
  * Redis: 6379

* API Documentation: http://localhost:8080/swagger-ui.html

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Create a player
* Enter username then click "Continue"

Create Game Room
* Enter a room name and select difficulty then click "Create Room"

Game Play
* Click "Start Game"
* Submit a 4-6 digit number (depending on the difficulty), digits must be 0-7
* Once your guess has been submitted your guess and feedback will populate onto the screen
* The game is completed once you have guessed the master code or you have run out of guesses
* Once the game is completed you can restart the game with a new master code by clicking "Reset Game"
* You can navigate back to the Room Creation page by clicking "Back to Room Creation"


<p align="right">(<a href="#readme-top">back to top</a>)</p>


## Codestructure

### Core Components

- **`/config`** - Application configuration
  - `WebConfig.java` - CORS configuration enabling frontend integration

- **`/controllers`** - API endpoints and request handling
  - `GameController.java` - Game-related API endpoints and template rendering
  - `PlayerController.java` - Player CRUD operations
  - `PlayerScoreController.java` - Scoreboard CRUD operations
  - `RoomController.java` - Room CRUD operations
  
- **`/dto`** - Data transfer objects for client-server communication
  - `PlayerGuessDTO.java` - Player's guess submission
  - `PlayerRoomViewDTO.java` - Room information sent to player (excludes secret code)
  - `RoomCreationDTO.java` - Room creation request data
  - `RoomUpdateDTO.java` - Room update, start, and reset operations
  
- **`/entities`** - JPA entities and data models
  - `Player.java` - Player data
  - `PlayerGuess.java` - Stores player guesses with correct/position feedback
  - `PlayerScore.java` - Player scores by difficulty level
  - `Room.java` - Room configuration, mastercode, and current game progress

- **`/services`** - Business logic and game mechanics
  - `PlayerScoreService.java` -  Tracks and retrieves player win statistics
  - `PlayerService.java` - Manages player accounts and sessions
  - `RoomService.java` - Creates rooms, validates guesses, generates mastercode

- **`/utils`** - Utility classes and constants
  - `AppConstants.java` - Application constants

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- ROADMAP -->
## Roadmap

- [ ] Timed rounds
- [ ] Multiplayer Time trials or other multiplayer modes
- [ ] Time limit on rounds

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

James Liaw- jamesliaw10@gmail.com

Project Link: [https://github.com/JLv10B/Mastermind_Game_2025](https://github.com/JLv10B/Mastermind_Game_2025)

<p align="right">(<a href="#readme-top">back to top</a>)</p>


