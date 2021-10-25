import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int errors = 0;

        HashMap<String, String> map = new HashMap<>();

        File mapFile = new File("C:\\5 semester\\Big Data\\datasett\\src\\airport-codes_csv.csv");

        File countries = new File("countries.csv");

        // Leser inn flyplassene
        try {
            Scanner scanner = new Scanner(mapFile);

            String line = scanner.nextLine();
            String[] fields;

            ArrayList<String> nodes = new ArrayList<>();
            FileWriter fileWriter = new FileWriter("countries.csv");

            while (scanner.hasNextLine()) {

                line = scanner.nextLine();

                fields = line.split(",");

                if (!(fields.length == 13)) {
                    errors++;
                    continue;
                }

                map.put(fields[0], fields[5]);

                if (!nodes.contains(fields[5])) {
                    nodes.add(fields[5]);
                    fileWriter.write(fields[5]);
                    System.out.println(fields[5]);
                }
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Har lest inn data");

        File datasett = new File("C:\\5 semester\\Big Data\\datasett\\src\\flights4.csv\\flightlist_20200401_20200430.csv");
/*
        File out = new File("out.csv");
        try {
            boolean error = false;

            Scanner scanner = new Scanner(datasett);

            String line = scanner.nextLine();
            String[] fields;

            FileWriter writer = new FileWriter("out.csv");

            while (scanner.hasNextLine()) {

                line = scanner.nextLine();

                fields = line.split(",");

                fields[5] = map.get(fields[5]);
                fields[6] = map.get(fields[6]);


                String temp = "";

                for (String f : fields) {
                    temp += f + ",";
                }
                temp = temp.substring(0, temp.length() - 1);

                temp = temp + "\n";

                System.out.print(temp);

                writer.write(temp);


            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println(errors);
*/
    }
}
