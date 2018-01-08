/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotrace;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import static com.jogamp.opengl.GL2.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import static robotrace.ShaderPrograms.*;
/**
 *
 * @author Marsbewoner
 */
public class Water {
    
    int ref_width = 320;
    int ref_height = 180;
    int refr_width = 1280;
    int refr_height = 720;
    int reflectionFrameBuffer;
    int reflectionTexture;
    int reflectionDepthBuffer;
    int refractionFrameBuffer;
    int refractionTexture;
    int refractionDepthTexture;
    
    
    public Water(GL2 gl, GLU glu, GLUT glut){
        //initialiseReflectionFrameBuffer(gl, glu, glut);
        //initialiseRefractionFrameBuffer(gl, glu, glut);
    }

    public void draw_water(GL2 gl, GLU glu, GLUT glut){
        int mapSize = 100;
        gl.glColor4f(0f,0.5f,0.5f,0.2f);
        
        gl.glUseProgram(waterShader.getProgramID());
        gl.glPushMatrix();
        gl.glTranslated(-mapSize/2,-mapSize/2,-6);
        
        for(int i = 0; i < mapSize-1; i++){
            gl.glBegin(GL_QUAD_STRIP);
            for(int j = 0; j < mapSize-1; j++){
                gl.glNormal3f(0,0,1);
                gl.glVertex3f(i,j,0);
                
                gl.glNormal3f(0,0,1);
                gl.glVertex3f(i+1,j,0);
            }
            gl.glEnd();
        }
        
        gl.glTranslated(mapSize/2,mapSize/2,6);
        gl.glPopMatrix();
    }
}



