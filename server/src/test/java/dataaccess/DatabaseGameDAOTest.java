package dataaccess;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseGameDAOTest {


    String authToken;
    String username = "Joe";
    String gameName = "fun times game";
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new DatabaseGameDAO();
        authToken = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    @DisplayName("Create and get a game")
    public void getCreateGame() throws DataAccessException {
        int gameID = gameDAO.createGame(gameName);
        GameData game = gameDAO.getGame(gameID);
        assertEquals(gameID, game.gameID());
        assertEquals(ChessGame.TeamColor.WHITE, game.game().getTeamTurn());
        assertEquals(new ChessGame(), game.game());
    }

    @Test
    @DisplayName("getGame succeeds")
    public void getGamePositive() {
    }

}
