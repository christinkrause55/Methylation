import java.io.*;

/**
 * This class generates a filter for single CpG level
 * Call as java ReadMyFileforSingleCpG FileName minAnimals1 minAnimals2 StdDev(as - for 1 sigma or 0.1 for 10%) difference(0.1 for 10%)
 */
public class ReadMyFileForSingleCpGSingleCohort{
    public static void main(String[] args) throws Exception{

        File file = new File(args[0]);
        BufferedReader buffReader = new BufferedReader(new FileReader(file));

        String str;
        String headerArray[] = buffReader.readLine().replace("\"","").split("\t");
        String header = headerArray[0] + "\t" + "pos" + "\t" + headerArray[2] + "\t";

        int j = 0;
        for(int i = 0; i < (headerArray.length-3)/2; i++){
            header += "g1_" + headerArray[i+3] + "\t";
            j = i+3;
        }
        for(int i = j+1; i < headerArray.length; i++){
            header += "g2_" + headerArray[i] + "\t";
        }

        // Prep of header
        System.out.println(header + "animals_g1" + "\t" + "animals_g2" + "\t" + "mean_g1" + "\t" + "mean_g2" + "\t" + "stabw_g1" + "\t" + "stabw_g2" );

        // Read-in data
        while ((str = buffReader.readLine()) != null) {
            String[] divide = str.split("\t");

            double[] animals_g1 = new double[(divide.length - 3) / 2];
            double[] animals_g2 = new double[(divide.length - 3) / 2];

            for (int i = 0; i < animals_g1.length; i++) {
                if (!divide[i + 3].equals("NA")) {
                    animals_g1[i] = Double.parseDouble(divide[i + 3].replace(",", "."));
                } else {
                    animals_g1[i] = -1;
                }
            }

            for (int i = 0; i < animals_g2.length; i++) {
                if (!divide[i + 3 + animals_g1.length].equals("NA")) {
                    animals_g2[i] = Double.parseDouble(divide[i + 3 + animals_g1.length].replace(",", "."));
                } else {
                    animals_g2[i] = -1;
                }
            }

            String animals_g1_m = Integer.toString(giveMeasurements(animals_g1));
            String animals_g2_m = Integer.toString(giveMeasurements(animals_g2));
            String animals_g1_mean = Double.toString(giveMean(animals_g1));
            String animals_g2_mean = Double.toString(giveMean(animals_g2));
            String animals_g1_stabw = Double.toString(standardDeviation(animals_g1));
            String animals_g2_stabw = Double.toString(standardDeviation(animals_g2));

            int g1 = Integer.parseInt(args[1]);
            int g2 = Integer.parseInt(args[2]);
            double distanceChow = -1;
            double distanceHfd = -1;

            // Get information about distribution of values
            // - means 1 sigma
            if (!args[3].equals("-")) {
                distanceChow = Double.parseDouble(args[3]);
                distanceHfd = Double.parseDouble(args[3]);
            } else {
                distanceChow = 1.5*standardDeviation(animals_g1);
                distanceHfd = 1.5*standardDeviation(animals_g2);
            }

            boolean flagG1 = false;
            boolean flagG2 = false;

            if (distanceChow != -1 && distanceHfd != -1) {
                flagG1 = isInInterval(animals_g1, distanceChow);
                flagG2 = isInInterval(animals_g2, distanceHfd);
            }

            // Get information about mean difference between both groups
            boolean meanFlag = false;
            double absoluteDiff = Math.abs(giveMean(animals_g1) - giveMean(animals_g2));
            if(absoluteDiff > Double.parseDouble(args[4])){
                meanFlag = true;
            }

            if (meanFlag && flagG1 && flagG2 && giveMeasurements(animals_g1) >= g1 && giveMeasurements(animals_g2) >= g2) {
                System.out.println(str.replace(",", ".").replace("NA", ".") + "\t" + animals_g1_m + "\t" + animals_g2_m + "\t" + animals_g1_mean + "\t" + animals_g2_mean
                        + "\t" + animals_g1_stabw + "\t" + animals_g2_stabw);
            }
        }
    }

    public static boolean isInInterval(double[] a, double interval){
        double mean = giveMean(a);
        double lower = mean-interval;
        double upper = mean+interval;
        for(double i:a){
            if(!(i < upper && i>lower)){
                return false;
            }
        }
        return true;
    }

    public static double[] concatMe(double[] a, double[] b){
        double[] result = new double[a.length+b.length];
        int i=0;
        for (i=0; i<a.length;i++){
            result[i] = a[i];
        }
        int j=0;
        for(i=i;i<result.length;i++){
            result[i] = b[j];
            j++;
        }
        return result;
    }

    public static double standardDeviation(double[] a){
        double mean = giveMean(a);
        double varianz = 0;
        for (double i: a) {
            if(i != -1) {
                varianz += Math.pow(i - mean, 2.0);
            }
        }
        varianz = varianz/giveMeasurements(a);
        return Math.sqrt(varianz);
    }

    public static double giveMean(double[] a){
        double mean = 0;
        for(int i=0; i < a.length; i++){
            if(a[i] != -1){
                mean = mean + a[i];
            }
        }
        return mean/giveMeasurements(a);
    }

    public static int giveMeasurements(double[] a){
        int number = 0;
        for(int i=0; i < a.length; i++){
            if(a[i] != -1){
                number++;
            }
        }
        return number;
    }
}