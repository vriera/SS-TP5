final String CONFIG_LOCATION = "../config_visualization.json";

String SIM_NAME;

float space_radius;
float scale_factor;
JSONObject static_data;
JSONObject dynamic_data;
JSONArray info;

int step = 0;


void setup() {
  size(600, 600);
  background(0);
  ellipseMode(CENTER);
  scale_factor = (width/(2.0 * 11));
  dynamic_data = loadJSONObject("../results/Test6/dynamic.json");
  info = dynamic_data.getJSONArray("t");
  
}

void draw() {
  background(0);
  noFill();
  stroke(255);
  strokeWeight(2);
  ellipse(width/2, height/2, width, height);
  JSONArray particles = info.getJSONArray(step);
  for (int i = 0; i < particles.size(); i++) {
    JSONArray p = particles.getJSONArray(i);
    float x = map(p.getFloat(0), -11, 11, 0, width);
    float y = map(p.getFloat(1), -11, 11, 0, height);
    float radius = map(p.getFloat(2), 0.1, 0.3, 0.1 * scale_factor, 0.3 * scale_factor);
    int state = p.getInt(3);
    if (state == 1) {
      fill(255, 0, 0);
    } else {
      fill(0, 0, 255); 
    }
    noStroke();
    ellipse(x, y, 20, 20);
  }
  step++;
}