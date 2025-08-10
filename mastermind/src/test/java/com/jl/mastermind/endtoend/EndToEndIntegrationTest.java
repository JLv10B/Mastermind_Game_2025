package com.jl.mastermind.endtoend;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jl.mastermind.dto.PlayerGuessDTO;
import com.jl.mastermind.dto.RoomCreationDTO;
import com.jl.mastermind.dto.RoomUpdateDTO;
import com.jl.mastermind.entities.Player;
import com.jl.mastermind.entities.PlayerGuess;
import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.entities.Room;
import com.jl.mastermind.repositories.PlayerRepository;
import com.jl.mastermind.repositories.PlayerScoreRepository;
import com.jl.mastermind.repositories.RoomRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class EndToEndIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PlayerScoreRepository playerScoreRepository;

        @Autowired
        private PlayerRepository playerRepository;

        @Autowired
        private RoomRepository roomRepository;

        private MockHttpSession mockHttpSession = new MockHttpSession();
        private ObjectMapper objectMapper = new ObjectMapper();
        private String username = "testuser";
        private String roomName = "testroom";
        private int originalDifficulty = 4;
        private String mockMastercode = "7654";

        @Test
        void testCreatePlayer_NewPlayer() throws Exception {
            Player newPlayer = new Player(username);
            mockMvc.perform(post("/players/create-player")
                    .session(mockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPlayer)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.username", is("testuser")));
            

            List<PlayerScore> mockPlayerScoreList = playerScoreRepository.findByUsername(username);
            Optional<PlayerScore>mockEasyScore = playerScoreRepository.findByUsernameAndDifficulty(username, 4);
            Optional<PlayerScore>mockMediumScore = playerScoreRepository.findByUsernameAndDifficulty(username, 5);
            Optional<PlayerScore>mockHardScore = playerScoreRepository.findByUsernameAndDifficulty(username, 6);
            Optional<Player> foundPlayer = playerRepository.getPlayerByName(username);
            
            assertTrue(foundPlayer.isPresent());
            assertEquals(username, foundPlayer.get().getUsername());
            assertEquals(username, mockHttpSession.getAttribute("username").toString());
            assertEquals(3, mockPlayerScoreList.size());
            assertTrue(mockEasyScore.isPresent());
            assertTrue(mockMediumScore.isPresent());
            assertTrue(mockHardScore.isPresent());
            assertEquals(0, mockEasyScore.get().getScore());
            assertEquals(0, mockMediumScore.get().getScore());
            assertEquals(0, mockHardScore.get().getScore());


            //Room creation
            RoomCreationDTO roomCreationDTO = new RoomCreationDTO(roomName, originalDifficulty);
            

            mockMvc.perform(post("/rooms/create-room")
                    .session(mockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(roomCreationDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.room_name", is(roomName)))
                    .andExpect(jsonPath("$.host.username", is(username)))
                    .andExpect(jsonPath("$.difficulty", is(4)))
                    .andExpect(jsonPath("$.started", is(false)))
                    .andExpect(jsonPath("$.completed", is(false)))
                    .andExpect(jsonPath("$.guessList.size()").value(0));
            
            assertEquals(username, mockHttpSession.getAttribute("username").toString());
            
            Optional<Room> roomOptional = roomRepository.findByRoomName(roomName);
            Room room = roomOptional.get();

            assertTrue(roomOptional.isPresent());
            assertEquals(roomName, room.getRoomName());
            assertEquals(username, room.getHost().getUsername());
            assertFalse(room.isStarted());
            assertFalse(room.isCompleted());
            assertEquals(originalDifficulty, room.getDifficulty());
            assertEquals(originalDifficulty, room.getMastercode().length());

            //Start game
            RoomUpdateDTO startRoomUpdateDTO = new RoomUpdateDTO(null, true, null);

            mockMvc.perform(patch("/rooms/{roomName}", roomName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(startRoomUpdateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.room_name", is(roomName)))
                    .andExpect(jsonPath("$.host.username", is(username)))
                    .andExpect(jsonPath("$.difficulty", is(4)))
                    .andExpect(jsonPath("$.started", is(true)))
                    .andExpect(jsonPath("$.completed", is(false)))
                    .andExpect(jsonPath("$.guessList.size()").value(0));

            //Submitting an incorrect guess
            room.setMastercode(mockMastercode);
            PlayerGuessDTO incorrectPlayerGuessDTO = new PlayerGuessDTO("1234");

            mockMvc.perform(post("/rooms/{roomName}/submit-guess", roomName)
                    .session(mockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(incorrectPlayerGuessDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.playerGuess", is("1234")))
                    .andExpect(jsonPath("$.correctGuess", is(false)))
                    .andExpect(jsonPath("$.feedback", is("You guessed 1234. You got 1 correct with 1 in the correct location. You have 9 guesses remaining. Oops, not quite. Try again!")))
                    .andExpect(jsonPath("$.remainingGuesses", is(9)));

            PlayerGuess testIncorrectPlayerGuess = room.getGuessList().get(0);
            
            assertEquals(1, room.getGuessList().size());
            assertEquals("1234", testIncorrectPlayerGuess.getPlayerGuess());
            assertFalse(testIncorrectPlayerGuess.isCorrectGuess());
            assertEquals("You guessed 1234. You got 1 correct with 1 in the correct location. You have 9 guesses remaining. Oops, not quite. Try again!", testIncorrectPlayerGuess.getFeedback());
            assertEquals(9, testIncorrectPlayerGuess.getRemainingGuesses());
            assertTrue(room.isStarted());

            //Submit correct guess
            PlayerGuessDTO correctPlayerGuessDTO = new PlayerGuessDTO(mockMastercode);

            mockMvc.perform(post("/rooms/{roomName}/submit-guess", roomName)
                    .session(mockHttpSession)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(correctPlayerGuessDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.playerGuess", is(mockMastercode)))
                    .andExpect(jsonPath("$.correctGuess", is(true)))
                    .andExpect(jsonPath("$.feedback", is("You guessed 7654. You got 4 correct with 4 in the correct locations. You have 8 guesses remaining. Congratulations, you win!")))
                    .andExpect(jsonPath("$.remainingGuesses", is(8)));

            PlayerGuess testCorrectPlayerGuess = room.getGuessList().get(1);
            
            assertEquals(2, room.getGuessList().size());
            assertEquals(mockMastercode, testCorrectPlayerGuess.getPlayerGuess());
            assertTrue(testCorrectPlayerGuess.isCorrectGuess());
            assertEquals("You guessed 7654. You got 4 correct with 4 in the correct locations. You have 8 guesses remaining. Congratulations, you win!", testCorrectPlayerGuess.getFeedback());
            assertEquals(8, testCorrectPlayerGuess.getRemainingGuesses());
            assertTrue(room.isCompleted());

            //Score Updated
            Optional<PlayerScore> resultEasyPlayerScore = playerScoreRepository.findByUsernameAndDifficulty(username, 4);
            Optional<PlayerScore> resultMediumPlayerScore = playerScoreRepository.findByUsernameAndDifficulty(username, 5);
            Optional<PlayerScore> resultHardPlayerScore = playerScoreRepository.findByUsernameAndDifficulty(username, 6);

            assertEquals(1, resultEasyPlayerScore.get().getScore());
            assertEquals(0, resultMediumPlayerScore.get().getScore());
            assertEquals(0, resultHardPlayerScore.get().getScore());
        }
}
