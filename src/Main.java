import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    static String FILE_NAME = "db.csv";
    public static void main(String[] args) {

        //region Scanner and asks for input
        Scanner input = new Scanner(System.in);
        String command = null;
        System.out.print("Enter command: ");
        //endregion

        //region Main switch function
        do {
            try {
                command = input.nextLine().trim().toLowerCase();
                String[] command_full = command.split(" ", 2);

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
                        System.out.println("wrong command");
                }
            }catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Wrong command!");
            }
        } while (!Objects.equals(command, "exit"));

        input.close();
        //endregion

    }

    private static void print() {

        //region Call read_csv function
        String[][][] array3D = read_csv(FILE_NAME);
        //endregion

        //region print the 3D array
        System.out.println("------------------------------------------------------------");
        System.out.format("%-4s%-21s%-11s%6s%10s%8s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");
        for (String[][] strings : array3D) {
            System.out.format("%-4s%-21s%-11s%-6s%-10s %-7s%n", strings[0][0], strings[1][0], strings[2][0],
                    String.format("%-6s", String.format("%6s", strings[3][0])),
                    String.format("%-10s", String.format("%10s", strings[4][0])), strings[5][0]);
        }
        System.out.println("------------------------------------------------------------");
        //endregion

    }

    public static void add(String field) {

        //region split input and check ID
        String[] fields = field.split(";");

        if (fields.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        //endregion

        //region ID verify
        String id;
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
        //endregion

        // region Check if id is existing!
        Path path = Paths.get(FILE_NAME);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            System.out.println("error reading file");
            return;
        }

        for (String line : lines) {
            String[] data = line.split(";");
            if (data[0].equals(id)) {
                System.out.println("id already exists");
                return;
            }
        }
        //endregion

        // region Check inputs!
        String city = fields[1];
        String[] cityWords = city.split("\\s");
        StringBuilder cityCapitalized = new StringBuilder();
        for (String word : cityWords) {
            cityCapitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        city = cityCapitalized.toString().trim();


        String date = fields[2];
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        Date dates;
        try{
            dates = dateFormat.parse(date);
        }catch (Exception e){
            System.out.println("wrong date");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(fields[3]);
        } catch (NumberFormatException e) {
            System.out.println("wrong day count");
            return;
        }
        double price;
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
        //endregion

        //region WriteToFile!
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db.csv", true))) {
            writer.write(id + ";" + city + ";" + date + ";" + days + ";" + String.format("%.2f", price) + ";" + vehicle);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("error writing to file");
        }
        //endregion

        System.out.println("added");
    }

    public static void del(String tokens) {

        //region Check if id is a 3-digit integer
        if (!tokens.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        //endregion

        //region Read in the contents of the file and remove the line with the given id
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
            if (line.startsWith(tokens + ";")) {
                lines.remove(i);
                idFound = true;
                break;
            }
        }
        if (!idFound) {
            System.out.println("wrong id");
            return;
        }
        //endregion

        //region Write the modified contents back to the file
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("error writing file");
        }
        //endregion

        System.out.println("deleted");
    }

    public static void edit(String query) {

        //region split query string into fields
        String[] fields = query.split(";");
        //endregion

        //region check number of fields
        if (fields.length != 6) {
            System.out.println("wrong field count");
            return;
        }
        //endregion

        //region Verify and set iD
        String id;
        try{
            id = fields[0];
            if (!id.matches("\\d{3}")) {
                System.out.println("wrong id");
                return;
            }
        }catch (NumberFormatException e){
            System.out.println("wrong id");
            return;
        }
        //endregion

        //region Verify and set inputs
        String city = fields[1];
        if(!Objects.equals(city, "")){
            String[] cityWords = city.split("\\s");
            StringBuilder cityCapitalized = new StringBuilder();
            for (String word : cityWords) {
                cityCapitalized.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
            city = cityCapitalized.toString().trim();
        }


        String date_var = fields[2];
        if (!Objects.equals(date_var, "")) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            formatter.setLenient(false);
            Date date;
            try {
                date = formatter.parse(date_var);
                if (!formatter.format(date).equals(date_var)) {
                    System.out.println("wrong date");
                    return;
                }
            } catch (Exception e) {
                System.out.println("wrong date");
                return;
            }
        }
        String days = fields[3];
        if (!Objects.equals(days, "")){
            try {
                int day = Integer.parseInt(days);
            } catch (NumberFormatException e) {
                System.out.println("wrong days");
                return;
            }
        }
        String prices = fields[4];
        Double price = 0.00d;
        if(!Objects.equals(prices, "")){
            try {
                price = Double.parseDouble(prices);
            } catch (NumberFormatException e) {
                System.out.println("wrong price");
                return;
            }
        }
        String vehicle = fields[5].toUpperCase();
        if (!vehicle.equals("PLANE") && !vehicle.equals("BUS") && !vehicle.equals("TRAIN") && !vehicle.equals("BOAT") && !vehicle.equals("")) {
            System.out.println("wrong vehicle");
            return;
        }
        //endregion

        //region Read in the contents of the file and update the line with the given id
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
                if (!date_var.equals("")) {
                    existingFields[2] = date_var;
                }
                if (!days.equals("")) {
                    existingFields[3] = days;
                }
                if (!prices.equals("")) {
                    existingFields[4] = String.format("%.2f", price);
                }
                if (!vehicle.equals("")) {
                    existingFields[5] = vehicle;
                }
                lines.set(i, String.join(";", existingFields));
                idFound = true;
                break;
            }
        }
        //endregion

        //region If the id was not found in the file, print an error message
        if (!idFound) {
            System.out.println("wrong id");
            return;
        }
        //endregion

        //region Write the modified contents back to the file
        try {
            Files.write(path, lines);
        } catch (IOException e) {
            System.out.println("error writing file");
        }
        //endregion

        System.out.println("changed");
    }

    public static void sort() {

        //region Call read_csv function
        String[][][] array3D = read_csv(FILE_NAME);
        //endregion

        //region Sorter!
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
        //endregion

        //region Write to file!
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("db.csv"))) {
            for (String[][] strings : array3D) {
                writer.write(strings[0][0] + ";" + strings[1][0] + ";" + strings[2][0] + ";" + strings[3][0] + ";" + strings[4][0] + ";" + strings[5][0]);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("error writing to file");
        }
        //endregion

        System.out.println("sorted");
    }

    public static void find(String tokens) {

        //region Find the "find" value
        String[] fields = tokens.split(" ");
        //endregion

        //region Convert it to float and verify it!
        double targetPrice = 0.00d;
        try {
            targetPrice = Double.parseDouble(fields[0]);
        }catch (NumberFormatException e){
            System.out.println("Wrong find num: " + fields[0]);
            return;
        }
        //endregion

        //region Call read_csv function
        String[][][] trips = read_csv(FILE_NAME);
        //endregion

        //region print the table with the found values!
        boolean found = false;
        System.out.println("------------------------------------------------------------");
        System.out.format("%-4s%-21s%-11s%6s%10s%8s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");

        for (String[][] trip : trips) {
            double price = Double.parseDouble(trip[4][0]);
            if (price <= targetPrice) {
                System.out.format("%-4s%-21s%-11s%-6s%-10s %-7s%n", trip[0][0], trip[1][0], trip[2][0],
                        String.format("%-6s", String.format("%6s", trip[3][0])),
                        String.format("%-10s", String.format("%10s", trip[4][0])), trip[5][0]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No trips found.");
        }
        System.out.println("------------------------------------------------------------");
        //endregion

    }

    private static void avg() {

        //region Call read_csv funtion
        String[][][] array3D = read_csv(FILE_NAME);
        //endregion

        //region calculate average price
        double sum = 0.0;
        int count = 0;
        for (String[][] strings : array3D) {
            try {
                double price = Double.parseDouble(strings[4][0]);
                sum += price;
                count++;
            } catch (NumberFormatException e) {
                // ignore malformed prices
            }
        }
        //endregion

        //region Cauculate avrage!
        if (count == 0) {
            System.out.println("No valid prices found.");
        } else {
            double avg = sum / count;
            System.out.printf("average=%.2f\n", avg);
        }
        //endregion

    }

    private static String[][][] read_csv(String filename) {

        //region Read the row count of the file db.csv
        String line;
        int rowCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while (br.readLine() != null) {
                rowCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion

        //region make a new 3D array that later returns
        String[][][] array3D = new String[rowCount][6][];
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
        //endregion

        //region Return array
        return array3D;
        //endregion

    }
}
