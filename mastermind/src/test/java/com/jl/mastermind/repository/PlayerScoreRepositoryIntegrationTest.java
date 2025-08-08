package com.jl.mastermind.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.jl.mastermind.entities.PlayerScore;
import com.jl.mastermind.repositories.PlayerScoreRepository;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class PlayerScoreRepositoryIntegrationTest {

    private PlayerScoreRepository playerScoreRepository;
 
    @Autowired
    public PlayerScoreRepositoryIntegrationTest(PlayerScoreRepository playerScoreRepository) {
        this.playerScoreRepository = playerScoreRepository;
    }


    @Test
    void testFindPlayerScore() {
        PlayerScore testScoreLeaderboard = PlayerScore.builder().username("testuser").score(1).difficulty(4).build();
        playerScoreRepository.save(testScoreLeaderboard);
        List<PlayerScore> foundLeaderboard = playerScoreRepository.findByUsername("testuser");

        assertNotNull(foundLeaderboard);
        assertEquals(1, foundLeaderboard.size());
    }

    @Test
    void testScoreLeaderboardCreationGetAll() {
        PlayerScore testScoreLeaderboard = PlayerScore.builder().username("testuser").score(1).difficulty(4).build();
        PlayerScore testScoreLeaderboard2 = PlayerScore.builder().username("testuser1").score(2).difficulty(4).build();
        PlayerScore testScoreLeaderboard3 = PlayerScore.builder().username("testuser2").score(3).difficulty(4).build();
        PlayerScore testScoreLeaderboard4 = PlayerScore.builder().username("testuser3").score(4).difficulty(4).build();
        playerScoreRepository.save(testScoreLeaderboard);
        playerScoreRepository.save(testScoreLeaderboard2);
        playerScoreRepository.save(testScoreLeaderboard3);
        playerScoreRepository.save(testScoreLeaderboard4);

        Iterable<PlayerScore> foundLeaderboard = playerScoreRepository.findAll();
        List<PlayerScore> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertNotNull(foundLeaderboard);
        assertTrue(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(4, foundLeaderboardList.size());
    }

    @Test
    void testUpdate() {
        String username = "testuser";
        PlayerScore testScoreLeaderboard = PlayerScore.builder().username(username).score(1).difficulty(4).build();
        playerScoreRepository.save(testScoreLeaderboard);

        Optional<PlayerScore> testInputScoreLeaderboard = playerScoreRepository.findByUsernameAndDifficulty(username, 4);
        testScoreLeaderboard = testInputScoreLeaderboard.get();
        testScoreLeaderboard.setScore(5);
        playerScoreRepository.save(testScoreLeaderboard);

        Optional<PlayerScore> foundLeaderboard = playerScoreRepository.findByUsernameAndDifficulty(username, 4);

        assertEquals(5, foundLeaderboard.get().getScore());
    }

    @Test
    void testDelete() {
        String username = "testuser";
        PlayerScore testScoreLeaderboard = PlayerScore.builder().username(username).score(1).difficulty(4).build();
        PlayerScore testScoreLeaderboard2 = PlayerScore.builder().username("testuser2").score(2).difficulty(4).build();
        playerScoreRepository.save(testScoreLeaderboard);
        playerScoreRepository.save(testScoreLeaderboard2);
        playerScoreRepository.deleteByUsername(username);

        Iterable<PlayerScore> foundLeaderboard = playerScoreRepository.findAll();
        List<PlayerScore> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertFalse(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(1, foundLeaderboardList.size());
    }


}