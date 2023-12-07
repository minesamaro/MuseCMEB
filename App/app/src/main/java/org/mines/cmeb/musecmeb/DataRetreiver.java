package org.mines.cmeb.musecmeb;

import java.io.*;
import java.util.Scanner;

public class DataRetreiver {


    public static void main(String[] args) throws Exception {
//parsing a CSV file into Scanner class constructor
        Scanner sc = new Scanner(new File("F:\\CSVDemo.csv"));
        sc.useDelimiter(",");   //sets the delimiter pattern
        int alpha_power;
        int i=1;
        while (sc.hasNext())  //returns a boolean value
        {
            if (i==4) {
                alpha_power= Integer.parseInt(sc.next()); //ver o q isto significa
                //intent that stores power

            }

            System.out.print(sc.next());
            i=i+1;//find and returns the next complete token from this scanner
        }
        sc.close();  //closes the scanner
    }

}

