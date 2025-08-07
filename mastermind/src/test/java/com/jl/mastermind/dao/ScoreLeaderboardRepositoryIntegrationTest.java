package com.jl.mastermind.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

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
        assertEquals(testScoreLeaderboard, foundLeaderboardList.get(0));
        assertEquals(4, foundLeaderboardList.size());
    }

    // @Test
    // void testUpdate() {
    //     String username = "testuser";
    //     ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard(username, 1, 4);
    //     scoreLeaderboardDAOImpl.create(testScoreLeaderboard);

    //     ScoreLeaderboard testInputScoreLeaderboard = new ScoreLeaderboard(username, 5, 4);
    //     scoreLeaderboardDAOImpl.updateScore(testInputScoreLeaderboard);

    //     Optional<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardDAOImpl.findPlayerScore(username);

    //     assertEquals(5, foundLeaderboard.get().getScore());
    // }

    // @Test
    // void testDelete() {
    //     String username = "testuser";
    //     ScoreLeaderboard testScoreLeaderboard = new ScoreLeaderboard(username, 1, 4);
    //     ScoreLeaderboard testScoreLeaderboard2 = new ScoreLeaderboard("testuser1", 2, 4);
    //     scoreLeaderboardDAOImpl.create(testScoreLeaderboard);
    //     scoreLeaderboardDAOImpl.create(testScoreLeaderboard2);
    //     scoreLeaderboardDAOImpl.deleteScore(username);

    //     List<ScoreLeaderboard> foundLeaderboard = scoreLeaderboardDAOImpl.listByDifficulty_Top10(4);

    //     assertEquals(1, foundLeaderboard.size());
    //     assertEquals(testScoreLeaderboard2, foundLeaderboard.get(0));
    // }


}
