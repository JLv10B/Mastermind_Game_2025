package com.jl.mastermind.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerScoreRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlayerScoreRepositoryIntegrationTest {

    private PlayerScoreRepository scoreLeaderboardRepository;
 

    @Autowired
    public PlayerScoreRepositoryIntegrationTest(PlayerScoreRepository scoreLeaderboardRepository) {
        this.scoreLeaderboardRepository = scoreLeaderboardRepository;
    }


    @Test
    void testFindPlayerScore() {
        PlayerScore testScoreLeaderboard = new PlayerScore("testuser", 1, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        Optional<PlayerScore> foundLeaderboard = scoreLeaderboardRepository.findById("testuser");

        assertNotNull(foundLeaderboard);
        assertEquals(testScoreLeaderboard.getUsername(), foundLeaderboard.get().getUsername());
    }

    @Test
    void testScoreLeaderboardCreationGetAll() {
        PlayerScore testScoreLeaderboard = new PlayerScore("testuser", 1, 4);
        PlayerScore testScoreLeaderboard2 = new PlayerScore("testuser1", 2, 4);
        PlayerScore testScoreLeaderboard3 = new PlayerScore("testuser2", 3, 4);
        PlayerScore testScoreLeaderboard4 = new PlayerScore("testuser3", 4, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        scoreLeaderboardRepository.save(testScoreLeaderboard2);
        scoreLeaderboardRepository.save(testScoreLeaderboard3);
        scoreLeaderboardRepository.save(testScoreLeaderboard4);

        Iterable<PlayerScore> foundLeaderboard = scoreLeaderboardRepository.findAll();
        List<PlayerScore> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertNotNull(foundLeaderboard);
        assertTrue(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(4, foundLeaderboardList.size());
    }

    @Test
    void testUpdate() {
        String username = "testuser";
        PlayerScore testScoreLeaderboard = new PlayerScore(username, 1, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);

        PlayerScore testInputScoreLeaderboard = new PlayerScore(username, 5, 4);
        scoreLeaderboardRepository.save(testInputScoreLeaderboard);

        Optional<PlayerScore> foundLeaderboard = scoreLeaderboardRepository.findById(username);

        assertEquals(5, foundLeaderboard.get().getScore());
    }

    @Test
    void testDelete() {
        scoreLeaderboardRepository.deleteAll();
        String username = "testuser";
        PlayerScore testScoreLeaderboard = new PlayerScore(username, 1, 4);
        PlayerScore testScoreLeaderboard2 = new PlayerScore("testuser1", 2, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        scoreLeaderboardRepository.save(testScoreLeaderboard2);
        scoreLeaderboardRepository.deleteById(username);

        Iterable<PlayerScore> foundLeaderboard = scoreLeaderboardRepository.findAll();
        List<PlayerScore> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertFalse(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(1, foundLeaderboardList.size());
    }


}