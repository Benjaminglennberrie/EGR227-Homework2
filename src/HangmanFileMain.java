// Class HangmanMain is the driver program for the Hangman program.  It reads a
// dictionary of words to be used during the game and then plays a game with
// the user.  This is a cheating version of hangman that delays picking a word
// to keep its options open.  You can change the setting for SHOW_COUNT to see
// how many options are still left on each turn.

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class HangmanFileMain {
    public static final String INPUT_FILE = "test_input.txt";

    private static void runHangman(String DICTIONARY_FILE, boolean SHOW_COUNT,
                                   Scanner consoleMimic, PrintWriter outFile)
            throws FileNotFoundException{
        outFile.println("Welcome to hangman game.");
        outFile.println();

        // open the dictionary file and read dictionary into an ArrayList
        Scanner input = new Scanner(new File(DICTIONARY_FILE));
        List<String> dictionary = new ArrayList<String>();
        while (input.hasNext())
            dictionary.add(input.next().toLowerCase());

        // set basic parameters
        outFile.print("What length word do you want to use? ");
        int length = consoleMimic.nextInt();
        outFile.println(length);
        outFile.print("How many wrong answers allowed? ");
        int max = consoleMimic.nextInt();
        outFile.println(max);
        outFile.println();

        // set up the HangmanManager and start the game
        List<String> dictionary2 = Collections.unmodifiableList(dictionary);
        HangmanManager hangman = new HangmanManager(dictionary2, length, max);
        if (hangman.words().isEmpty()) {
            outFile.println("No words of that length in the dictionary.");
        } else {
            boolean success = playGame(SHOW_COUNT, consoleMimic, outFile, hangman);
            if (success) {
                showResults(hangman, outFile);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        Scanner inputFileScanner = new Scanner(new File(INPUT_FILE));
        while(inputFileScanner.hasNextLine()) {
            String line = inputFileScanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            String actualOutputFileName = "actual-" + lineScanner.next();
            String dictionaryFile = lineScanner.next();
            boolean showCount = lineScanner.nextBoolean();
            PrintWriter outFile = new PrintWriter(new FileWriter(actualOutputFileName));
            runHangman(dictionaryFile, showCount, lineScanner, outFile);
            outFile.close();
        }
    }

    // Plays one game with the user
    public static boolean playGame(boolean SHOW_COUNT, Scanner consoleMimic, PrintWriter outFile, HangmanManager hangman) {
        while (hangman.guessesLeft() > 0 && hangman.pattern().contains("-")) {
            outFile.println("guesses : " + hangman.guessesLeft());
            if (SHOW_COUNT) {
                outFile.println("words   : " + hangman.words().size());
            }
            outFile.println("guessed : " + hangman.guesses());
            outFile.println("current : " + hangman.pattern());
            outFile.print("Your guess? ");
            if(consoleMimic.hasNext()) {
                char ch = consoleMimic.next().toLowerCase().charAt(0);
                outFile.println(ch);
                if (hangman.guesses().contains(ch)) {
                    outFile.println("You already guessed that");
                } else {
                    int count = hangman.record(ch);
                    if (count == 0) {
                        outFile.println("Sorry, there are no " + ch + "'s");
                    } else if (count == 1) {
                        outFile.println("Yes, there is one " + ch);
                    } else {
                        outFile.println("Yes, there are " + count + " " + ch +
                                "'s");
                    }
                }
            }else{
                outFile.println();
                outFile.println("console mimic has no more character!");
                return false;
            }
            outFile.println();
        }
        return true;
    }

    // reports the results of the game, including showing the answer
    public static void showResults(HangmanManager hangman, PrintWriter outFile) {
        // if the game is over, the answer is the first word in the list
        // of words, so we use an iterator to get it
        String answer = hangman.words().iterator().next();
        outFile.println("answer = " + answer);
        if (hangman.guessesLeft() > 0) {
            outFile.println("You beat me");
        } else {
            outFile.println("Sorry, you lose");
        }
    }
}