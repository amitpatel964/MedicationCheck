package com.medcheck;
import java.util.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

public class functions {
    // Creating list to hold patient's list of medications
    private ArrayList<String> medlist;
    // Creating hashmap to store medicine database
    private HashMap<String, ArrayList<String>> database;
    // Creating hashmap to store final list
    private HashMap<String, ArrayList<String>> toPrint;

    static Scanner input = new Scanner(System.in);

    public functions()
    {
        medlist = new ArrayList<>();
        database = new HashMap<>();
        toPrint = new HashMap<>();
    }

    // Intro message
    public void intro() {
        System.out.println("Hello!");
        System.out.println("This program is used to sort your medication list into the categories it belongs to");
        System.out.println( "Your primary care provider can customize the database or even make their own. " +
                "You can also make your own or just use the default database");
        System.out.println("Please note that while the default database includes many medications, " +
                "it does not include every single medication available");
        System.out.println("Furthermore, a medication may have many different brand names and while the database " +
                "has many of them, it does not every brand name");
        System.out.println("Finally, takes note that a medication may have multiple uses that may not be reflected " +
                "in the database. " + "Always discuss your medications and what they are used for with your " +
                "primary care provider.");
        System.out.println("This program offers a reference for your medications. " +
                "You can bring the list to your primary care provider and discuss what it may be used for");
    }

    // Asks if the user wants to import a table or create a new file
    public void tableOrFile()
    {
        System.out.println("Do you want to import a table or do you want to create a new table?");
        System.out.println("Type import for importing a table or create to make a new table");

        while (true)
        {
            String answer = input.nextLine();
            if (answer.equalsIgnoreCase("import"))
            {
                importTable();
                break;
            }
            else if (answer.equalsIgnoreCase("create"))
            {
                boolean whichToUse = ask();

                if (whichToUse)
                {
                    //Adding medications to ArrayList of user's medications via file
                    medlistAddFromFile("medlist.txt");
                    break;
                }
                else
                {
                    //Adding medications to ArrayList of user's medications via typing in
                    medlistAddFromDirectInput();
                    break;
                }
            }
            else
            {
                System.out.println("Invalid command");
            }
        }

    }

    // Imports a table made by this program or with the same formatting
    private void importTable()
    {
        System.out.println("Please type the name of the table you wish to add");
        System.out.println("Please note that the table should have the same formatting as the files " +
                "made by this program");

        String tableName = input.nextLine();

        try (Scanner scanner = new Scanner(Paths.get(tableName))) {
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine().toLowerCase();

                // Splits the line to get a key and a string of medicine names
                String[] line = nextLine.split(": ", 2);

                // Splits string of medications into separate entries
                String[] medications = line[1].split(", ");

                // Removes [ from first entry
                medications[0] = medications[0].substring(1);

                // Index is saved for ease of readability
                int index = medications.length-1;

                // Removes ] from final entry
                medications[index] = medications[index].substring(0, medications[index].length()-1);

                // Begins adding to the medication list
                toPrint.putIfAbsent(line[0], new ArrayList<>());

                for (String medicine: medications)
                {
                    toPrint.get(line[0]).add(medicine.toLowerCase());
                }

            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Ask user if they want to use a file or to type in their medication list
    // True = use file, false = type in
    public boolean ask() {
        System.out.println(
                "Would you like to type your medication names now or are they in a .txt file? " +
                        "Answer with type or file respectively.");
        String answer = input.nextLine().toLowerCase();

        if (answer.equals("type")) {
            return false;
        } else {
            return true;
        }

    }

    // Adding medications to ArrayList of user's medications via file
    public void medlistAddFromFile(String inputMedlist) {
        try (Scanner scanner = new Scanner(Paths.get(inputMedlist))) {
            while (scanner.hasNextLine()) {
                String nextLine = scanner.nextLine();
                medlist.add(nextLine.toLowerCase());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Adding medications to ArrayList of user's medications via typing in
    public void medlistAddFromDirectInput() {
        System.out.println("Type in the name of your medications one be one. Type stop once finished");
        while (true) {
            String inputString = input.nextLine().toLowerCase();
            if (inputString.equals("stop")) {
                break;
            }
            medlist.add(inputString.toLowerCase());
        }
    }

    // Adding database medications to hashmap of medications
    public void databaseAdd(String inputDatabase) {
        String[] temp;
        try (Scanner scanner = new Scanner(Paths.get(inputDatabase))) {
            while (scanner.hasNextLine()) {
                String nextLine2 = scanner.nextLine();
                String toLower = nextLine2.toLowerCase();
                temp = toLower.split(",");
                database.putIfAbsent(temp[0], new ArrayList<>());

                for (int i = 1; i < temp.length; i++) {
                    database.get(temp[0]).add(temp[i]);
                }
            }

            // Catch all category if a medication does not match a category
            database.putIfAbsent("other", new ArrayList<>());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Check medication list against database
    public void compare() {
        // Check to see if medication needs to go in the "Other section"
        ArrayList<String> checkList = new ArrayList<>();
        for (String s : medlist) {
            // for (HashMap.Entry<String, ArrayList<String>> type: database.entrySet())
            boolean check = false;
            int counter = 0;
            for (String type : database.keySet()) {
                checkList = database.get(type);
                if (checkList.contains(s)) {
                    toPrint.putIfAbsent(type, new ArrayList<>());
                    toPrint.get(type).add(s);
                    check = true;
                    counter++;
                }
            }

            if (counter > 1) {
                whichCategory(s);
            }

            // If no matching categories are found, the medication is put in the "other" category
            if (!check) {
                whatIsMedicineUsedFor(s);
            }
        }
    }

    // If medication is not the csv file, this method allows the user to put it in a category of their own
    public void whatIsMedicineUsedFor(String medicine)
    {
        System.out.println(medicine + " was not found in the database. Do you know what it is used for? " +
                "Type yes if you know and then enter the category. Otherwise, type no");

        while (true)
        {
            String answer = input.nextLine().toLowerCase();
            if (answer.equals("yes"))
            {
                System.out.println("What is " + medicine + " used for?");
                String category = input.nextLine().toLowerCase();

                toPrint.putIfAbsent(category, new ArrayList<>());
                toPrint.get(category).add(medicine);
                break;
            }
            else if (answer.equals("no"))
            {
                toPrint.putIfAbsent("other", new ArrayList<>());
                toPrint.get("other").add(medicine);
                break;
            }
            System.out.println("Please type yes or no");
        }
    }

    // If a medication is found in multiple categories, this allows them to choose which ones they are using
    // the medication for
    public void whichCategory(String medicine)
    {
        System.out.println(medicine + " has multiple uses. Do you what you are using the medicine for? Type yes for " +
                "a category when prompted if you are using it for that, no if not, or not sure if you are unsure");
        for (String name : toPrint.keySet())
        {
            ArrayList<String> checker = toPrint.get(name);
            if (checker.contains(medicine))
            {
                label:
                while (true)
                {
                    System.out.print(name + ": ");
                    String choice = input.nextLine();
                    switch (choice.toLowerCase()) {
                        case "yes":
                            break label;
                        case "no":
                            checker.remove(medicine);
                            break label;
                        case "not sure":
                            ListIterator<String> iterator = checker.listIterator();
                            while (iterator.hasNext()) {
                                String nextLine = iterator.next();
                                if (nextLine.equals(medicine)) {
                                    iterator.set(medicine + " (unsure)");
                                }
                            }
                            break label;
                    }
                }
            }
        }
    }

    // Removes any empty categories from the final table
    public void isItEmpty()
    {
        Iterator<Map.Entry<String, ArrayList<String>>> iter = toPrint.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = iter.next();
            if(entry.getValue().isEmpty()){
                iter.remove();
            }
        }
    }

    // Print out final list
    public void print() {
        System.out.println("Printing table...");

        for (String name : toPrint.keySet()) {
            System.out.println(name + ": " + toPrint.get(name).toString());
        }

        System.out.println("Table printed");
    }

    // Puts the table into a .txt file
    public void outputFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("sorted_list.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for (String name : toPrint.keySet()) {
            assert writer != null;
            writer.println(name + ": " + toPrint.get(name).toString());
            /*
            writer.print(name + ": [");
            ArrayList<String> list = toPrint.get(name);
            for (int i = 0; i < list.size(); i++)
            {
                writer.print(list.get(i) + ", ");
            }
            writer.println("]");
            */
        }
        assert writer != null;
        writer.close();

        System.out.println("Table file created called sorted_list.txt");

    }

    // Puts the user in a loop that gives them access to multiple functions that they can use
    public void commandLine()
    {
        commandLineIntro();
        while (true)
        {
            System.out.print("Command: ");
            String userChoice = input.nextLine();

            // Change category a medication is in
            if (userChoice.equals("change"))
            {
                System.out.println("For which medication?");
                System.out.print("Medicine: ");
                String medicine = input.nextLine();
                boolean check1 = removeCategory(medicine);
                if (!check1)
                {
                    System.out.println("That category or medicine was not found");
                    continue;
                }
                addCategory(medicine);
                isItEmpty();
            }
            // Remove medication from a category
            else if (userChoice.equals("remove"))
            {
                System.out.println("For which medication?");
                System.out.print("Medicine: ");
                String medicine = input.nextLine();
                boolean check2 = removeCategory(medicine);
                if (!check2)
                {
                    System.out.println("That category or medicine was not found");
                    continue;
                }
                isItEmpty();
            }
            // Add medication to a category
            else if (userChoice.equals("add"))
            {
                System.out.println("For which medication?");
                System.out.print("Medicine: ");
                String medicine = input.nextLine();
                addCategory(medicine);
                System.out.println();
            }
            // Edit medication name
            else if (userChoice.equals("edit"))
            {
                System.out.println("For which medication?");
                System.out.print("Medicine: ");
                String medicine = input.nextLine();
                editMedication(medicine);
            }
            else if (userChoice.equals("print"))
            {
                print();
            }
            else if (userChoice.equalsIgnoreCase("stop"))
            {
                System.out.println("Thank you for using this program!");
                break;
            }
            else if (userChoice.equalsIgnoreCase("help") || userChoice.equalsIgnoreCase("repeat"))
            {
                commandLineIntro();
            }
            else if (userChoice.equalsIgnoreCase("file"))
            {
                outputFile();
            }
            else
            {
                System.out.println("Invalid input");
            }
        }
    }

    // Remove medication from a category
    public boolean removeCategory(String medicine)
    {
        System.out.println("Which category do you want to remove " + medicine + " from?");
        System.out.print("Category: ");
        String category = input.nextLine();
        category = category.toLowerCase();

        for (String name : toPrint.keySet())
        {
            if (name.equals(category))
            {
                ArrayList<String> list = toPrint.get(name);
                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).equalsIgnoreCase(medicine))
                    {
                        list.remove(i);
                        System.out.println(medicine + " removed from " + category);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Add medication to a category
    public void addCategory(String medicine)
    {
        System.out.println("Which category do you want to add " + medicine + " to?");
        System.out.print("Category: ");
        String category = input.nextLine();
        category = category.toLowerCase();

        toPrint.putIfAbsent(category, new ArrayList<>());
        toPrint.get(category).add(medicine);
        System.out.println(medicine + " added to " + category);
    }

    // Edit medication name
    public void editMedication(String medicine)
    {
        for (String name : toPrint.keySet())
        {
            ArrayList<String> checker = toPrint.get(name);
            if (checker.contains(medicine))
            {
                ListIterator<String> iterator = checker.listIterator();
                while(iterator.hasNext())
                {
                    String nextLine = iterator.next();
                    if (nextLine.equals(medicine))
                    {
                        System.out.println("Please type in the new medication name");
                        System.out.print("Name: ");
                        String newMedicine = input.nextLine();
                        iterator.set(newMedicine);

                        System.out.println(medicine + " changed to " + newMedicine);
                    }
                }
                break;
            }
        }
    }

    // Prints the commandLine method intro
    public void commandLineIntro()
    {
        System.out.println("Type change if you want to change the category a medication is in");
        System.out.println("Type remove if you want to remove a medication from a category");
        System.out.println("Type add if you want to add a medication to a category");
        System.out.println("Type edit if you want to edit a medication name");
        System.out.println("Type print if you want print the final list of medications");
        System.out.println("Type file if you want to put the final list into a .txt file");
        System.out.println("Type stop to exit the program");
        System.out.println("Type help or repeat to repeat these commands");
    }

}
