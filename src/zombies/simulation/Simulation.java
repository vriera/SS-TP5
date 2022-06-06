package zombies.simulation;

import zombies.fileManagment.Config;
import zombies.fileManagment.OutputManager;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    static ArrayList<Agent> agents;
    static double delta_t;
    static OutputManager out;
    public static void main(String[] args) {
        Config.readConfig();
        Agent.t_conversion = Config.T_CONVERSION;
        out = new OutputManager(Config.NAME);

        agents = new ArrayList<>(1); //TODO: Agregar todos los agents

        delta_t = Config.MIN_R / (2 * Config.H_V_DESIRED);


        Agent.setDelta_r((Config.MAX_R)/(Config.TIME_TO_MAX_R/delta_t));

        System.out.println(Agent.delta_r);

        for (int i = 0; i < Config.TOTAL_H; i++) {
            double posX, posY;
            Agent human;
            boolean repited = false;
            do {
                do {
                    posX = (Math.random() - 0.5) * 2;
                    posY = (Math.random() - 0.5) * 2;
                    System.out.println("X: " + posX + ", Y: " + posY);
                } while (new Vector2D(posX, posY).magnitude() >= 0.9);
                repited = false;
                //Agent zombie = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
                for (int j = 0; j < i && !repited; j++) {
                    Agent other = agents.get(j);
                    if (other.pos.distance(new Vector2D(posX * Config.SPACE_RADIUS, posY * Config.SPACE_RADIUS)) < (2*Config.MIN_R)) {
                        repited = true;
                    }
                }
                human = new Agent(new Vector2D(0 + posX * Config.SPACE_RADIUS, 0 + posY * Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.HUMAN, Config.H_V_DESIRED, 0, Config.HUMAN_VISION_R);
            } while (repited);
            agents.add(human);
        }
        System.out.println("Finished");

        Agent zombie = new Agent(new Vector2D(0 , 0 ), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
        zombie.direction = new Vector2D(1, 0).rotate(Math.random() * 2 * Math.PI);
        agents.add(zombie);
        // Agent zombie = new Agent(new Vector2D(0 , 0 ), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
        Simulation.run();
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
        while(step < totalSteps && zombies < Config.TOTAL_H + 1) {
            zombies= 0;

            for (Agent agent : agents) {

                ArrayList<Agent> close = Simulation.getSurroundings(agent);
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
            System.out.println(zombies);
            out.saveT();
            velocities.clear(); //Es mejor porque tiene el mismo tamaño
            step++;
        }

        out.saveDynamic();
    }

}
