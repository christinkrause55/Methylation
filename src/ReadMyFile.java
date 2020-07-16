import java.io.*;

public class ReadMyFile{
    public static void main(String[] args) throws Exception{

        File file = new File(args[0]);
        BufferedReader buffReader = new BufferedReader(new FileReader(file));

        String str;
        String header = buffReader.readLine().replace("\"","");
        System.out.println(header + "\t" + "animals_g1" + "\t" + "animals_g2" + "\t" + "mean_g1" + "\t" + "mean_g2");

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

            int g1 = Integer.parseInt(args[1]);
            int g2 = Integer.parseInt(args[2]);
            if(giveMeasurements(animals_g1) >= g1 && giveMeasurements(animals_g2) >= g2){
                System.out.println(str.replace(",",".").replace("NA",".") + "\t" + animals_g1_m + "\t" + animals_g2_m + "\t" + animals_g1_mean + "\t" + animals_g2_mean);
            }
        }

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