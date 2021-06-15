package com.medcheck;
import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        //Scanner to help read names of user's medication list and database name
        Scanner input = new Scanner(System.in);

        //Class with all of the functions needed
        functions function = new functions();

        //Read name of user's medication list file name
        //System.out.println("Type the name of the file that has your medications");
        //System.out.print("File name: ");
        //String medInput = input.nextLine();

        //Intro message
        function.intro();

        // Asks the user if they want to import a table made by the program
        // (or if they made one themselves that has the same structure as a table made by the program)
        // or if they want to make a table by scratch
        // If they want to make a table, it will ask if they want to use a .txt file with their medication names
        // or if they want to type in the medication names
        function.tableOrFile();

        //Adding database medications to hashmap of medications
        function.databaseAdd("meddatabase.csv");

        //Check medication list against database
        function.compare();

        //Check for empty ArrayLists
        function.isItEmpty();

        //Puts user in a loop that allows them to change medication categories if needed
        function.commandLine();

        /*
        Plan is to import patient's medication list as a .txt file
        and a database as a csv file. This program will come with a default database, but patients can use their own.
        Medication list will be an ArrayList and the database will be a hashtable.
        Will iterate through medication list through hashtable.
        Hashtable will return the category of medication.
        Will be simple at first, will introduce more complexity as a medication can serve more then one purpose.
        Final print will categorize medications by what disease/condition it is used for.

        Future features:
        Get it working on android (maybe iphone to)
        Seamless transition between the two with an optional account system, or just being able to transfer info between the two
        Timer for medications
        Link to drugs.com for each medication
        Spell check
        */
    }
}
