package bunarcticfloof.FireHost;
import java.io.*;
import java.util.Scanner;
public class FireHost {
    
    private static Scanner scanner;
    private static Process process;
    private static boolean running;
    
    public static void main(String[] args) {
        running = true;
        
        System.out.println("Welcome to FireHost by Bun_Bo_Hue");
        System.out.println("Type '?' or 'help' without the quotes to view list of commands.");
    
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if(process != null)
                process.destroy();
            scanner.close();
            System.out.println("FireHost > Thanks for using FireHost!");
        }));
        
        scanner = new Scanner(System.in);
        while(running) {
            System.out.print("> ");
            handleCommand(scanner.nextLine().split(" "));
        }
    }
    
    private static void handleCommand(String[] command) {
        if(command[0].equalsIgnoreCase("help") || command[0].equalsIgnoreCase("?")) {
            System.out.println("Help");
            System.out.println("Firehost command");
            System.out.println("run <path> <jar> : Allows you to run command, for example: 'run bukkit.jar 6144M'.");
            System.out.println("stop : Allows you to stop FireHost.");
            System.out.println("Console firehost access");
            System.out.println("firehost kill : Allows you to kill the server process.");
        }else if(command[0].equalsIgnoreCase("run")) {
            if(command.length > 2) {
                try{
                    String[] args = new String[]{"/usr/bin/java", "-Xmx" + command[2], "-Xms" + "128M", "-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled", "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", "-XX:+DisableExplicitGC", "-XX:+AlwaysPreTouch", "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40", "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20", "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4", "-XX:InitiatingHeapOccupancyPercent=15", "-XX:G1MixedGCLiveThresholdPercent=90", "-XX:G1RSetUpdatingPauseTimePercent=5", "-XX:SurvivorRatio=32", "-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1", "-Dusing.aikars.flags=https://mcflags.emc.gs", "-Daikars.new.flags=true", "-Djline.terminal=jline.UnsupportedTerminal", "-XX:+PerfDisableSharedMem", "-jar", command[1], "nogui"};
                    process = Runtime.getRuntime().exec(args);
                    System.out.println("FireHost > Command executed!");
                    handleProcess();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }else{
                System.out.println("FireHost > Error");
            }
        }else if(command[0].equalsIgnoreCase("stop")) {
            System.out.println("FireHost > Thanks for using FireHost!");
            System.exit(0);
        }
    }
    
    private static void handleProcess() {
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Thread outputThread = new Thread(() -> {
            String outputLine;
            try{
                while((outputLine = outputReader.readLine()) != null)
                    System.out.println(outputLine);
            }catch(IOException e) {
                e.printStackTrace();
            }
        });
        outputThread.start();
        
        Thread errorThread = new Thread(() -> {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String errorLine;
            try{
                while((errorLine = errorReader.readLine()) != null)
                    System.out.println(errorLine);
            }catch(IOException e) {
                e.printStackTrace();
            }
        });
        errorThread.start();
        
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(process.getOutputStream());
        while(process.isAlive()) {
            try{
                String line = scanner.nextLine();
                if(line.startsWith("firehost")) {
                    String[] command = line.split(" ");
                    
                    if(command.length > 1 && command[1].equalsIgnoreCase("kill")) {
                        process.destroy();
                        process = null;
                        System.out.println("FireHost > The process was killed!");
                        return;
                    }
                    
                    System.out.println("FireHost > Unknown command");
                }else{
                    outputStreamWriter.write(line + "\n");
                    outputStreamWriter.flush();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        process = null;
    }
}