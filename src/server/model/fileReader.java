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
package server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class fileReader {

    private static final String FILEPATH = System.getProperty("user.dir") + "\\src\\server\\model\\words.txt";

    
    //Reads from the file and returns a random word
    public String readFile() throws IOException {
        BufferedReader br = null;
        FileReader fr = null;
        ArrayList<String> words = new ArrayList<String>();

        fr = new FileReader(FILEPATH);
        br = new BufferedReader(fr);
        String word;
        while ((word = br.readLine()) != null) {
            words.add(word);
        }

        return words.get((int) (Math.random() * words.size()));
    }
}
