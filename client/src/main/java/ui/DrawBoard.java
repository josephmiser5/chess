package ui;
import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final String AXIS = "  ";


    public static void main(String[] args) {
        drawBoard();
    }

    public static void drawBoard() {
        int size = 10;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (row < 1 || size - row <= 1) {
                    if (col > 0 && col < size - 1) {
                        char file =  (char) ('a' + col - 1);
                        System.out.print(SET_BG_COLOR_DARK_BROWN + SET_TEXT_BOLD);
                        System.out.print(" " + file + " ");
                        System.out.print(RESET_TEXT_BOLD_FAINT);
                        continue;
                    } else {
                        System.out.print(SET_BG_COLOR_DARK_BROWN);
                        System.out.print(EMPTY);
                        continue;
                    }
                } else if (col < 1 || size - col <= 1) {
                    if (row < size - 1) {
                        System.out.print(SET_BG_COLOR_DARK_BROWN + SET_TEXT_BOLD);
                        System.out.print(" " + row + " ");
                        System.out.print(RESET_TEXT_BOLD_FAINT);
                        continue;
                    }
                }
                boolean light = (row + col) % 2 == 0;
                System.out.print(light ? SET_BG_COLOR_BEIGE : SET_BG_COLOR_DARK_GREY);
                System.out.print(EMPTY);
            }
            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }
    }



}
