package ui;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_BOLD;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_YELLOW;

public class PrintStyles {
    public static void printSuccess(String message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message + RESET_TEXT_COLOR);
    }

    public static void printError(String message) {
        System.out.println(SET_TEXT_COLOR_RED + message + RESET_TEXT_COLOR);
    }

    public static void printPrompt(String prompt) {
        System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + prompt
                + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " >>> ");
    }

    public static void printCommand(String command, String description) {
        System.out.println(SET_TEXT_COLOR_YELLOW + command
                + SET_TEXT_COLOR_LIGHT_GREY + " - " + description + RESET_TEXT_COLOR);
    }
}
