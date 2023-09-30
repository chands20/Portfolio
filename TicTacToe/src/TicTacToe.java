import java.util.ArrayList;
import java.util.Scanner;


public class TicTacToe {
    static char emptySpaceSymbol = ' ';
    static char playerOneSymbol = 'X';
    static char playerTwoSymbol = 'O';

    public static void main(String[] args) {

        //Loop will run until gameOver = true
        boolean gameOver = false;

        //Scanner in
        Scanner in = new Scanner(System.in);

        //ArrayList to save game history
        ArrayList<char[][]> gameHistory = new ArrayList<char[][]>();

        // Initialize player names
        String[] playerNames = new String[2];

        while(gameOver == false)
        {
            //Prompt user input, provide options
            System.out.println("Welcome to game of Tic Tac Toe, choose one of the following options from below: ");
            System.out.println();
            System.out.println("1. One player game");
            System.out.println("2. Two player game");
            System.out.println("D. Display last match");
            System.out.println("Q. Quit");
            System.out.print("What do you want to do: ");
            String choice = in.next();
            System.out.println();

            if(choice.equals("1")) //run one player game
            {
                System.out.print ("Enter player 1 name: "); //prompt user input
                playerNames[0] = in.next(); //take player name
                playerNames[1] = "Computer";

                gameHistory = runOnePlayerGame(playerNames);
            }
            else if(choice.equals("2")) // run two player game
            {
                System.out.print("Enter player 1 name: "); //take player1 name
                playerNames[0] = in.next(); //prompt user input

                System.out.print("Enter player 2 name: "); //take player2 name
                playerNames[1] = in.next(); //prompt user input

                gameHistory = runTwoPlayerGame(playerNames);
            }
            else if(choice.equals("D") || choice.equals("d"))
            {
                //check if gameHistory is empty
                if(gameHistory.size() == 0)
                    System.out.println("No match found\n");
                else
                    runGameHistory(playerNames,gameHistory); // if not empty, run gameHistory
            }
            else if(choice.equals("Q") || choice.equals("q"))
                gameOver = true; //game ends, will exit loop
            else
                System.out.println("Invalid option\n"); //invalid input
        }

        System.out.println("Thanks for playing. Hope you had fun!"); //Exit message
    }

    /**
     * Simulates flipping a coin
     * @return 1 or 0 (heads, or tails)
     */
    public static int flipCoin() {
        double rand = Math.random();
        if (rand < 0.5)
            return 0;
        else
            return 1;
    }

    /**
     * Given a state, return a String which is the textual representation of the tic-tac-toe board at that state.
     * @param state the 2d array of the current game positions
     * @return String
     */
    private static String displayGameFromState(char[][] state) {

        System.out.println(" " + state[0][0] + " | " + state[0][1] + " | " + state[0][2] + " \n" + //top row
                "-----------\n" +
                " " + state[1][0] + " | " + state[1][1] + " | " + state[1][2] + " \n" + //middle row
                "-----------\n" +
                " " + state[2][0] + " | " + state[2][1] + " | " + state[2][2] + " \n"); //bottom row

        return null;
    }

    /**
     * Returns the state of a game that has just started.
     * @return a 2d array of the initial game state (an empty board)
     */
    private static char[][] getInitialGameState() {
        return new char[][]{
                {emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol},
                {emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol},
                {emptySpaceSymbol, emptySpaceSymbol, emptySpaceSymbol}};
    }

    /**
     * Runs a two player game
     * @param playerNames
     * @return an ArrayList of game states of each turn -- in other words, the gameHistory
     */
    private static ArrayList<char[][]> runTwoPlayerGame(String[] playerNames) {

        char[][] currentState = getInitialGameState(); // gives empty board
        ArrayList<char[][]> gameHistory = new ArrayList<char[][]>(); //will record game history

        //decides who is player 1 and who is player 2
        System.out.println("Tossing a coin to decide who goes first!!!");
        int coinResult = flipCoin();

        if(coinResult == 1)
        {
            String tempName = playerNames[0];
            playerNames[0] = playerNames[1];
            playerNames[1] = tempName;
        }
        System.out.println(playerNames[0] + " goes first."); //player 2 goes first

        displayGameFromState(getInitialGameState()); // Displays initial board
        gameHistory.add(currentState); // adds initial board to gameHistory

        while(!checkDraw(currentState) || !checkWin(currentState)) //continues until a win or a draw
        {
            //Player 1 turn
            currentState = runPlayerMove(playerNames[0], playerOneSymbol, currentState);
            gameHistory.add(currentState); //adds turn to history
            displayGameFromState(currentState); //displays game
            //checks for win or draw after each turn
            if (checkWin(currentState)) {
                System.out.println(playerNames[0] + " has won the game!\n");
                break;
            }
            if (checkDraw(currentState)) {
                System.out.println("The game is a draw!\n");
                break;
            }

            //Player 2 turn
            currentState = runPlayerMove(playerNames[1], playerTwoSymbol, currentState);
            gameHistory.add(currentState); //add turn to history
            displayGameFromState(currentState); //displays game
            //checks for win or draw after each turn
            if (checkWin(currentState)) {
                System.out.println(playerNames[1] + " has won the game!\n");
                break;
            }
            if (checkDraw(currentState)) {
                System.out.println("The game is a draw!\n");
                break;
            }
        }
        return gameHistory;
    }

    /**
     * Runs the one-player game
     * @param playerNames
     * @return an ArrayList of game states of each turn -- in other words, the gameHistory
     */
    private static ArrayList<char[][]> runOnePlayerGame(String[] playerNames) {

        char[][] currentState = getInitialGameState(); //gives empty board
        ArrayList<char[][]> gameHistory = new ArrayList<char[][]>(); //list records game history

        //decides who is player 1 and who is player 2
        System.out.println("Tossing a coin to decide who goes first!!!");
        int coinResult = flipCoin();

        if(coinResult == 1)
        {
            String tempName = playerNames[0];
            playerNames[0] = playerNames[1];
            playerNames[1] = tempName;
        }
        System.out.println(playerNames[0] + " goes first."); //player 2 goes first

        displayGameFromState(getInitialGameState());
        gameHistory.add(currentState); //adds initial board to gameHistory

        if(coinResult == 0) //if player is going first
        {
            while(!checkDraw(currentState) || !checkWin(currentState))
            {
                //Player turn
                currentState = runPlayerMove(playerNames[0], playerOneSymbol, currentState);
                gameHistory.add(currentState); //records game history
                displayGameFromState(currentState); //displays game
                //check for win after each turn
                if (checkWin(currentState)) {
                    System.out.println(playerNames[0] + " has won the game!\n");
                    break;
                }
                if (checkDraw(currentState)) {
                    System.out.println("The game is a draw!\n");
                    break;
                }

                //Computers turn
                System.out.println("Computer's turn:");
                currentState = getCPUMove(currentState, playerTwoSymbol);
                gameHistory.add(currentState); //records game history
                displayGameFromState(currentState); //displays game
                //check for win after each turn
                if (checkWin(currentState)) {
                    System.out.println(playerNames[1] + " has won the game!\n");
                    break;
                }
                if (checkDraw(currentState)) {
                    System.out.println("The game is a draw!\n");
                    break;
                }
            }
        }
        else //if computer is going first
        {
            while(!checkDraw(currentState) || !checkWin(currentState))
            {
                //Computer turn
                System.out.println("Computer's turn:");
                currentState = getCPUMove(currentState, playerOneSymbol);
                gameHistory.add(currentState); //records game history
                displayGameFromState(currentState); //displays game
                //check for win after each turn
                if (checkWin(currentState)) {
                    System.out.println(playerNames[0] + " has won the game!\n");
                    break;
                }
                if (checkDraw(currentState)) {
                    System.out.println("The game is a draw!\n");
                    break;
                }

                //Player turn
                System.out.println(playerNames[1] + " turn:");
                currentState = runPlayerMove(playerNames[1], playerTwoSymbol, currentState);
                gameHistory.add(currentState); //records game history
                displayGameFromState(currentState); //displays game
                //check for win after each turn
                if (checkWin(currentState)) {
                    System.out.println(playerNames[1] + " has won the game!");
                    break;
                }
                if (checkDraw(currentState)) {
                    System.out.println("The game is a draw!");
                    break;
                }
            }
        }
        return gameHistory;
    }

    /**
     * Repeatedly prompts player for move in current state, returning new state after their valid move is made
     * @param playerName
     * @param playerSymbol
     * @param currentState
     * @return the new state after a valid move
     */
    private static char[][] runPlayerMove(String playerName, char playerSymbol, char[][] currentState) {

        int[] move = getInBoundsPlayerMove(playerName);
        while (!checkValidMove(move, currentState)) //continues to prompt until valid move
        {
            System.out.println("That space is already taken. Try again.");
            move = getInBoundsPlayerMove(playerName);
        }

        return makeMove(move, playerSymbol, currentState);
    }

    /**
     * Repeatedly prompts player for move. Returns [row, column] of their desired move such that row & column are on
     * the 3x3 board
     * @param playerName
     * @return move
     */
    private static int[] getInBoundsPlayerMove(String playerName) {
        Scanner sc = new Scanner(System.in); //create scanner object
        boolean inBound = false;
        int[] move = new int[2];
        System.out.println(playerName + "'s turn:");

        while(!inBound) //loop until inputted move is in bounds
        {
            //prompt user input
            System.out.print(playerName + " enter row: ");
            while (!sc.hasNextInt()) //checks if input is valid
            {
                System.out.println("Invalid row. Try again.");
                System.out.print(playerName + " enter row: ");
                sc.next();
            }
            move[0] = sc.nextInt();

            System.out.print(playerName + " enter column: ");
            while (!sc.hasNextInt()) //checks if input is valid
            {
                System.out.println("Invalid column. Try again.");
                System.out.print(playerName + " enter column: ");
                sc.next();
            }
            move[1] = sc.nextInt();

            if(move[0] < 3 && move[0] >= 0 && move[1] < 3 && move[1] >= 0) //checks if move is in bounds
                inBound = true;
            else
                System.out.println("That row or column is out of bounds. Try again.");
        }
        return move;
    }

    /**
     * Given a [row, col] move, return true if a space is unclaimed.
     * @param move
     * @param state
     * @return boolean
     */
    private static boolean checkValidMove(int[] move, char[][] state) {

        if (state[move[0]][move[1]] == emptySpaceSymbol) // if spot is empty
            return true;
        else
            return false;
    }

    /**
     * Return a NEW array with the new game state
     * @param move
     * @param symbol
     * @param currentState
     * @return game state
     */
    private static char[][] makeMove(int[] move, char symbol, char[][] currentState) {

        char[][] newState = new char[3][3];

        //copies current state to new state
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                newState[i][j] = currentState[i][j];

        newState[move[0]][move[1]] = symbol; //updates new state with move and symbol
        return newState;
    }

    /**
     * Given a state, return true if some player has won in that state
     * @param state
     * @return boolean
     */
    private static boolean checkWin(char[][] state) {

        // Horizontals
        for(int i = 0; i < 3; i++)
        {
            if((state[i][0] == playerOneSymbol && state[i][1] == playerOneSymbol && state[i][2] == playerOneSymbol) ||
                    (state[i][0] == playerTwoSymbol && state[i][1] == playerTwoSymbol && state[i][2] == playerTwoSymbol))
            {
                return true;
            }
        }
        // Verticals
        for(int i = 0; i < 3; i++)
        {
            if((state[0][i] == playerOneSymbol && state[1][i] == playerOneSymbol && state[2][i] == playerOneSymbol) ||
                    (state[0][i] == playerTwoSymbol && state[1][i] == playerTwoSymbol && state[2][i] == playerTwoSymbol))
            {
                return true;
            }
        }
        // Diagonals
        if ((state[0][0] == playerOneSymbol && state[1][1] == playerOneSymbol && state[2][2] == playerOneSymbol) ||
                (state[0][0] == playerTwoSymbol && state[1][1] == playerTwoSymbol && state[2][2] == playerTwoSymbol)) {
            return true;
        }
        if ((state[0][2] == playerOneSymbol && state[1][1] == playerOneSymbol && state[2][0] == playerOneSymbol) ||
                (state[0][2] == playerTwoSymbol && state[1][1] == playerTwoSymbol && state[2][0] == playerTwoSymbol)) {
            return true;
        }
        return false; //there is not a win
    }

    /**
     * Checks if all spaces are occupied for a draw
     * @param state
     * @return boolean
     */
    private static boolean checkDraw(char[][] state) {

        for (int i = 0; i < 3; i++) { //for every row
            for (int j = 0; j < 3; j++) { //for every column
                if (state[i][j] == emptySpaceSymbol) // if spot is empty
                    return false;
            }
        }
        return true; //returns if every spot is occupied
    }

    /**
     * For 1 player game, getCPUMove determines the move the computer will take
     * @param gameState
     * @param symbol
     * @return game state
     */
    private static char[][] getCPUMove(char[][] gameState, char symbol) {

        ArrayList<int[]> availableMoves = getValidMoves(gameState);//determines all available spaces

        char otherSymbol; //determines other players symbol
        if(symbol == playerOneSymbol)
            otherSymbol = playerTwoSymbol;
        else
            otherSymbol = playerOneSymbol;

        //checks if there is a possible win
        for(int i = 0; i < availableMoves.size(); i++)
            if(checkWin(makeMove(availableMoves.get(i), symbol, gameState)))
                return makeMove(availableMoves.get(i), symbol, gameState); //makes move to win

        //checks if opponent has possible win
        for(int i = 0; i < availableMoves.size(); i++)
            if(checkWin(makeMove(availableMoves.get(i), otherSymbol, gameState)))
                return makeMove(availableMoves.get(i), symbol, gameState); //makes move to counter

        //declare significant moves
        int[] middle = {1,1};
        int[] corner1 = {0,0};
        int[] corner2 = {0,2};
        int[] corner3 = {2,0};
        int[] corner4 = {2,2};

        //Middle is first priority
        if (checkValidMove(middle, gameState))
            return makeMove(middle, symbol, gameState);
            //if middle is occupied, will take one of the corners
        else if (checkValidMove(corner1, gameState))
            return makeMove(corner1, symbol, gameState);
        else if (checkValidMove(corner2, gameState))
            return makeMove(corner2, symbol, gameState);
        else if (checkValidMove(corner3, gameState))
            return makeMove(corner3, symbol, gameState);
        else if (checkValidMove(corner4, gameState))
            return makeMove(corner4, symbol, gameState);
            //If all are taken, makes first available move
        else
            return makeMove(getValidMoves(gameState).get(0), symbol, gameState);
    }

    /**
     * Given a game state, return an ArrayList of [row, column] positions that are unclaimed on the board
     * @param gameState
     * @return result
     */
    private static ArrayList<int[]> getValidMoves(char[][] gameState) {

        ArrayList<int[]> result = new ArrayList<int[]>(); //declare new array list

        //checks if each move is valid
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int[] move = {i, j};
                if (checkValidMove(move, gameState))
                    result.add(move); //if valid, move is added to ArrayList
            }
        }
        return result;
    }

    /**
     * Given player names and the game history, display the past game as in the PDF sample code output
     * @param playerNames
     * @param gameHistory
     */
    private static void runGameHistory(String[] playerNames, ArrayList<char[][]> gameHistory) {

            System.out.println(playerNames[0] + " (X) vs " + playerNames[1] + " (O)"); // output who is playing and their symbol
            displayGameFromState(gameHistory.get(0)); //output initial game state

            //Displays each state in gameHistory
            for (int i = 1; i < gameHistory.size(); i++) {
                if(i % 2 == 1) // output the name of the player who went first
                    System.out.println(playerNames[0] + ": ");
                else
                    System.out.println(playerNames[1] + ": ");
                displayGameFromState(gameHistory.get(i));
            }

        //check for a draw
        if(checkDraw(gameHistory.get(gameHistory.size()-1)))
            System.out.println("The game is a draw!\n"); // displays result if draw

        //check for a win
        if(checkWin(gameHistory.get(gameHistory.size()-1)))
        {
            if ((gameHistory.size()-1) % 2 == 0)
                System.out.println(playerNames[1] + " wins!\n"); //displays result if player wins
            else
                System.out.println(playerNames[0] + " wins!\n");
        }
    }
}

