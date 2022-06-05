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
        out = new OutputManager(Config.NAME);

        agents = new ArrayList<>(1); //TODO: Agregar todos los agents

        delta_t = Config.MIN_R / (2 * Config.H_V_DESIRED);


        Agent.setDelta_r((Config.MAX_R)/(Config.TIME_TO_MAX_R/delta_t));

        System.out.println(Agent.delta_r);

        for (int i = 0; i < 10; i++) {
            double posX, posY;
            do {
                posX = (Math.random() - 0.5) * 1;
                posY = (Math.random() - 0.5) * 1;
            } while (Math.sqrt(posX*posX + posY*posY) >= 0.9);

            //Agent zombie = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
            Agent human = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.HUMAN , Config.H_V_DESIRED, 0 , Config.VISION_R);

            agents.add(human);
        }


        for (int i = 0; i < 3; i++) {
            double posX, posY;
            do {
                posX = (Math.random() - 0.5) * 1;
                posY = (Math.random() - 0.5) * 1;
            } while (Math.sqrt(posX*posX + posY*posY) >= 0.9);

            Agent zombie = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.ZOMBIE , Config.Z_V_DESIRED, Config.Z_V_INACTIVE , Config.VISION_R);
          //  Agent human = new Agent(new Vector2D(0 + posX*Config.SPACE_RADIUS, 0 + posY*Config.SPACE_RADIUS), Config.MIN_R, Config.MIN_R, Config.MAX_R, AgentType.HUMAN , Config.H_V_DESIRED, 0 , Config.VISION_R);

            Vector2D initial_vel = new Vector2D(1, 0).rotate(Math.random() * 2 * Math.PI);
            zombie.setDirection(initial_vel);
            agents.add(zombie);
        }

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

        while(step < totalSteps) {
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
            }
            out.saveT();
            velocities.clear(); //Es mejor porque tiene el mismo tamaño
            step++;
        }

        out.saveDynamic();
    }

}
