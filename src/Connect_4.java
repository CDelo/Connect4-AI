import java.io.*;
import java.util.*;



public class Connect_4 {
    public int[][] board = new int[7][6];
    public int lastx,lasty,lastPlayer;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    public Connect_4() {
        printBoard(board);
        int stalemate = 0;
        while (true){
            System.out.println("Input the column you want to play");
            try {
                board = addPiece(board,playerChoice(),-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            printBoard(board);
            if (winState(-1)){
                System.out.println("You Win!!!");
                break;
            } //checks for a player victory

            botMove();
            printBoard(board);
            if (winState(1)){
                System.out.println("You Lose");
                break;
            }// checks for an AI victory

            stalemate +=2;
            if (stalemate == 42){
                System.out.println("Stalemate");
                break;
            }   // this ends the game if there is a stalemate
        }
    }
    public int[][] addPiece(int[][] bState, int x, int p){
        int y;
        int[][] temp = dupeBoard(bState);
        for (y = 5;y>=0;y--){
            if (temp[x][y] == 0) {
                temp[x][y] = p;
                lastx = x;
                lasty = y;
                lastPlayer = p;
                break;}
        }
        return temp;
    } // adds a piece to a board in a given column if possible
    public int playerChoice() throws IOException {
        try {
            int x = Integer.parseInt(in.readLine());
            if ((x > 6) || (x < 0) || board[x][0] != 0) {
                System.out.print(x);
                System.out.println("Please input a valid position");
                playerChoice();
            }
            return x;
        }
        catch (NumberFormatException e){
            System.out.println("Only numbers please");
            playerChoice();}
        return 0;
    }//method for getting player input
    public void printBoard(int[][] board){
        int i,j;
        String c;
        for (int p = 0; p<7; p++) System.out.print(" " + p);
        System.out.println();
        for (i=0;i<6;i++){
            for (j=0;j<7;j++) {
                switch (board[j][i]) {
                    case 0:
                        c = " ";
                        break;
                    case 1:
                        c = "X";
                        break;
                    default:
                        c = "O";
                        break;
                }
                System.out.print("|" + c);

            }
            System.out.print("|" + '\n');
        }
    }//diplays the board in the console

    public void botMove(){
        int i;
        int bestValue = -100000000;
        int bestMove = 0;
        for (i=0;i<7;i++){
            int[][] temp = dupeBoard(board);
            if (temp[i][0] == 0) {
                int xd = boardEvalMax(addPiece(temp,i,-1),4);
                if (xd > bestValue) {
                    bestMove = i;
                    bestValue = xd;
                }
            }
        }
        board = addPiece(board,bestMove,1);
    }//method for determining the AI's move, also works as the first portion of minimax

    public int boardEvalMax(int[][] bState,int depth){

        if (depth == 0){
            int[] botHeu = Heuristic(1,bState);
            int[] huHeu = Heuristic(-1,bState);
            int total;
            total = (botHeu[0] - huHeu[0])*5 + (botHeu[1] - huHeu[1])*300 + (botHeu[2] - huHeu[2])*1000000;

            return total;
        }
        int max = -1000000000;
        int t;
        for (int a = 0;a<7;a++){
            int[][] temp = dupeBoard(bState);
            if (temp[a][0] == 0) {

                t = boardEvalMin(addPiece(temp, a, 1),depth -1);
                if (max < t) max = t;
            }

        }
        return max;
    }// the max part of minimax
    public int boardEvalMin(int[][] bState, int depth){
        if (depth == 0){
            int[] botHeu = Heuristic(1,bState);
            int[] huHeu = Heuristic(-1,bState);
            int total;
            total = (botHeu[0] - huHeu[0])*5 + (botHeu[1] - huHeu[1])*30 + (botHeu[2] - huHeu[2])*10000000;
            return total;
        }

        int min = 100000000;
        int t;
        for (int a = 0;a<7;a++){
            int[][] temp = dupeBoard(bState);
            if (temp[a][0] == 0) {
                t = boardEvalMax(addPiece(temp, a, -1),depth-1);
                if (min > t) min = t;
            }
        }
        return min;
    }// the mini part of minimax

    public int[][] dupeBoard(int[][] input){
        int[][] output = new int[7][6];
        for (int i = 0;i<7;i++){
            output[i] = Arrays.copyOf(input[i],input[i].length);
        }

        return output;
    }// duplicates the board to not edit the same board
    public int[] Heuristic(int player, int[][] board){
        int[] eval = new int[3];
        int[] scores = new int[4];
        scores[0] = vWin(lastx, lasty, player, board);
        scores[1] = hWin(lastx, lasty, player, board);
        scores[2] = pSlopeWin(lastx, lasty, player, board);
        scores[3] = nSlopeWin(lastx, lasty, player, board);
        for (int k = 0; k < 4; k++) {
            if (scores[k] == 2) eval[0]++;
            if (scores[k] == 3) eval[1]++;
            if (scores[k] == 4) eval[2]++;
        }
        return eval;
    }//the heuristic with which the program evaluates a game state
    public int vWin(int x, int y, int p, int[][] board){
        int t = 0;
        int max = Math.min(y + 4, 6);
        for (int i = y; i<max; i++){
            if (board[x][i] == p) t++;
        }
        return t;

    }// next four methods count pieces in rows, columns, diagonals, etc
    public int hWin(int x, int y, int p, int[][] board){
        int t = 0;
        int tmax = 0;
        int max = Math.min(x + 4, 7);
        int min = x - 4 < 0? 0:x-3;
        for (int i =min;i<max ;i++){
            if (board[i][y] == p) {
                t++;
                tmax = Math.max(t, tmax);
                if (t == 4) return t;
            }
            else t = 0;
        }
        return tmax;
    }
    public int pSlopeWin(int x, int y, int p, int[][] board){
        int t = 0;
        int tmax = 0;
        int xMax = x + 3;
        int xMin = x - 3;
        int j = y + 3;
        for (int i =xMin;i<xMax ;i++){
            if (i >= 0 && i <= 6 && j >= 0 && j<=5){
                if(board[i][j] == p) {
                    t++;
                    tmax = Math.max(t, tmax);
                    if (t == 4) return t;

                }
                else t = 0;
            }
            j--;
        }
        return tmax;
    }
    public int nSlopeWin(int x, int y, int p, int[][] board){
        int t = 0;
        int tmax = 0;
        int xMax = x + 3;
        int xMin = x - 3;
        int j = y - 3;
        for (int i =xMin;i<xMax ;i++){
            if (i >= 0 && i <= 6 && j >= 0 && j<=5){
                if(board[i][j] == p) {
                    t++;
                    tmax = Math.max(t, tmax);
                    if (t == 4) return t;

                }
                else t = 0;
            }
            j++;
        }
        return tmax;
    }
    public boolean winState(int player){
        for (int i=0;i<7;i++) {
            int[] scores = new int[4];
            scores[0] = vWin(lastx, lasty, player, board);
            scores[1] = hWin(lastx, lasty, player, board);
            scores[2] = pSlopeWin(lastx, lasty, player, board);
            scores[3] = nSlopeWin(lastx, lasty, player, board);
            Arrays.sort(scores);
            if (scores[3] == 4) return true;
        }
        return false;
    }//checks if a given player (AI or human) has won the game
    public static void main(String[] args) {
        Connect_4 c = new Connect_4();
    }

}