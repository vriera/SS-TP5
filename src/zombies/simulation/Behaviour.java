package zombies.simulation;

import java.util.List;

@FunctionalInterface
public interface Behaviour {
   Vector2D behave(List<Agent> close, Agent self , double space_radius);
}
