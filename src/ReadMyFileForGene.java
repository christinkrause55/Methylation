import java.io.*;
/**
 * Class script for generation of an summary and input file for DMR calling via metilene.
 */
public class ReadMyFileForGene{
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

        System.out.println(header + "animals_g1" + "\t" + "animals_g2" + "\t" + "mean_g1" + "\t" + "mean_g2");

        // Einlesen der Datei
        while ((str = buffReader.readLine()) != null){
            String[] divide = str.split("\t");

            double[] animals_g1 = new double[(divide.length-3)/2];
            double[] animals_g2 = new double[(divide.length-3)/2];

            for(int i=0; i < animals_g1.length; i++){
                if(!divide[i+3].equals("NA")){
                    animals_g1[i] = Double.parseDouble(divide[i+3].replace(",","."));
                } else{
                    animals_g1[i] = -1;
                }
            }

            for(int i=0; i < animals_g2.length; i++){
                if(!divide[i+3+animals_g1.length].equals("NA")){
                    animals_g2[i] = Double.parseDouble(divide[i+3+animals_g1.length].replace(",","."));
                } else{
                    animals_g2[i] = -1;
                }
            }

            String animals_g1_m = Integer.toString(giveMeasurements(animals_g1));
            String animals_g2_m = Integer.toString(giveMeasurements(animals_g2));
            String animals_g1_mean = Double.toString(giveMean(animals_g1));
            String animals_g2_mean = Double.toString(giveMean(animals_g2));

            // Flags for input handling
            int g1 = Integer.parseInt(args[1]);
            int g2 = Integer.parseInt(args[2]);
            boolean rightChrom = false;
            boolean chromIndependent= false;
            boolean inPlace = false;
            boolean placeIndependent = false;

            if(!args[3].equals("-")){
                rightChrom = divide[0].equals(args[3]);
            }else if (args[3].equals("-")){
                chromIndependent = true;
            }

            if(!args[4].equals("-") && !args[5].equals("-") && !divide[1].equals("NA")){
                inPlace = Double.parseDouble(divide[1]) > Integer.parseInt(args[4]) && Double.parseDouble(divide[1]) < Integer.parseInt(args[5]);
            }else if(args[4].equals("-") && args[5].equals("-") && !divide[1].equals("NA")) {
                placeIndependent = true;
            }

            if(giveMeasurements(animals_g1) >= g1 && giveMeasurements(animals_g2) >= g2 && (inPlace || placeIndependent) && (rightChrom || chromIndependent)){
                System.out.println(str.replace(",",".").replace("NA",".") + "\t" + animals_g1_m + "\t" + animals_g2_m + "\t" + animals_g1_mean + "\t" + animals_g2_mean);
            }
        }

    }

    /**
     * Assesses the number of animals/values
     * @param a Array with animals
     * @return Number of values within a
     */
    public static int giveMeasurements(double[] a){
        int number = 0;
        for(int i=0; i < a.length; i++){
            if(a[i] != -1){
                number++;
            }
        }
        return number;
    }

    /**
     * Calculates the mean value of an array
     * @param a An array with values
     * @return The mean value of a
     */
    public static double giveMean(double[] a){
        double mean = 0;
        for(int i=0; i < a.length; i++){
            if(a[i] != -1){
                mean = mean + a[i];
            }
        }
        return mean/giveMeasurements(a);
    }

}