package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");

        wordleDatabaseConnection.createNewDatabase("words.db");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            System.out.println("Wordle created and connected.");
            // Exercise 5.4.2 part 1 - print game information to the console only
            // if it was being logged, logger.log(Level.INFO,"The words game is working!");
            System.out.println("The words game is working!");


        } else {
            System.out.println("Not able to connect. Sorry!");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            System.out.println("Wordle structures in place.");
        } else {
            System.out.println("Not able to launch. Sorry!");
            return;
        }
        
        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                // System.out.println(line);
                if (line.matches("[a-zA-Z]{4}")){ 
                    wordleDatabaseConnection.addValidWord(i, line);
                    // Exercise 5.4.2 part 4 - logging valid words in data.txt
                    logger.log(Level.FINE, "Valid word read from data.txt: ", line);
                    i++;
                }
                else {
                    // Exercise 5.3.2 - input validation 
                    System.out.println("Unacceptable input!");
                    // Exercise 5.4.2 part 3 - Logging all the invalid words in data.txt
                    logger.log(Level.SEVERE,"Invalid word read from data.txt: ", line);

                }
                
            }
            

        } catch (IOException e) {
            System.out.println("Not able to load . Sorry!");
            // 5.4.2 part 5 - logging exceptions
            logger.log(Level.WARNING, "IOException occurred! The file can't be loaded", e);

            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();

            // Task 5.3.1 - running input validation 
            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");
                if(!guess.matches("[a-z]{4}")){
                    System.out.println("Pls Try Again! Input must be lowercase and four letters long.");
                    // Exercise 5.4.2 part 2 - Invalid guess is logged 
                    logger.log(Level.WARNING, "Invalid guess! Not four letters long and/or lowercase:" + guess);
                }
                else{
                    
                    if (wordleDatabaseConnection.isValidWord(guess)) { 
                        System.out.println("Success! It is in the the list.\n");
                        //logging valid guesses
                        logger.log(Level.FINE, "Valid try: " + guess);
                    }else{
                        System.out.println("Sorry. This word is NOT in the the list.\n");
                        //Exercise 5.4.2 part 2 - logging invalid guesses
                        logger.log(Level.WARNING, "Invalid try! Word is not on the list: " + guess);

                    }
                }
                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            // Exercise 5.4.2 part 5 - continued - logging exceptions
            logger.log(Level.WARNING, "Error! Input could not be read.", e);
            e.printStackTrace();
        }



    }
}