package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import static com.jogamp.opengl.GL2.*;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
abstract class RaceTrack {
    
    /** The width of one lane. The total width of the track is 4 * laneWidth. */
    private final static float laneWidth = 1.22f;
    
    
    
    /**
     * Constructor for the default track.
     */
    public RaceTrack() {
    }


    
    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) { 
        gl.glMaterialfv(GL_FRONT,GL_DIFFUSE,new float[] {1f,1f,1f,1f}, 0);
        gl.glMaterialfv(GL_FRONT,GL_SPECULAR,new float[] {1f,1f,1f,1f}, 0);
        gl.glMaterialf(GL_FRONT,GL_SHININESS,20f);
        double delta = 0.01;
        drawLanes(gl, glu, glut, delta);   
        drawEdges(gl, glu, glut, delta);
    }
    private void drawEdges(GL2 gl, GLU glu, GLUT glut, double delta){

        gl.glEnable(GL_TEXTURE_2D);
        Textures.brick.bind(gl);
        gl.glColor3f(1f,1f,1f);
         for(double i = 0.0; i < 1 + delta; i+=delta){ 
            Vector tangent = getTangent(i);
            Vector pos = getPoint(i);
            Vector normal = tangent.cross(Vector.Z).normalized().scale(1.22f);
            Vector tangent2 = getTangent(i+delta);
            Vector pos2 = getPoint(i+delta);
            Vector normal2 = tangent2.cross(Vector.Z).normalized().scale(1.22f);
            
            gl.glBegin(GL_QUAD_STRIP);
            Vector shading_normal = normal.scale(-1).normalized();
            gl.glNormal3d(shading_normal.x,shading_normal.y,shading_normal.z);
            Vector temp = pos.add(normal.scale(-2));
            gl.glTexCoord2d(0.0,0.0); gl.glVertex3d(temp.x,temp.y,temp.z);
            gl.glTexCoord2d(0.0,1.0); gl.glVertex3d(temp.x,temp.y,temp.z-1);
            
            Vector temp2 = pos2.add(normal2.scale(-2));
            gl.glTexCoord2d(1.0,0.0); gl.glVertex3d(temp2.x,temp2.y,temp2.z);
            gl.glTexCoord2d(1.0,1.0); gl.glVertex3d(temp2.x,temp2.y,temp2.z-1);  
            gl.glEnd();
            
            gl.glBegin(GL_QUAD_STRIP);
            shading_normal = normal.scale(1).normalized();
            gl.glNormal3d(shading_normal.x,shading_normal.y,shading_normal.z);
            temp = pos.add(normal.scale(2));
            gl.glTexCoord2d(0.0,0.0); gl.glVertex3d(temp.x,temp.y,temp.z);
            gl.glTexCoord2d(0.0,1.0); gl.glVertex3d(temp.x,temp.y,temp.z-1);

            temp2 = pos2.add(normal2.scale(2));
            gl.glTexCoord2d(1.0,0.0); gl.glVertex3d(temp2.x,temp2.y,temp2.z);
            gl.glTexCoord2d(1.0,1.0); gl.glVertex3d(temp2.x,temp2.y,temp2.z-1); 
            gl.glEnd(); 
        }
        Textures.brick.disable(gl); 
        gl.glDisable(GL_TEXTURE_2D);
    }
    private void drawLanes(GL2 gl, GLU glu, GLUT glut, double delta){
        gl.glEnable(GL_TEXTURE_2D);
        Textures.track.bind(gl);   
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);       
        gl.glColor3f(1f,1f,1f);        
        for(double i = 0.0; i < 1 + delta; i+=delta){
            gl.glBegin(GL_QUAD_STRIP);
            gl.glNormal3d(0,0,1);
            for(double j = -2; j < 3; j++){        
                Vector tangent = getTangent(i);
                Vector pos = getPoint(i);
                Vector normal = tangent.cross(Vector.Z).normalized().scale(1.22f);

                Vector tangent2 = getTangent(i+delta);
                Vector pos2 = getPoint(i+delta);
                Vector normal2 = tangent2.cross(Vector.Z).normalized().scale(1.22f);

                double tex = (j + 2)*0.25;
                Vector temp = pos.add(normal.scale(j));
                gl.glTexCoord2d(tex,(i)*10); gl.glVertex3d(temp.x,temp.y,temp.z);

                Vector temp2 = pos2.add(normal2.scale(j));
                gl.glTexCoord2d(tex,(i+delta)*10); gl.glVertex3d(temp2.x,temp2.y,temp2.z);
            }
            gl.glEnd();
        }
        Textures.track.disable(gl);  
        gl.glDisable(GL_TEXTURE_2D);
    }
    
    /**
     * Returns the center of a lane at 0 <= t < 1.
     * Use this method to find the position of a robot on the track.
     */
    public Vector getLanePoint(int lane, double t){
        Vector tangent = getTangent(t);
        Vector pos = getPoint(t);
        Vector normal = tangent.cross(Vector.Z).normalized().scale(1.22f);
        
        return pos.add(normal.scale(lane-2.5f));
    }
    
    /**
     * Returns the tangent of a lane at 0 <= t < 1.
     * Use this method to find the orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t){
        
        return getTangent(t);

    }
    
    public Vector getCubicBezierPnt(double t, Vector P0, Vector P1, Vector P2, Vector P3){
        Vector pnt = new Vector(0,0,0);
        pnt = pnt.add(P0.scale(-Math.pow(t,3) + 3*Math.pow(t,2) - 3*t + 1));
        pnt = pnt.add(P1.scale(3*Math.pow(t,3) - 6*Math.pow(t,2) + 3*t));
        pnt = pnt.add(P2.scale(-3*Math.pow(t,3) + 3*Math.pow(t,2)));
        pnt = pnt.add(P3.scale(Math.pow(t,3)));
        return pnt;
    }
    

    public Vector getCubicBezierTng(double t, Vector P0, Vector P1, Vector P2, Vector P3){
        Vector pnt = new Vector(0,0,0);
        pnt = pnt.add(P0.scale(-3*Math.pow(t,2) + 6*t - 3));
        pnt = pnt.add(P1.scale(9*Math.pow(t,2) - 12*Math.pow(t,1) + 3));
        pnt = pnt.add(P2.scale(-9*Math.pow(t,2) + 6*t));
        pnt = pnt.add(P3.scale(3*Math.pow(t,2)));
        return pnt;
    }
    
    // Returns a point on the test track at 0 <= t < 1.
    protected abstract Vector getPoint(double t);

    // Returns a tangent on the test track at 0 <= t < 1.
    protected abstract Vector getTangent(double t);
}
