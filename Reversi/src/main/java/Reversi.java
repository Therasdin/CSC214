

import java.util.Scanner;

//**********************************************************************
//**
//**						  APCS Reversi
//**			The classic game of reversal of fortunes
//**
//**		Written by Mr. Indelicato as an APCS Final Project
//**	Copyright (c) Tom Indelicato / Bishop Guertin High School
//**
//**********************************************************************
//** REVISION HISTORY
//**	20150427	Initial work begins: board storage, printing, prompt
//**				for human / computer players; framework added for
//**				computer and human move selection.
//**	20150428	Added logic for accepting human moves: input parsing,
//**				validity checks, piece flipping. At this point the
//**				program serves as a human v. human game
//**	20150429	Logic added to prevent end-of-game lock-up, added
//**				Winner check & announce code.
//**	20150508	Minor code clean-up
//**********************************************************************
//** TO DO LIST
//**	- Add AI for Computer (see bottom of code)
//**********************************************************************
//** BUG LIST
//**	- Can put piece on top of another piece				KILLED 0429
//**********************************************************************

public class Reversi
{
	static Scanner input;
	
	static final int EMPTY = 0;
	static final int BLACK = 1;
	static final int WHITE = 2;
	static final int HUMAN = 1;
	static final int COMPUTER = 2;
	
	static int[][] board = new int[8][8];
	static int whitePlayer, blackPlayer;
	static boolean gameOver = false;
	static int firstMove = BLACK;
	static int nextMove;

	
	public static void main(String[] args)
	//**********************************************************************
	//** main
	//**	Displays welcome banner, allows user to select human or computer 
	//**	for White and Black, resets and prints board. Play alternates 
	//**	between Black and White A) as long as there are more moves to
	//**	make, and B) until a winner is found.
	//**********************************************************************
	{
		boolean onePlayerCantMove = false;		// indicates both players blocked 

		input = new Scanner(System.in);

		System.out.println("\t\t#O#O#O  APCS Reversi  O#O#O# \n");
		System.out.println("\t\t   Author: Mr. Indelicato\n\n"); // <-- Insert Your Name Here!

		choosePlayers();

		newGame();
		printBoard();

		while (!gameOver)
		{
			if (nextMove == BLACK)
			{
				if (!moreMoves(BLACK))
				{
					System.out.println("No move for Black, White moves next");
					if (onePlayerCantMove) gameOver = true;
					onePlayerCantMove = true;
				}
				else
				{
					onePlayerCantMove = false;
					if (blackPlayer == HUMAN)
						promptHuman(BLACK);
					else
						machineStrategy(BLACK);

					printBoard();
				}
			}
			else
			{
				if (!moreMoves(WHITE))
				{
					System.out.println("No move for White, Black moves next");
					if (onePlayerCantMove) gameOver = true;
					onePlayerCantMove = true;
				}
				else
				{
					onePlayerCantMove = false;
					if (whitePlayer == HUMAN)
						promptHuman(WHITE);
					else
						machineStrategy(WHITE);

					printBoard();
				}
			}
			
			nextMove = BLACK + WHITE - nextMove;	// toggle next player
		}
		
		// Game over: Report winner
		System.out.println("*** Game Over ***");
		int blackTotal = pieceCount(BLACK);
		int whiteTotal = pieceCount(WHITE);
		
		if (blackTotal == whiteTotal)
			System.out.println("Tie Game, " + blackTotal + "-" + whiteTotal);
		if (blackTotal > whiteTotal)
			System.out.println("Black wins, " + blackTotal + "-" + whiteTotal);
		if (blackTotal < whiteTotal)
			System.out.println("White wins, " + whiteTotal + "-" + blackTotal);
 
	}
	
	public static void choosePlayers()
	//**********************************************************************
	//** choosePlayers
	//**	Called at the start of the game, this code prompts the user to 
	//**	indicate which player (Black or White) will be human and which
	//**	(white or Black) will be computer.
	//**
	//** NOTE: It is perfectly legal to have both players human, or both
	//**	players computer.
	//**********************************************************************
	{
		String inputData = "";
		while (!inputData.toUpperCase().equals("H") && !inputData.toUpperCase().equals("C"))
		{
			System.out.print("Black Player -- Human or Computer (H or C): ");
			inputData = input.nextLine();
		}
		if (inputData.toUpperCase().equals("H")) blackPlayer = HUMAN; else blackPlayer = COMPUTER; 
		inputData = "";
		while (!inputData.toUpperCase().equals("H") && !inputData.toUpperCase().equals("C"))
		{
			System.out.print("White Player -- Human or Computer (H or C): ");
			inputData = input.nextLine();
		}
		if (inputData.toUpperCase().equals("H")) whitePlayer = HUMAN; else whitePlayer = COMPUTER; 
	}
	
	public static void newGame()
	//**********************************************************************
	//** newGame
	//**	Resets the Reversi board: Sets all 64 squares to EMPTY, puts the
	//**	initial four pieces in the center, flips the color of the first
	//**	player to move.
	//**********************************************************************
	{
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				board[row][col] = EMPTY;
		
		board[3][3] = WHITE;
		board[3][4] = BLACK;
		board[4][3] = BLACK;
		board[4][4] = WHITE;
		
		nextMove = firstMove;
		firstMove = BLACK + WHITE - firstMove;
		
		initializeMachineLogic();	// Used to set up AI code, if needed
	}
	
	public static void printBoard()
	//**********************************************************************
	//** printBoard
	//**	Does a passable job of displaying the Reversi board
	//**********************************************************************
	{
		System.out.print("\n\t     A   B   C   D   E   F   G   H");
		for (int row = 0; row < 8; row++)
		{
			System.out.print("\n\t   +---+---+---+---+---+---+---+---+\n\t " + (row + 1) + " |");

			for (int col = 0; col < 8; col++)
			{
				if (board[row][col] == EMPTY) System.out.print("   |");
				if (board[row][col] == BLACK) System.out.print(" # |");
				if (board[row][col] == WHITE) System.out.print(" O |");
			}
		}
		System.out.print("\n\t   +---+---+---+---+---+---+---+---+\n");
	}

	public static int pieceCount(int thisColor)
	//**********************************************************************
	//** pieceCount
	//**	Used to determine Winner. Counts the number of thisColor pieces
	//**	found on the board.
	//**********************************************************************
	{
		int count = 0;
		
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if (board[row][col] == thisColor) count++;
		
		return count;
	}
	
	public static boolean moreMoves(int myColor)
	//**********************************************************************
	//** moreMoves
	//**	Checks to see if there is at least one valid move for the next 
	//**	player. Scans the entire board, checking each empty place to 
	//**	see if the "myColor" player can move. If so, return true so that
	//**	s/he can move. If not, return false to say the previous player
	//**	can go again.
	//**********************************************************************
	{
		for (int row = 0; row < 8; row++)
			for (int col = 0; col < 8; col++)
				if (board[row][col] == EMPTY)
					if (checkMove(col, row, myColor))
						return true;
		return false;
	}
	
	public static boolean checkMove(int X, int Y, int myColor)
	//**********************************************************************
	//** checkMove
	//**	Determines if this square (X,Y) has a valid move for the given 
	//**	color. Using the input parameters, scan the board in each of 
	//**	the eight potential directions, returning true if a move can be
	//**	made in any of the directions.
	//**
	//** INPUT:
	//**	X, Y:			board coordinates of the square to be checked
	//**	myColor:		color of the piece being (potentially) placed
	//**
	//** OUTPUT:
	//**	true  - if a valid move is found
	//**	false - if no valid move is found
	//**********************************************************************
	{

		return  checkMoveThisDir(X, Y, -1, -1, myColor) ||
				checkMoveThisDir(X, Y, -1,  0, myColor) ||
				checkMoveThisDir(X, Y, -1,  1, myColor) ||
				checkMoveThisDir(X, Y,  0, -1, myColor) ||
				checkMoveThisDir(X, Y,  0,  1, myColor) ||
				checkMoveThisDir(X, Y,  1, -1, myColor) ||
				checkMoveThisDir(X, Y,  1,  0, myColor) ||
				checkMoveThisDir(X, Y,  1,  1, myColor);
	}
	
	public static boolean checkMoveThisDir(int X, int Y, int deltaX, int deltaY, int myColor)
	//**********************************************************************
	//** checkMoveThisDir
	//**	Determines if this square (X,Y) has a valid move in the given 
	//**	direction (deltaX, deltaY) for the given color. Using the input
	//**	parameters, scan the board quitting if a blank, a myColor piece,
	//**	or a series of Enemy pieces that don't have a myColor piece 
	//**	immediately following.
	//**
	//** INPUT:
	//**	X, Y:			board coordinates of the square to be checked
	//**	deltaX,deltaY:	direction to be checked for valid move
	//**	myColor:		color of the piece being (potentially) placed
	//**
	//** OUTPUT:
	//**	true  - if a valid move is found
	//**	false - if no valid move is found
	//**********************************************************************
	{
		if (board[Y][X] != EMPTY) return false;
		
		int enemyColor = WHITE + BLACK - myColor;

		int currX = X + deltaX;
		int currY = Y + deltaY;
		if (currX < 0 || currX > 7 || currY < 0 || currY > 7)
			return false;
		
		if (board[currY][currX] != enemyColor) return false;

		while (board[currY][currX] == enemyColor)
		{
			currX += deltaX;
			currY += deltaY;
			if (currX < 0 || currX > 7 || currY < 0 || currY > 7)
				return false;
		}
		if (board[currY][currX] != myColor) 
			return false;
		
		return true;		
	}

	public static void putPiece(int X, int Y, int myColor)
	//**********************************************************************
	//** putPiece
	//**     Now that we know there's a valid move, we have to flip the
	//**     pieces around. Following the pattern of checkMove, we scan in
	//**     eight directions (up&left, up, up&right, left, right,
	//**     down&left, down, and down&right), looking for pieces to flip.
	//**     We then put the piece down on the square, ending the flipping.
	//** INPUT:
	//**     X, Y:           coordinates of space where the piece is placed 
	//**     myColor:        color of piece being placed
	//**********************************************************************
	{
		flipPiecesThisDir(X, Y, -1, -1, myColor);
		flipPiecesThisDir(X, Y, -1,  0, myColor);
		flipPiecesThisDir(X, Y, -1,  1, myColor);
		flipPiecesThisDir(X, Y,  0, -1, myColor);
		flipPiecesThisDir(X, Y,  0,  1, myColor);
		flipPiecesThisDir(X, Y,  1, -1, myColor);
		flipPiecesThisDir(X, Y,  1,  0, myColor);
		flipPiecesThisDir(X, Y,  1,  1, myColor);
		board[Y][X] = myColor;
	}
	
	public static void flipPiecesThisDir(int X, int Y, int deltaX, int deltaY, int myColor)
	//**********************************************************************
	//** flipPiecesThisDir
	//**     Search a given square in a given direction, and flip pieces
	//**     around if appropriate. Using logic similar to checkMove, if
	//**     a valid move is found we go the additional step of flipping
	//**     pieces.
	//** INPUT:
	//**     X, Y:           board coordinates of square to be checked
	//**     deltaX, deltaY: direction to be checked for pieces to flip
	//**     myColor:        color of piece being (potentially) placed
	//**********************************************************************
	{
		int currX, currY;
		int enemyColor = WHITE + BLACK - myColor;
		
		currX = X + deltaX;
		currY = Y + deltaY;
		if (currX == -1 || currX == 8 || currY == -1 || currY == 8) return;
		if (board[currY][currX] != enemyColor) return;

		while(board[currY][currX] == enemyColor)
		{
			currX += deltaX;
			currY += deltaY;
			if (currX == -1 || currX == 8 || currY == -1 || currY == 8) return;
		}
		if (board[currY][currX] != myColor) return;

		// If we're here, we passed all checks, so start flipping pieces
		do
		{
			board[currY][currX] = myColor;
			currX -= deltaX;
			currY -= deltaY;
		} while (board[currY][currX] == enemyColor);
	}
	
	public static void promptHuman(int playerColor)
	//**********************************************************************
	//** promptHuman
	//**	Asks the human player to indicate where his/her next move will
	//**	be. Starts by prompting for input; input is then checked to see
	//**	if it valid (i.e., a letter-number combination). If it's valid,
	//**	the move is checked to see if it's legal (i.e., the selected
	//**	space results in tiles flipped), and if so, the piece is placed.
	//**********************************************************************
	{
		String inputData = "";
		boolean goodMove = false;
		boolean validEntry;
		int col = 0, row = 0;
		
		while (!goodMove)
		{
			validEntry = false;
			while (!validEntry)
			{
				if (playerColor == BLACK)
					System.out.print("Black");
				else
					System.out.print("White");
				System.out.print(" player, enter your move in column-row format (e.g., A5): ");
				inputData = input.nextLine();
				
				col = -1; row = -1;
				if (inputData.length() >= 2 && 
					inputData.toUpperCase().charAt(0) >= 'A' &&
					inputData.toUpperCase().charAt(0) <= 'H' &&
					inputData.charAt(1) >= '1' && inputData.charAt(1) <= '8')
				{
					col = inputData.toUpperCase().charAt(0) - 'A';
					row = Integer.parseInt(inputData.substring(1,2)) - 1;
				}
				else
					System.out.println("Invalid entry, please try again.");
				
				if (col >= 0 && col < 8 && row >= 0 && row < 8) validEntry = true;
			}
			
			if (checkMove(col, row, playerColor))
				goodMove = true;
			else
				System.out.println("Invalid location, try again.");
		}
		
		putPiece(col, row, playerColor);
	}

	
	//**********************************************************************
	//** MACHINE LOGIC CODE
	//**	This is where you will put your code, to make your game as
	//**	intelligent / competitive as you can.
	//**********************************************************************
	
	public static void initializeMachineLogic()
	//**********************************************************************
	//** initializeMachineLogic
	//**	Called from the newGame() method, this initializes your AI.
	//**
	//**					describe your code here
	//**	
	//**********************************************************************
	{
		
	}
	
	public static void machineStrategy(int playerColor)
	//**********************************************************************
	//** machineStrategy
	//**	Determines the best place to move.
	//**
	//**					describe your code here.
	//**
	//**********************************************************************
	{
		
		
		System.out.println("Computer placed piece at location: ");
	}

	//*******************************
	//** Machine Logic Variables
	//*******************************
	

}
