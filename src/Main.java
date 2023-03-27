import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    static String FILE_NAME = "db.csv";
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String command;
        do {
            System.out.print("Enter command: ");
            command = input.nextLine().trim().toLowerCase();
            String[] command_full = command.toString().split(" ",2);

            switch (command_full[0]) {
                case "print":
                    print();
                    break;
                case "add":
                    add(command_full[1]);
                    break;
                case "del":
                    del(command_full[1]);
                    break;
                case "edit":
                    edit(command_full[1]);
                    break;
                case "sort":
                    sort();
                    break;
                case "find":
                    find();
                    break;
                case "avg":
                    avg();
                    break;
                case "exit":
                    System.out.println("Exiting program...");
                    break;
                default:
                    System.out.println("Invalid command!");
            }
        } while (!command.equals("exit"));

        input.close();
    }

    private static void print() {
        // code to display all records in the db.csv file
    }

    public static void add(String field) {
        String[] fields = field.toString().split(";");
        if (fields.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        String id = "";
        try {
            id = fields[0];
            if (!id.matches("\\d{3}")) {
                System.out.println("wrong id");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("wrong id");
            return;
        }
        String city = fields[1].toLowerCase();
        city = city.substring(0, 1).toUpperCase() + city.substring(1);
        String date = fields[2];
        int days = 0;
        try {
            days = Integer.parseInt(fields[3]);
        } catch (NumberFormatException e) {
            System.out.println("wrong days");
            return;
        }
        double price = 0.0;
        try {
            price = Double.parseDouble(fields[4]);
        } catch (NumberFormatException e) {
            System.out.println("wrong price");
            return;
        }
        String vehicle = fields[5].toUpperCase();
        if (!vehicle.equals("PLANE") && !vehicle.equals("BUS") && !vehicle.equals("TRAIN") && !vehicle.equals("BOAT")) {
            System.out.println("wrong vehicle");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db.csv", true))) {
            writer.write(id + ";" + city + ";" + date + ";" + days + ";" + String.format("%.2f", price) + ";" + vehicle);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("error writing to file");
        }
        System.out.println("trip added");
    }

    public static void del(String tokens) {
        String id = tokens;

        // Check if id is a 3-digit integer
        if (!id.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }

        // Read in the contents of the file and remove the line with the given id
        List<String> lines;
        Path path = Paths.get("db.csv");
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("error reading file");
            return;
        }

        boolean idFound = false;
        for (Iterator<String> it = lines.iterator(); it.hasNext(); ) {
            String line = it.next();
            if (line.startsWith(id + ";")) {
                it.remove();
                idFound = true;
                break;
            }
        }

        // If the id was not found in the file, print an error message
        if (!idFound) {
            System.out.println("wrong id");
            return;
        }

        // Write the modified contents back to the file
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("error writing file");
        }

        System.out.println("deleted");
    }

    public static void edit(String query) {
        // split query string into fields
        String[] fields = query.split(";");

        // check number of fields
        if (fields.length != 6) {
            System.out.println("wrong field count");
            return;
        }

        // extract fields
        String idStr = fields[0];
        String city = fields[1].toLowerCase();
        // Update the city field with uppercase first letter of each word
        StringBuilder city_name = new StringBuilder();
        for (String word : city.split("\\s+")) {
            city_name.append(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ");
        }
        city = city_name.toString().trim();
        String date = fields[2];
        String daysStr = fields[3];
        String priceStr = fields[4];
        String vehicle = fields[5];

        // check id format
        if (!idStr.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }

        int id = Integer.parseInt(idStr);
        int days = Integer.parseInt(daysStr);
        double price = Double.parseDouble(priceStr);

        // check if trip with given id exists in database
        boolean found = false;
        try {
            File dbFile = new File("db.csv");
            Scanner scanner = new Scanner(dbFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tripFields = line.split(";");
                int tripId = Integer.parseInt(tripFields[0]);
                if (tripId == id) {
                    found = true;
                    break;
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("database file not found");
            return;
        }

        // output error message if trip with given id not found
        if (!found) {
            System.out.println("wrong id");
            return;
        }

        // update trip information in database
        try {
            File dbFile = new File("db.csv");
            File tempFile = new File("temp.csv");
            Scanner scanner = new Scanner(dbFile);
            PrintWriter writer = new PrintWriter(tempFile);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] tripFields = line.split(";");
                int tripId = Integer.parseInt(tripFields[0]);
                if (tripId == id) {
                    // update trip information
                    writer.println(id + ";" + city + ";" + date + ";" + days + ";" + String.format("%.2f", price) + ";" + vehicle);
                } else {
                    writer.println(line);
                }
            }

            scanner.close();
            writer.close();

            // delete original database file and rename temporary file to original file name
            dbFile.delete();
            tempFile.renameTo(dbFile);

            System.out.println("edited");
        } catch (FileNotFoundException e) {
            System.out.println("database file not found");
        }
    }

    private static void sort() {
        // code to sort records in the db.csv file by date
    }

    private static void find() {
        // code to find records in the db.csv file with a price lower or equal to the given price
    }

    private static void avg() {
        // code to calculate the average price of all records in the db.csv file
    }
//    public static void readDatabase() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
//            while ((line = reader.readLine()) != null) {
//                //read file
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return trips;
//    }
}
