/*
 * Copyright (C) 2017 Shayan Fallahian shayanf@kth.se
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.net;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import server.controller.Controller;

public class HangmanHandler {

    private final SocketChannel socketChannel;
    private final Socket socket;
    private final Controller control;
    private String word;
    private int isWordGuessed;
    private int tries;
    private int score;
    private int totalTries;
    private char[] guessWord;
    private char[] playerGuess;

    public HangmanHandler(SocketChannel socketChannel, Socket socket) {
        this.socketChannel = socketChannel;
        this.socket = socket;
        this.control = new Controller();
        this.isWordGuessed = 1;
        this.score = 0;
    }

    //This is where the client is handled
    //The outer do while loop keeps track of the entire gamesession
    //while the inner while loop keeps track of one game session (i.e word guessing)
    public void run(String input) {

        if (isWordGuessed == 1) {
            word = control.getWord().toLowerCase();

            guessWord = word.toCharArray();
            totalTries = guessWord.length;
            playerGuess = new char[totalTries];

            for (int i = 0; i < playerGuess.length; i++) {
                playerGuess[i] = '_';
            }

            isWordGuessed = 2;
            tries = 0;
            println("Welcome (again) to the Hangman game made by Shayan Fallahian! (shayanf@kth.se)\n");
            print("Your total score so far is: " + score + "\n\n");
            print("The word has " + totalTries + " letters.\n\n");
            print("Lets begin!\n");
            print("\nCurrent state: ");
            printArray(playerGuess);
            print("\nYou have " + (totalTries - tries) + " tries left.\n");
            print("Enter a letter or word! ('-' to quit)\n");
            return;
        }
        if (isWordGuessed == 2 && tries != totalTries) {
            tries++;
            String guessedWord = input.toLowerCase();
            char letter = guessedWord.charAt(0);

            if (letter == '-') {
                isWordGuessed = 5;
                tries = totalTries;
                closeSocket(socket);
            } else {
                for (int i = 0; i < guessWord.length; i++) {
                    if (guessWord[i] == letter && guessedWord.length() == 1) {
                        playerGuess[i] = letter;
                    }
                }
            }
            if (isWordGuessed(playerGuess) || word.equals(guessedWord)) {
                isWordGuessed = 4;
                println("Congratulations you won!");
                score++;
            } else if (tries == totalTries) {
                isWordGuessed = 3;
            } else if (isWordGuessed == 2) {
                print("\nCurrent state: ");
                printArray(playerGuess);
                print("\nYou have " + (totalTries - tries) + " tries left.\n");
                print("Enter a letter or word! ('-' to quit)\n");
            }
        }
        if (isWordGuessed == 3) {
            println("\nYou ran out of guesses.");
            print("Your final state was: ");
            printArray(playerGuess);
            println("Word was " + word + ".\n");
            isWordGuessed = 4;
        }

        if (isWordGuessed == 4) {
            println("Do you want to play another game? (yes/no)");
            isWordGuessed = 1;
        }
    }

    public void printArray(char[] array) {
        for (int i = 0; i < array.length; i++) {
            print(array[i] + " ");
        }
        print("\n");
    }

    public boolean isWordGuessed(char[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '_') {
                return false;
            }
        }
        return true;
    }

    /*
    println and print are simple print function
    where you simply wrap your message in a ByteBuffer
    and pass it on to the socketChannel
     */
    private void println(String message) {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            System.out.println("\nConnection with " + socket + " has been terminated.\n");
            closeSocket(socket);
        }
    }

    private void print(String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        try {
            socketChannel.write(buffer);
        } catch (IOException e) {
            System.out.println("\nConnection with " + socket + " has been terminated.\n");
            closeSocket(socket);
        }
    }

    //Closes current socket
    private void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Unable to close socket!");
        }
    }
}
