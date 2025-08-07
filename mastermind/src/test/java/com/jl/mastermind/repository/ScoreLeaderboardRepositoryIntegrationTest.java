package com.jl.mastermind.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jl.mastermind.entities.ScoreLeaderboard;
import com.jl.mastermind.repositories.ScoreLeaderboardRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ScoreLeaderboardRepositoryIntegrationTest {

    private ScoreLeaderboardRepository scoreLeaderboardRepository;
 

    @Autowired
    public ScoreLeaderboardRepositoryIntegrationTest(ScoreLeaderboardRepository scoreLeaderboardRepository) {
        this.scoreLeaderboardRepository = scoreLeaderboardRepository;
    }


    @Test
    void testFindPlayerScore() {
        ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard("testuser", 1, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        Optional<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardRepository.findById("testuser");

        assertNotNull(foundLeaderboard);
        assertEquals(testScoreLeaderboard.getUsername(), foundLeaderboard.get().getUsername());
    }

    @Test
    void testScoreLeaderboardCreationGet() {
        ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard("testuser", 1, 4);
        ScoreLeaderboard testScoreLeaderboard2 = new ScoreLeaderboard("testuser1", 2, 4);
        ScoreLeaderboard testScoreLeaderboard3 = new ScoreLeaderboard("testuser2", 3, 4);
        ScoreLeaderboard testScoreLeaderboard4 = new ScoreLeaderboard("testuser3", 4, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        scoreLeaderboardRepository.save(testScoreLeaderboard2);
        scoreLeaderboardRepository.save(testScoreLeaderboard3);
        scoreLeaderboardRepository.save(testScoreLeaderboard4);

        Iterable<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardRepository.findAll();
        List<ScoreLeaderboard> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertNotNull(foundLeaderboard);
        assertTrue(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(4, foundLeaderboardList.size());
    }

    @Test
    void testUpdate() {
        String username = "testuser";
        ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard(username, 1, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);

        ScoreLeaderboard testInputScoreLeaderboard = new ScoreLeaderboard(username, 5, 4);
        scoreLeaderboardRepository.save(testInputScoreLeaderboard);

        Optional<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardRepository.findById(username);

        assertEquals(5, foundLeaderboard.get().getScore());
    }

    @Test
    void testDelete() {
        scoreLeaderboardRepository.deleteAll();
        String username = "testuser";
        ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard(username, 1, 4);
        ScoreLeaderboard testScoreLeaderboard2 = new ScoreLeaderboard("testuser1", 2, 4);
        scoreLeaderboardRepository.save(testScoreLeaderboard);
        scoreLeaderboardRepository.save(testScoreLeaderboard2);
        scoreLeaderboardRepository.deleteById(username);

        Iterable<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardRepository.findAll();
        List<ScoreLeaderboard> foundLeaderboardList = StreamSupport.stream(foundLeaderboard.spliterator(), false).toList();

        assertFalse(foundLeaderboardList.contains(testScoreLeaderboard));
        assertEquals(1, foundLeaderboardList.size());
    }


}