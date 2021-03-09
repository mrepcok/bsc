package com.company;

import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    @AllArgsConstructor
    class Package{

        float weight;
        String postalCode;
        float price;

        @Override
        public String toString() {
            return String.format(Locale.US,"%s %.3f %.2f", postalCode, weight, price);
        }
    }

    @AllArgsConstructor
    class PriceLevel{
        float maxWeight;
        float price;
    }

    Map<Float, Package> packages = new TreeMap<>();

    public static void main(String[] args) throws FileNotFoundException {

        Main main = new Main();
        main.init(args);

    }

    private void init(String[] args) throws FileNotFoundException {


        Scanner scanner = null;


        TreeMap<Float, Float> priceLevels;


        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> print(),
                1, 1, TimeUnit.MINUTES);

        if (args.length>0){
            scanner = new Scanner(new File(args[0]));
        }
        if (args.length>1){
            priceLevels = loadFees(args[1]);
        } else {
            priceLevels = new TreeMap<>();
            priceLevels.put(0f,0f);
        }

        while(true){
            if(scanner!= null && scanner.hasNextLine()){
            } else {
                scanner = new Scanner(System.in);
                System.out.println(String.format("Loading of file %s finished.", args[0]));
                System.out.println("Enter package info, quit or print command:");
            }

            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                System.out.println("quit accepted");
                break;
            }
            else if(input.equalsIgnoreCase("print")) {
                print();
            }
            else {
                Pattern pattern = Pattern.compile("^([0-9]{1,11}(?:\\.[0-9]{1,3})?)\\s([0-9]{5})");
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()){

                    float weight = Float.valueOf(matcher.group(1));

                    //set the minimal price
                    float price = priceLevels.firstEntry().getValue();

                    //check for appropriate price level
                    if (priceLevels.floorEntry(weight) != null) {
                        price =  priceLevels.floorEntry(weight).getValue();
                    }

                    packages.put(weight, new Package(weight,matcher.group(2),price));
                }
                else{
                    System.out.println("Wrong input format, expected input is <weight: positive number, >0, maximal 3 decimal places, . (dot) as decimal separator><space><postal code: fixed 5 digits> ");
                }
            }
        };
    }

    TreeMap<Float,Float> loadFees(String fileName) throws FileNotFoundException {

        TreeMap<Float, Float> result = new TreeMap<>();

        Scanner feeScanner = new Scanner(new File(fileName));
        feeScanner.useLocale(Locale.US);
        while (feeScanner.hasNextLine()){
            float w = feeScanner.nextFloat();
            float p = feeScanner.nextFloat();
            result.put(w,p);
        }

        return result;
    }

    void print(){
        Iterator<Package> iterator = packages.values().iterator();

        while(iterator.hasNext()){
            System.out.println( iterator.next() );
        }
    }

}