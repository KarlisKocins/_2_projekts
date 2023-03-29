import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static String FILE_NAME = "db.csv";
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String command = null;
            do {
                try {
                System.out.print("Enter command: ");
                command = input.nextLine().trim().toLowerCase();
                String[] command_full = command.toString().split(" ", 2);

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
                        find(command_full[1]);
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
                }catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Wrong command!");
                }
            } while (!command.equals("exit"));
        
        input.close();
    }

    private static void print() {
        // code to display all records in the db.csv file
        String[][][] array3D = read_csv(FILE_NAME, 6);
        // print the 3D array
        System.out.println("------------------------------------------------------------");
        System.out.format("%-4s%-21s%-11s%-6s%-10s%-8s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < array3D.length; i++) {
            System.out.format("%-4s%-21s%-11s%-6s%-10s%-8s%n", array3D[i][0][0], array3D[i][1][0], array3D[i][2][0],
                    array3D[i][3][0], array3D[i][4][0], array3D[i][5][0]);
        }
        System.out.println("------------------------------------------------------------");
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

        String city = fields[1];
        String[] cityWords = city.split("\\s");
        StringBuilder cityCapitalized = new StringBuilder();
        for (String word : cityWords) {
            cityCapitalized.append(Character.toUpperCase(word.charAt(0)) + word.substring(1)).append(" ");
        }
        city = cityCapitalized.toString().trim();

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

        String id = fields[0];
        String city = fields[1];

        String[] cityWords = city.split("\\s");
        StringBuilder cityCapitalized = new StringBuilder();
        for (String word : cityWords) {
            cityCapitalized.append(Character.toUpperCase(word.charAt(0)) + word.substring(1)).append(" ");
        }
        city = cityCapitalized.toString().trim();

        String date = fields[2];
        String days = fields[3];
        String price = fields[4];
        String vehicle = fields[5].toUpperCase();
        if (!vehicle.equals("PLANE") && !vehicle.equals("BUS") && !vehicle.equals("TRAIN") && !vehicle.equals("BOAT")) {
            System.out.println("wrong vehicle");
            return;
        }

        // Check if id is a 3-digit integer
        if (!id.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }

        // Read in the contents of the file and update the line with the given id
        List<String> lines;
        Path path = Paths.get("db.csv");
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("error reading file");
            return;
        }

        boolean idFound = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.startsWith(id + ";")) {
                // update the fields that are not empty
                String[] existingFields = line.split(";");
                if (!city.equals("")) {
                    existingFields[1] = city;
                }
                if (!date.equals("")) {
                    existingFields[2] = date;
                }
                if (!days.equals("")) {
                    existingFields[3] = days;
                }
                if (!price.equals("")) {
                    existingFields[4] = price;
                }
                if (!vehicle.equals("")) {
                    existingFields[5] = vehicle;
                }
                lines.set(i, String.join(";", existingFields));
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

        System.out.println("edited");
    }

    public static void sort() {
        String[][][] array3D = read_csv(FILE_NAME, 6);

        Arrays.sort(array3D, (a, b) -> {
            String[] date1 = a[2][0].split("/");
            String[] date2 = b[2][0].split("/");
            int year1 = Integer.parseInt(date1[2]);
            int year2 = Integer.parseInt(date2[2]);
            int month1 = Integer.parseInt(date1[1]);
            int month2 = Integer.parseInt(date2[1]);
            int day1 = Integer.parseInt(date1[0]);
            int day2 = Integer.parseInt(date2[0]);
            if (year1 != year2) {
                return year1 - year2;
            } else if (month1 != month2) {
                return month1 - month2;
            } else {
                return day1 - day2;
            }
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db.csv"))) {
            for (int i = 0; i < array3D.length; i++) {
                writer.write(array3D[i][0][0] + ";" + array3D[i][1][0] + ";" + array3D[i][2][0] + ";" + array3D[i][3][0] + ";" + array3D[i][4][0] + ";" + array3D[i][5][0]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("error writing to file");
        }
        System.out.println("sorted");
    }

    public static void find(String tokens) {
        String[] fields = tokens.split(" ");
        double targetPrice = Double.parseDouble(fields[0]);

        String[][][] trips = read_csv(FILE_NAME, 6);
        boolean found = false;

        System.out.println("------------------------------------------------------------");
        System.out.format("%-4s%-21s%-11s%-6s%-10s%-8s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");

        for (int i = 0; i < trips.length; i++) {
            double price = Double.parseDouble(trips[i][4][0]);
            if (price <= targetPrice) {
                System.out.format("%-4s%-21s%-11s%-6s%-10s%-8s%n",
                        trips[i][0][0], trips[i][1][0], trips[i][2][0], trips[i][3][0], trips[i][4][0], trips[i][5][0]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No trips found.");
        }
        System.out.println("------------------------------------------------------------");
    }

    private static void avg() {
        String[][][] array3D = read_csv(FILE_NAME, 6);

        // calculate average price
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < array3D.length; i++) {
            try {
                double price = Double.parseDouble(array3D[i][4][0]);
                sum += price;
                count++;
            } catch (NumberFormatException e) {
                // ignore malformed prices
            }
        }
        if (count == 0) {
            System.out.println("No valid prices found.");
        } else {
            double avg = sum / count;
            System.out.printf("Average price: %.2f\n", avg);
        }
    }

    private static String[][][] read_csv(String filename, int collums) {
        String line;
        int rowCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[][][] array3D = new String[rowCount][collums][];
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(";");
                for (int j = 0; j < 6; j++) {
                    array3D[i][j] = new String[]{row[j]};
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array3D;
    }
}
