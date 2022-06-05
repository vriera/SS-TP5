package zombies.simulation;

import zombies.fileManagment.Config;

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
                return self.direction.mul(self.vel_mag * self.inactive_v);

            List<Vector2D> vels = new ArrayList<>();

            Vector2D escape_vels = new Vector2D(0, 0);
            int escape_count = 0;
            for (Agent agent : close) {
                if (agent.agentType == AgentType.ZOMBIE) {
                    if (self.pos.distance(agent.pos) < (self.radius + agent.radius)) { // Si colisionan
                        Vector2D vel_dir = self.pos.sub(agent.pos);
                        escape_vels.add(vel_dir);
                        escape_count++;
                    }
                }
            }

            if (escape_count > 0) {
                escape_vels = escape_vels.normalize();
                self.reset_radius_in_next_update();
                return escape_vels.mul(self.vel_mag * self.desired_v);
            }

            Vector2D sum = new Vector2D(0,0);
            for (Vector2D v: vels) {
                sum = sum.add(v);
            }
            return sum.normalize();
        }
    },
    TRANSFORMING{
        @Override
        public Vector2D behave(List<Agent> close, Agent self, double space_radius){
            return new Vector2D(0,0);
        }
    }
}
