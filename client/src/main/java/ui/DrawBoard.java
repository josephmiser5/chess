package ui;
import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final String AXIS = "  ";

    public static void main(String[] args) {
        drawBoard();
    }

    public static void drawBoard() {
        int size = 16;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                boolean light;
                if (row % 2 == 0) {
                    light = (row + col) % 4 == 2 || (row + col) % 4 == 3;
                } else {
                    light = (row + 1 + col) % 4 == 0 || (row + 1 + col) % 4 == 1;
                }
                System.out.print(light ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_BLUE);
                System.out.print(EMPTY);
            }
            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }
    }



}
