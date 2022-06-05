package zombies.simulation;

import zombies.fileManagment.Config;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public enum AgentType implements Behaviour{
    HUMAN{
       @Override
       public Vector2D behave(List<Agent> close, Agent self, double space_radius){
           return new Vector2D(0,0);
       }
    },


    ZOMBIE{
        @Override
        public Vector2D behave(List<Agent> close, Agent self, double space_radius){

            //Check wall
            if (self.pos.magnitude() >= (Config.SPACE_RADIUS - self.radius)) {
                self.reset_radius_in_next_update();
                self.direction = new Vector2D(1 , 0).rotate(Math.random() * 2 *Math.PI);
                return  self.pos.mul(-1.0).normalize().mul(self.desired_v);

            }

            if(close.isEmpty())
                return self.direction.mul(self.inactive_v);




            List<Vector2D> vels = new ArrayList<>();
            //Check collision
            Vector2D escape_vels = new Vector2D(0, 0);
            Vector2D chase_vel = new Vector2D(0,0);
            double closest_human = Double.POSITIVE_INFINITY;
            int escape_count = 0;

            boolean human_in_sight = false;

            for (Agent agent : close) {
                double distance = self.pos.distance(agent.pos);
                if(agent == self || agent.vision_r < distance){
                    continue;
                }

                if (agent.agentType == AgentType.ZOMBIE || agent.agentType == AgentType.TRANSFORMING) {
                    if (distance < (self.radius + agent.radius)) { // Si colisionan
                        Vector2D vel_dir = self.pos.sub(agent.pos);
                        escape_vels.add(vel_dir);
                        escape_count++;
                        self.reset_radius_in_next_update();
                        self.direction = new Vector2D(1 , 0).rotate(Math.random() * 2 *Math.PI);

                    }
                }else{
                    //Human
                    if(distance < closest_human){
                        human_in_sight = true;
                        chase_vel = agent.pos.sub(self.pos);
                        closest_human = distance;
                    }

                }
            }

            if (escape_count > 0) {
                escape_vels = escape_vels.normalize();
                self.reset_radius_in_next_update();
                return escape_vels.mul(self.desired_v);
            } else if (!human_in_sight) {
                return self.direction.mul(self.vel_mag * self.inactive_v);
            }

            return chase_vel.mul(self.vel_mag * self.desired_v);
        }
    },
    TRANSFORMING{
        @Override
        public Vector2D behave(List<Agent> close, Agent self, double space_radius){
            return new Vector2D(0,0);
        }
    }
}
