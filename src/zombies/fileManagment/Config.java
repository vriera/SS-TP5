package zombies.fileManagment;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Config {

    //Ambiente
    public static double SPACE_RADIUS , T_CONVERSION , MAX_T;
    public static String NAME;
    //Zombies
    public static double Z_V_DESIRED , Z_V_INACTIVE;
    //Humans
    public static int TOTAL_H;
    public static double H_V_DESIRED;
    //Agents
    public static double VISION_R , MIN_R , MAX_R, TIME_TO_MAX_R;

    private static final String config_path = "./config.json";

    /*
{
"space_radius": 11,
"humans": 2,
"human_vision": 5,
"zombie_vision": 4 ,
"conversion_time": 7,
"inactive_v": 0.3,
"desired_v_z": 6,
"desired_v_h": 4,
"min_h_size": 0.1,
"max_h_size": 0.1,
"max_time": 300
}
    */

    public static void readConfig(){
        StringBuilder str_builder = new StringBuilder();
        try {
            Stream<String> stream = Files.lines(Paths.get(Config.config_path));
            stream.forEach(s -> str_builder.append(s).append('\n'));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        String content = str_builder.toString();
        JSONObject root = new JSONObject(content);
        SPACE_RADIUS = root.getDouble("space_radius");
        T_CONVERSION = root.getDouble("t_conversion");
        MAX_T = root.getDouble("max_t");
        NAME = root.getString("name");
        Z_V_DESIRED = root.getDouble("z_v_desired");
        Z_V_INACTIVE = root.getDouble("z_v_inactive");
        TOTAL_H = root.getInt("total_h");
        H_V_DESIRED = root.getDouble("h_v_desired");
        VISION_R = root.getDouble("vision_r");
        MIN_R = root.getDouble("min_r");
        MAX_R = root.getDouble("max_r");
        TIME_TO_MAX_R = root.getDouble("time_to_max_r");
    }



}
