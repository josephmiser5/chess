package dataaccess.sql;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseGameDAOTest {


    String authToken;
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
    @DisplayName("getAllGames")
    public void getAllGames() throws DataAccessException {
        int gameID1 = gameDAO.createGame("checkers");
        int gameID2 = gameDAO.createGame("another game");
        GameData game1 = gameDAO.getGame(gameID1);
        GameData game2 = gameDAO.getGame(gameID2);
        ArrayList<GameData> allGames = (ArrayList<GameData>) gameDAO.getAllGames();
        assertEquals(game1, allGames.getFirst());
        assertEquals(game2, allGames.get(1));
    }

    @Test
    @DisplayName("updateGame")
    public void updateGame() throws DataAccessException, InvalidMoveException {
        int gameID = gameDAO.createGame(gameName);
        GameData game = gameDAO.getGame(gameID);
        assertEquals(new ChessGame(), game.game());

        game.game().applyMove(game.game().getBoard(), new ChessMove(new ChessPosition(2, 1),
                new ChessPosition(3, 1), ChessPiece.PieceType.PAWN));
        gameDAO.updateGame(game);
        assertNotEquals(new ChessGame(), game.game());
    }
}
