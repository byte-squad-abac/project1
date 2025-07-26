package gdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVLoader {
    
    public static HashMap<Integer, SpawnDetails> loadSpawnMap(String filename) {
        HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Parse CSV line: frame,type,x,y
                String[] values = line.split(",");
                if (values.length >= 4) {
                    try {
                        int frame = Integer.parseInt(values[0].trim());
                        String type = values[1].trim();
                        int x = Integer.parseInt(values[2].trim());
                        int y = Integer.parseInt(values[3].trim());
                        
                        spawnMap.put(frame, new SpawnDetails(type, x, y));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line);
                    }
                }
            }
            
            System.out.println("Loaded " + spawnMap.size() + " spawn entries from " + filename);
            
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + filename);
            System.err.println("Falling back to default spawn map.");
            return getDefaultSpawnMap();
        }
        
        return spawnMap;
    }
    
    public static int[][] loadStageMap(String filename) {
        List<int[]> rows = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header line if present
                if (isFirstLine && line.contains("col")) {
                    isFirstLine = false;
                    continue;
                }
                
                // Parse CSV line of integers
                String[] values = line.split(",");
                int[] row = new int[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    try {
                        row[i] = Integer.parseInt(values[i].trim());
                    } catch (NumberFormatException e) {
                        row[i] = 0; // Default to 0 if parsing fails
                    }
                }
                rows.add(row);
            }
            
            System.out.println("Loaded " + rows.size() + " map rows from " + filename);
            
        } catch (IOException e) {
            System.err.println("Error reading stage map CSV file: " + filename);
            System.err.println("Falling back to default stage map.");
            return getDefaultStageMap();
        }
        
        // Convert List to array
        return rows.toArray(new int[rows.size()][]);
    }
    
    private static HashMap<Integer, SpawnDetails> getDefaultSpawnMap() {
        HashMap<Integer, SpawnDetails> defaultMap = new HashMap<>();
        // Basic default spawn pattern
        defaultMap.put(50, new SpawnDetails("PowerUp-SpeedUp", 200, 0));
        defaultMap.put(150, new SpawnDetails("Alien1", 300, 0));
        defaultMap.put(250, new SpawnDetails("Alien2", 400, 0));
        return defaultMap;
    }
    
    private static int[][] getDefaultStageMap() {
        return new int[][] {
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}
        };
    }
}