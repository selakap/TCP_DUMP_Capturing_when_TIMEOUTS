package sample.project;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCP_DUMP implements Runnable {
    //java -jar TCP_DUMP_Capturing_when_TIMEOUTS-1.0-SNAPSHOT-jar-with-dependencies.jar path-to-config.properties-file

    static String tailCommand;
    static String TCPCommand;
    static String serchingKeyword;
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Start the TCP_DUMP program");
        String path = args[0];
        //String path = "/Users/selakapiumal/git/TCP_DUMP_Capturing_when_TIMEOUTS/config_files/config.properties";

        Properties prop = new Properties();
        File initialFile = new File(path);
        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(initialFile);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        if (inputStream != null) {
            try
            {
                prop.load(inputStream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        } else {
            System.out.println("config.property file is not found");
        }


        tailCommand = prop.getProperty("tailCommand");
        TCPCommand = prop.getProperty("TCPCommand");
        serchingKeyword = prop.getProperty("SerchingKeyword");

        decision();




    }
    static void decision() throws IOException, InterruptedException {

        System.out.println("decision got executed!");
        ExecutorService executor = Executors.newCachedThreadPool();
        String command = tailCommand;
        Process proc = Runtime.getRuntime().exec(command); //Tail the carbon log file

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while((line = reader.readLine()) != null) {
            if (line.contains(serchingKeyword)){ //if new line contains serchingKeyword
                System.out.print(line + "\n");
                Runnable worker = new TCP_DUMP(); //execute a new thread to capture TCP dump
                executor.execute(worker);

                //commandExecution();
            }

        }

        proc.waitFor();


    }

    @Override
    public void run() {

        System.out.println(Thread.currentThread().getName()+" (Start) run()");
        //String command = "jmap -dump:format=b,file=/Users/selakapiumal/Desktop/BE/dump.hprof 40598";
        String command = TCPCommand;
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command); //capture TCP dump
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while(true) {
            try {
                if (!((line = reader.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(line + "\n");

        }

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
