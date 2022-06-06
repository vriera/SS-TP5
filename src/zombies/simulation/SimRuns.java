package zombies.simulation;

import zombies.fileManagment.Config;
import zombies.fileManagment.OutputManager;

import java.util.ArrayList;
import java.util.List;

public class SimRuns {

    static ArrayList<Agent> agents;
    static double delta_t;
    static OutputManager out;
    static int REPETITIONS = 15;


    private static void generateAgents( int n){
        Agent zombie = new Agent(new Vector2D(0 , 0 ), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
        zombie.direction = new Vector2D(1, 0).rotate(Math.random() * 2 * Math.PI);
        agents.add(zombie);
        for (int i = 0; i < n; i++) {
            double posX, posY;
            Agent human;
            boolean repited = false;
            do {
                do {
                    posX = (Math.random() - 0.5) * 2;
                    posY = (Math.random() - 0.5) * 2;
                } while (new Vector2D(posX, posY).magnitude() >= 1);
                repited = false;
                //Agent zombie = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
                for (int j = 0; j < i && !repited; j++) {
                    Agent other = agents.get(j);
                    if (other.pos.distance(new Vector2D(posX * (Config.SPACE_RADIUS-1), posY * (Config.SPACE_RADIUS -1))) < (2*Config.MIN_R)) {
                        repited = true;
                    }
                }
                human = new Agent(new Vector2D(0 + posX * Config.SPACE_RADIUS, 0 + posY * Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.HUMAN, Config.H_V_DESIRED, 0, Config.HUMAN_VISION_R);
            } while (repited);
            agents.add(human);
        }
    }

    public static void main(String[] args) {

        Double[] vdzs = {1.0 , 1.5,  2.0, 2.5,  3.0, 3.5,  4.0 ,4.5 , 5.0};
        Config.readConfig();
        delta_t = Config.MIN_R / (2 * Config.H_V_DESIRED);
        Agent.setDelta_r((Config.MAX_R)/(Config.TIME_TO_MAX_R/delta_t));
        Agent.t_conversion = Config.T_CONVERSION;
        for(int k=0 ; k <2 ; k++){

            for( Double vzd : vdzs) {
                int name = (int) (vzd *10);
                System.out.println( "k=" + k +" n=" +  vzd );
                Config.Z_V_DESIRED = vzd;
                out = new OutputManager(Config.NAME +"_vz_" + name + "_" + k );
                agents = new ArrayList<>(); //TODO: Agregar todos los agents
                generateAgents(Config.TOTAL_H);
                run();
            }
        }
    }

    public static ArrayList<Agent> getSurroundings(Agent agent) {
        return agents;
    }

    public static void run() {

        List<Vector2D> velocities = new ArrayList<>();
        int step = 0;
        int totalSteps = (int)Math.floor(Config.MAX_T / delta_t);
        System.out.println(delta_t);
        int zombies = 1;
        while(step < totalSteps && zombies < (Config.TOTAL_H + 1)) {
            zombies= 0;

            for (Agent agent : agents) {

                ArrayList<Agent> close = getSurroundings(agent);
                velocities.add(agent.behave(close , Config.SPACE_RADIUS));

            }
            for(int i = 0 ; i < agents.size() ; i++){
                Agent a = agents.get(i);
                out.saveSnapshotAgent(a , a.direction.mul(a.vel_mag));
                agents.get(i).update(velocities.get(i) , delta_t);
                if (a.pos.distance(new Vector2D(0, 0)) > Config.SPACE_RADIUS) {
                    System.out.println(a.pos);
                    System.out.println(a.vel_mag);
                }
                if(a.agentType == AgentType.ZOMBIE){
                    zombies++;
                }
            }

            out.saveT();
            velocities.clear(); //Es mejor porque tiene el mismo tamaño
            step++;
        }

        out.saveDynamic();
    }

}
