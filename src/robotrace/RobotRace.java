package robotrace;

import static com.jogamp.opengl.GL2.*;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import javax.swing.Timer;
import java.awt.event.*;
import static robotrace.ShaderPrograms.*;
import static robotrace.Textures.*;

/**
 * Handles all of the RobotRace graphics functionality,
 * which should be extended per the assignment.
 * 
 * OpenGL functionality:
 * - Basic commands are called via the gl object;
 * - Utility commands are called via the glu and
 *   glut objects;
 * 
 * GlobalState:
 * The gs object contains the GlobalState as described
 * in the assignment:
 * - The camera viewpoint angles, phi and theta, are
 *   changed interactively by holding the left mouse
 *   button and dragging;
 * - The camera view width, vWidth, is changed
 *   interactively by holding the right mouse button
 *   and dragging upwards or downwards; (Not required in this assignment)
 * - The center point can be moved up and down by
 *   pressing the 'q' and 'z' keys, forwards and
 *   backwards with the 'w' and 's' keys, and
 *   left and right with the 'a' and 'd' keys;
 * - Other settings are changed via the menus
 *   at the top of the screen.
 * 
 * Textures:
 * Place your "track.jpg", "brick.jpg", "head.jpg",
 * and "torso.jpg" files in the folder textures. 
 * These will then be loaded as the texture
 * objects track, bricks, head, and torso respectively.
 * Be aware, these objects are already defined and
 * cannot be used for other purposes. The texture
 * objects can be used as follows:
 * 
 * gl.glColor3f(1f, 1f, 1f);
 * Textures.track.bind(gl);
 * gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0);
 * gl.glVertex3d(0, 0, 0);
 * gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0);
 * gl.glTexCoord2d(1, 1);
 * gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1);
 * gl.glVertex3d(0, 1, 0);
 * gl.glEnd(); 
 * 
 * Note that it is hard or impossible to texture
 * objects drawn with GLUT. Either define the
 * primitives of the object yourself (as seen
 * above) or add additional textured primitives
 * to the GLUT object.
 */
public class RobotRace extends Base {
    
    /** Array of the four robots. */
    private final Robot[] robots;
    
    /** Instance of the camera. */
    private final Camera camera;
    
    /** Instance of the race track. */
    private final RaceTrack[] raceTracks;
    
    /** Instance of the terrain. */
    private final Terrain terrain;
    
    private final Water water;
   
        
    /**
     * Constructs this robot race by initializing robots,
     * camera, track, and terrain.
     */
    public RobotRace() {
        
        // Create a new array of four robots
        robots = new Robot[4];

        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD, new double[] {0,0,0}
                
        );
        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new double[] {1,0,0}
    
        );
        
        robots[1].setSpeed(3);  
        
        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, new double[] {2,0,0}
              
        );
        
        robots[2].setSpeed(0.5);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new double[] {3,0,0}
                
        );
        
        robots[3].setSpeed(4);
        
        // Initialize the camera
        camera = new Camera();
        
        // Initialize the race tracks
        raceTracks = new RaceTrack[2];
        
        // Track 1
        raceTracks[0] = new ParametricTrack();
        
        
        // Track 2
        float g = 3.5f;
        float elevation = 1;
        raceTracks[1] = new BezierTrack(
            new Vector[] {
                new Vector(0,0,0+elevation),new Vector(10,0,0+elevation),new Vector(0,10,0+elevation),new Vector(10,10,0+elevation),
                new Vector(10,10,0+elevation),new Vector(20,10,0+elevation),new Vector(20,-10,0+elevation),new Vector(0,-10,0+elevation),
                new Vector(0,-10,0+elevation),new Vector(-20,-10,0+elevation),new Vector(-20,10,0+elevation),new Vector(-10,10,0+elevation),
                new Vector(-10,10,0+elevation),new Vector(0,10,0+elevation),new Vector(-10,0,0+elevation),new Vector(0,0,0+elevation)
            }
        );
        
        // Initialize the terrain
        terrain = new Terrain(camera);
        water = new Water(gl, glu, glut);
       
    }
    /**
     * Called upon the start of the application.
     * Primarily used to configure OpenGL.
     */
    @Override
    public void initialize() {
		
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                
        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
		
        // Enable face culling for improved performance
        // gl.glCullFace(GL_BACK);
        // gl.glEnable(GL_CULL_FACE);
        
	    // Normalize normals.
        gl.glEnable(GL_NORMALIZE);
        
	// Try to load four textures, add more if you like in the Textures class         
        Textures.loadTextures();
        reportError("reading textures");
        
        // Try to load and set up shader programs
        ShaderPrograms.setupShaders(gl, glu);
        reportError("shaderProgram");
        
    }
   
    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);
        
        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        glu.gluPerspective(45, (float)gs.w / (float)gs.h, 0.1*gs.vDist, 50*gs.vDist);

        
        gl.glLightfv(GL_LIGHT0, GL_POSITION, new float[]{40,40,30,0}, 0);
        //gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f, 1.0f}, 0);
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);

        gl.glLoadIdentity();

        // Add light source
        
        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.update(gs, robots[1]);
        glu.gluLookAt(camera.eye.x(),    camera.eye.y(),    camera.eye.z(),
                      camera.center.x(), camera.center.y(), camera.center.z(),
                      camera.up.x(),     camera.up.y(),     camera.up.z());
    }
    
    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        
        gl.glUseProgram(defaultShader.getProgramID());
        reportError("program");
        
        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0.5f);
        
        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);
        
        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        
        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);
        
        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        
        draw_scene();
        
        gl.glDisable(GL_LIGHTING);
        
    }
    
    private void draw_scene(){
    // Draw hierarchy example.
        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }
        
        // Draw the (first) robot.
         
        gl.glEnable(GL_LIGHTING);
        
        gl.glUseProgram(robotShader.getProgramID());
        updateRobots();
        
        robots[0].draw(gl, glu, glut, 0);
        robots[1].draw(gl, glu, glut, 0);
        robots[2].draw(gl, glu, glut, 0);
        robots[3].draw(gl, glu, glut, 0);

        // Draw the race track.
        gl.glUseProgram(trackShader.getProgramID());
        raceTracks[gs.trackNr].draw(gl, glu, glut);
       
        draw_stilts();
   
        // Draw the terrain.
        terrain.draw(gl, glu, glut);
        water.draw_water(gl, glu, glut);
        reportError("terrain:");  
    }
    
    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue),
     * and origin (yellow).
     */
    public void drawAxisFrame() {
        gl.glPushMatrix();
            gl.glColor3d(255,255,0);
            glut.glutSolidSphere(0.05,100,100);
            gl.glColor3d(255, 0, 0);
            drawArrow();
            gl.glRotated(-90, 0, 1, 0);
            gl.glColor3d(0, 0, 255);
            drawArrow();
            gl.glRotated(90, 0, 1, 0);
            gl.glRotated(90,0,0,1);
            gl.glColor3d(0, 255, 0);
            drawArrow();
            gl.glRotated(-90,0,0,1);
        gl.glPopMatrix();
    }
    
    /**
     * Draws a single arrow
     */
    public void drawArrow() {  
       gl.glTranslated(0.5,0,0);
       gl.glScaled(1,0.01,0.01);
       glut.glutSolidCube(1);
       gl.glScaled(1,100,100);
       gl.glTranslated(-0.5,0,0);
       
       gl.glTranslated(1,0,0);
       gl.glRotated(90, 0, 1, 0);
       glut.glutSolidCone(0.05, 0.1, 100, 100);
       gl.glRotated(-90, 0, 1, 0);
       gl.glTranslated(-1,0,0);
    }
    
    private void updateRobots(){
        for(int i = 0; i < 4; i++){
            double speed = robots[i].getSpeed();
            Vector pos = raceTracks[gs.trackNr].getLanePoint(i+1,gs.tAnim/50 * speed);
            double[] coord = new double[] {pos.x,pos.y,pos.z};
            robots[i].setAngleArmLegs((Math.sin(2*Math.PI/(2/speed)*gs.tAnim)) * (180/Math.PI));
            robots[i].setCoords(coord); 
            
            Vector tangent = raceTracks[gs.trackNr].getLaneTangent(i+1, gs.tAnim/50 * speed + 0.02).normalized();
            robots[i].setView(tangent);   
        }
    };
    
    private void draw_stilts(){
        gl.glPushMatrix();
        switch(gs.trackNr){
            case 0:
                gl.glTranslated(10,3.5,0);
                gl.glRotated(180,0,1,0);

                gl.glScaled(0.5,0.5,10);
                glut.glutSolidCylinder(1, 1, 50,50);
                gl.glScaled(2,2,0.1);

                break;
            case 1:
                gl.glTranslated(15,9,0);
                gl.glRotated(180,0,1,0);

                gl.glScaled(0.5,0.5,10);
                glut.glutSolidCylinder(1, 1, 50,50);
                gl.glScaled(2,2,0.1);

                gl.glTranslated(-3,-6,0);

                gl.glScaled(0.5,0.5,10);
                glut.glutSolidCylinder(1, 1, 50,50);
                break;
            default:
                break;
        }
        gl.glPopMatrix();
    }

    /**
     * Drawing hierarchy example.
     * 
     * This method draws an "arm" which can be animated using the sliders in the
     * RobotRace interface. The A and B sliders rotate the different joints of
     * the arm, while the C, D and E sliders set the R, G and B components of
     * the color of the arm respectively. 
     * 
     * The way that the "arm" is drawn (by calling {@link #drawSecond()}, which 
     * in turn calls {@link #drawThird()} imposes the following hierarchy:
     * 
     * {@link #drawHierarchy()} -> {@link #drawSecond()} -> {@link #drawThird()}
     */

    /**
     * Main program execution body, delegates to an instance of
     * the RobotRace implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }
}
