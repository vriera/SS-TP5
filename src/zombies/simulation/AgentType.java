package zombies.simulation;

import zombies.fileManagment.Config;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public enum AgentType implements Behaviour{

    HUMAN{
       @Override
       public Vector2D behave(List<Agent> close, Agent self, double space_radius){



           if (self.pos.magnitude() >= (Config.SPACE_RADIUS - self.radius)) {
               self.reset_radius_in_next_update();
               self.direction = new Vector2D(1 , 0).rotate(Math.random() * 2 *Math.PI);
               return  self.pos.mul(-1.0).normalize().mul(self.desired_v);

           }
           ///Heuristic
           //Check wall
           Vector2D wallRepel = new Vector2D(0,0);
           Vector2D n = new Vector2D(0,0);
           if (self.pos.magnitude() >= (Config.SPACE_RADIUS - (2 *self.getMaxR()) ) ) {
               //multiplicar dependiendo de la distancia
               wallRepel = getNC(self.pos.mul(-1.0) , 2500 , 0.8);
           }

           int escape_count = 0;
           Vector2D escape_vels = new Vector2D(0, 0);
           Vector2D humanRepel = new Vector2D(0,0);
           boolean zombie_nearby = false;
           for (Agent agent : close) {
               double distance = self.pos.distance(agent.pos);
               if(agent == self || self.vision_r < distance){
                   continue;
               }

               if(agent.agentType == ZOMBIE || agent.agentType == TRANSFORMING){
                   if (distance < (self.radius + agent.radius)) {
                       Vector2D vel_dir = self.pos.sub(agent.pos).normalize();
                       System.out.println("desired v: " + self.desired_v);
                       escape_vels = escape_vels.add(vel_dir);
                       escape_count++;
                       zombie_nearby = true;
                   } else {
                       n = n.add(getNC(self.pos.sub(agent.pos), 2000, 0.08));
                   }
               } else {
                   if (distance < (self.radius + agent.radius)) {
                       Vector2D vel_dir = self.pos.sub(agent.pos).normalize();
                       System.out.println("desired v: " + self.desired_v);
                       escape_vels = escape_vels.add(vel_dir);
                       escape_count++;
                   }else{
                       if (agent.vel_mag >= self.desired_v/2) zombie_nearby = true;
                       humanRepel = humanRepel.add(getNC(self.pos.sub(agent.pos) , 500 , 0.02));
                   }
               }
           }

           if (escape_count > 0) {
               escape_vels = escape_vels.normalize();
               self.reset_radius_in_next_update();
               return escape_vels.mul(self.desired_v);
           }

           n = n.add(wallRepel); //--> n es mi direccion objetivo
           if (zombie_nearby) {
               n = n.add(humanRepel);
           }
           if(n.magnitude() == 0){
               self.reset_radius_in_next_update();
           }
           return n.normalize().mul(self.desired_v * self.vel_mag);
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

            //Check collision
            Vector2D escape_vels = new Vector2D(0, 0);
            Vector2D chase_vel = new Vector2D(0,0);
            double closest_human = Double.POSITIVE_INFINITY;
            Agent turneable_human = null;
            int escape_count = 0;

            boolean human_in_sight = false;

            for (Agent agent : close) {
                double distance = self.pos.distance(agent.pos);
                if(agent == self || self.vision_r < distance){
                    continue;
                }

                if (agent.agentType == AgentType.ZOMBIE || agent.agentType == AgentType.TRANSFORMING) {
                    if (distance < (self.radius + agent.radius)) { // Si colisionan
                        Vector2D vel_dir = self.pos.sub(agent.pos).normalize();
                        System.out.println("desired v: " + self.desired_v);
                        escape_vels = escape_vels.add(vel_dir);
                        escape_count++;
                    }
                }else{
                    //Human
                    if(distance < closest_human){
                        human_in_sight = true;
                        chase_vel = agent.pos.sub(self.pos);
                        closest_human = distance;
                        turneable_human = agent;
                    }
                }
            }

            if(turneable_human!= null && closest_human <= (self.radius + turneable_human.radius)){
                turneable_human.transform(self);
            }
            if(escape_count == 0 && !human_in_sight){
                self.setRfromV(self.inactive_v);
                return  self.direction.mul(self.radius * self.desired_v);
            }

            if (escape_count > 0) {
                escape_vels = escape_vels.normalize();
                self.reset_radius_in_next_update();
                self.direction = new Vector2D(1 , 0).rotate(Math.random() * 2 *Math.PI);
                return escape_vels.mul(self.desired_v);
            }

            return chase_vel.mul(self.vel_mag * self.desired_v);
        }
    },
    TRANSFORMING{
        @Override
        public Vector2D behave(List<Agent> close, Agent self, double space_radius){
            self.reset_radius_in_next_update();
            return new Vector2D(0,0);
        }
    };


    private static Vector2D getNC(Vector2D eij , double Ap ,double Bp){
        return eij.normalize().mul( Ap).mul(Math.exp( - eij.magnitude() / Bp));
    }

}
