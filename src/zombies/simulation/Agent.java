package zombies.simulation;

import java.util.List;

public class Agent {
    public static double delta_r = 0;
    public double desired_v = 0;
    public double inactive_v = 0;

    public Vector2D pos;
    public Vector2D direction;
    public double vel_mag;
    public double radius;
    public AgentType agentType;
    private final Double min_r;
    private final Double max_r;

    public double stunTime = 0;

    public double getMaxR() {
        return this.max_r;
    }
    private boolean shrink_in_next_update = false;
    private Double target_radius = null;
    public double vision_r;


    public Agent(Vector2D pos, double radius, double min_r, double max_r, AgentType agentType , double desired_v, double inactive_v , double vision_r) {
        this.desired_v = desired_v;
        this.pos = new Vector2D(pos.x, pos.y);
        this.radius = radius;
        this.min_r = min_r;
        this.max_r = max_r;
        this.agentType = agentType;
        this.direction = new Vector2D(0, 0);
        this.vel_mag = 0;
        this.inactive_v = inactive_v;
        this.vision_r = vision_r;
    }


    public Vector2D behave(List<Agent> near , double space_radius){
        return agentType.behave(near, this , space_radius);
    }



    public void update(Vector2D vel , double delta_t) {

        if( this.stunTime > 0 ) {
            this.reset_radius();
            this.stunTime -= delta_t;
            if(stunTime <= 0 ){
                this.agentType = AgentType.ZOMBIE;
            }
            return;
        }
        this.pos = this.pos.add(vel.mul(delta_t));
        this.updateRadius(shrink_in_next_update);
        this.updateMag();
        this.shrink_in_next_update = false;
    }


    public void setDirection(Vector2D dir) {
        this.direction = new Vector2D(dir.x, dir.y).normalize();
        this.updateMag();
    }

    private void updateMag() {
        this.vel_mag = (this.radius - this.min_r)/(this.max_r - this.min_r);
    }

    public void updateRadius(boolean reset) {
        if(target_radius != null){
            if(radius > target_radius) {
                this.radius = target_radius;
                this.target_radius = null;
                return;
            }
        }
        if (reset) {
            this.radius = min_r;
            return;
        }
        this.radius += Agent.delta_r;
        if (this.radius > this.max_r) {
            this.radius = max_r;
        }
    }


    public void transform( Agent zombie){
        this.agentType = AgentType.TRANSFORMING;
        this.vision_r = zombie.vision_r;
        this.desired_v = zombie.desired_v;
        this.inactive_v = zombie.inactive_v;
        this.stunTime = 7;
        zombie.stunTime = 7;
        this.reset_radius_in_next_update();
        zombie.reset_radius_in_next_update();
    }

    public void reset_radius() {
        this.radius = this.min_r;
    }

    public void reset_radius_in_next_update() {
        this.shrink_in_next_update = true;
    }

    public static void setDelta_r(double delta_r) {
        Agent.delta_r = delta_r;
    }

    public void setRfromV( double vel ){
        this.target_radius = (vel/desired_v) * (max_r - min_r) + min_r;
        System.out.println(String.format("target radious %f" , target_radius));
    }


}
