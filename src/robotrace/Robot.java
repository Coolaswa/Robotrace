package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;
import static com.jogamp.opengl.fixedfunc.GLLightingFunc.*;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
    private final Material material;
    
    private double hTorso;
    private double wTorso;
    private double dTorso;
    private double hLegs;
    private double wLegs;
    private double dLegs;
    private double lArms;
    private double rFace;
    private double angleArms;
    private double angleLegs;
    private double[] pos;
    private Vector view;
    private double angleView;
    private double speed;

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material, double[] pos
            
    ) {
        this.material = material;
        this.hTorso = 0.6;
        this.wTorso = 0.5;
        this.dTorso = 0.25;
        this.hLegs = 0.5;
        this.wLegs = 0.125;
        this.dLegs = 0.125;
        this.lArms = 0.7;
        this.rFace = 0.2;
        this.angleArms = 0;
        this.angleLegs = 10;
        this.pos = pos;
        this.angleView = 0;
        this.speed = 2;
        this.view = new Vector(0,1,0);
    }

    /**
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, float tAnim) {
        gl.glMaterialfv(GL_FRONT,GL_DIFFUSE,material.diffuse, 0);
        gl.glMaterialfv(GL_FRONT,GL_SPECULAR,material.specular, 0);
        gl.glMaterialf(GL_FRONT,GL_SHININESS,material.shininess);
        
        calculateAngle();
        
        gl.glPushMatrix(); 
        
            gl.glTranslated(pos[0],pos[1],pos[2]);
            gl.glRotated(angleView,0,0,1);
            
            //checkDirection(gl, glu, glut);
            drawBottom(gl,glu,glut,tAnim);
            gl.glTranslated(0,0,0.1);
            drawTorso(gl,glu,glut,tAnim);  

            gl.glTranslated(0,0,0.15);
            gl.glTranslated(0,0,0.1);
            drawHead(gl,glu,glut,tAnim);
            gl.glTranslated(0,0,-0.15);
            
            gl.glRotated(-angleView,0,0,1);
            gl.glTranslated(-pos[0],-pos[1],-pos[2]);
            
        gl.glPopMatrix();
    }
    
    private void drawBottom(GL2 gl, GLU glu, GLUT glut, float tAnim){
            gl.glTranslated(0,0,hLegs);
            gl.glRotated(angleLegs,1,0,0);
            gl.glTranslated(0,0,-hLegs);
            gl.glTranslated(wLegs,0,0);
            drawLeg(gl,glu,glut,tAnim);
            gl.glTranslated(-2*wLegs,0,0);
            
            gl.glTranslated(0,0,hLegs);
            gl.glRotated(-2*angleLegs,1,0,0);
            gl.glTranslated(0,0,-hLegs);
            drawLeg(gl,glu,glut,tAnim);
            gl.glTranslated(wLegs,0,0);
            gl.glTranslated(0,0,hLegs);
            gl.glRotated(angleLegs,1,0,0);
    }
    
    private void drawLeg(GL2 gl, GLU glu, GLUT glut, float tAnim){ 
        gl.glTranslated(0,0,0.5*hLegs);
        gl.glScaled(wLegs,dLegs,hLegs);
        glut.glutSolidCube(1);
        gl.glScaled(1/wLegs,1/dLegs,1/hLegs);
        gl.glTranslated(0,0,-0.5*hLegs);
    }
    
    private void drawTorso(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glTranslated(0,0,0.5*hTorso);
        gl.glScaled(wTorso,dTorso,hTorso);
        glut.glutSolidCube(1);
        gl.glScaled(1/wTorso,1/dTorso,1/hTorso);
        gl.glTranslated(0,0,-0.5*hTorso);
        
        gl.glTranslated(0,0,hTorso);
        gl.glTranslated(0,0,-0.1);
        gl.glTranslated(0.5*wTorso + 0.05,0,0);
        gl.glRotated(angleArms - 90,1,0,0);
        drawArm(gl,glu,glut,tAnim);
        gl.glRotated(-2*angleArms,1,0,0);
        gl.glTranslated(-2*(0.5*wTorso + 0.05),0,0);
        drawArm(gl,glu,glut,tAnim);
        gl.glTranslated(0.5*wTorso + 0.05,0,0);
        gl.glRotated(angleArms + 90,1,0,0);
        gl.glTranslated(0,0,0.1);
    }
    
    private void drawArm(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glTranslated(0,0.5*lArms*0.5,0);
        gl.glScaled(0.125,0.5*lArms,0.125);
        glut.glutSolidCube(1);
        gl.glScaled(8,1/(0.5*lArms),8);
        gl.glTranslated(0,-0.5*lArms*0.5,0);
        
        gl.glTranslated(0,0.5*lArms,0);
        
        gl.glRotated(30,1,0,0);
        gl.glTranslated(0,0.5*lArms*0.4,0);
        gl.glScaled(0.125,0.5*lArms*0.5,0.125);
        glut.glutSolidCube(1);
        gl.glScaled(8,1/(0.5*lArms*0.5),8);
        gl.glTranslated(0,-0.5*lArms*0.4,0);
        gl.glRotated(-30,1,0,0);
        
        gl.glTranslated(0,-0.5*lArms,0);
        
        
        
    }
    
    private void drawHead(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glScaled(rFace,rFace,rFace);
        glut.glutSolidSphere(1,100,100);
        gl.glScaled(1/rFace,1/rFace,1/rFace);
        
        gl.glTranslated(0,-0.01,-0.01);
        gl.glTranslated(0.1,-Math.cos(40)*rFace,Math.sin(40)*rFace);
        drawEye(gl,glu,glut,tAnim);
        gl.glTranslated(-0.2,0,0);
        drawEye(gl,glu,glut,tAnim);
        gl.glTranslated(0.1,Math.cos(40)*rFace,-Math.sin(40)*rFace);
        gl.glTranslated(0,0.01,0.01);
    }
    
    private void drawEye(GL2 gl, GLU glu, GLUT glut, float tAnim){
        gl.glScaled(0.02,0.02,0.02);
        glut.glutSolidSphere(1,100,100);
        gl.glScaled(50,50,50);
    }
    
    private void calculateAngle(){
        angleView = Math.acos(view.dot(Vector.Y)) * 180/Math.PI;

        if(view.x > 0){
            angleView *= -1;
        }
    }
    
    private void checkDirection(GL2 gl, GLU glu, GLUT glut){
        gl.glTranslated(0,0.5,0);
        gl.glScaled(0.1,1,0.1);
        glut.glutSolidCube(1);
        gl.glScaled(10,1,10);
        gl.glTranslated(0,-0.5,0);
    }
    
    public void setCoords(double[] pos){
        this.pos = pos;
    }
    
    public void setAngleArmLegs(double angle){
        this.angleArms = angle;
        this.angleLegs = -angle;
    }
    
    public void setView(Vector view){
        this.view = view;
    }
    
    public void setSpeed(double speed){
        this.speed = speed;
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public double[] getPos(){
        return pos;
    }

    public Vector getView() {
        return view;
    }

    public double getAngleView() {
        return angleView;
    }
    
    
}
