package zombies.fileManagment;

import org.json.JSONArray;
import org.json.JSONObject;
import zombies.simulation.Agent;
import zombies.simulation.Vector2D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OutputManager {
    private final static String DIRECTORY = "./results";
    private static FileWriter SNAPSHOT_WRITER;
    JSONObject dynamic_data;
    JSONObject static_data;
    JSONArray t;
    JSONArray positionsX;
    JSONArray positionsY;
    JSONArray velocitiesX;
    JSONArray velocitiesY;
    JSONArray states;
    String name;


    public OutputManager(String name) {
        if(name == null){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss");
            LocalDateTime time = LocalDateTime.now();
            this.name = dtf.format(time);
        }else {
            this.name = name;
        }
        dynamic_data = new JSONObject();
        t = new JSONArray();

        dynamic_data.put("t", t);
        static_data = new JSONObject();


        positionsX = new JSONArray();
        positionsY = new JSONArray();
        velocitiesX = new JSONArray();
        velocitiesY = new JSONArray();
        states = new JSONArray();

        new File(DIRECTORY).mkdir();
    }


    public void saveSnapshotAgent(Agent a, Vector2D vel ) {
        String format = "%.4f";
        JSONArray agent_info = new JSONArray();
        agent_info.put(Double.valueOf(String.format(format, a.pos.x)));
        agent_info.put(Double.valueOf(String.format(format, a.pos.y)));
//        agent_info.put(Double.valueOf(String.format(format, vel.x)));
//        agent_info.put(Double.valueOf(String.format(format, vel.y)));
        agent_info.put(Double.valueOf(String.format(format, a.radius)));
        agent_info.put(a.agentType.ordinal());
        positionsX.put(agent_info);
    }


    public void saveT(){
        t.put(positionsX);
//        snapshot.put("py" ,positionsY);
//        snapshot.put("vx" , velocitiesX);
//        snapshot.put("vy" ,velocitiesY);
//        snapshot.put("s" ,states);
        positionsX = new JSONArray();
//        positionsY = new JSONArray();
//        velocitiesX = new JSONArray();
//        velocitiesY = new JSONArray();
//        states = new JSONArray();
    }

    public void saveDynamic() {
        File dir = new File(DIRECTORY + "/" + name + "/");
        dir.mkdir();
        String filePath = DIRECTORY + "/" + name + "/" + "dynamic.json";
        save_json(filePath, dynamic_data);
    }

    public void saveStatic(double delta_t, double k, double Q, double M, double D) {
        static_data.put("delta_t", delta_t);
        static_data.put("k", k);
        static_data.put("Q", Q);
        static_data.put("M", M);
        static_data.put("D", D);
        File dir = new File(DIRECTORY + "/" + name + "/");
        dir.mkdir();
        String filePath = DIRECTORY + "/" + name + "/" + "static.json";
        save_json(filePath, static_data);
    }

    private void save_json(String filePath, JSONObject static_data) {
        File dir2 = new File(filePath);
        try {
            if (dir2.createNewFile()) {
                System.out.println("File created: " + dir2.getName());
                SNAPSHOT_WRITER = new FileWriter(filePath);
                SNAPSHOT_WRITER.write(static_data.toString());
                SNAPSHOT_WRITER.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
