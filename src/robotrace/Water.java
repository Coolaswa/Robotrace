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

    private void initialiseReflectionFrameBuffer(GL2 gl, GLU glu, GLUT glut) {
        reflectionFrameBuffer = createFrameBuffer(gl, glu, glut);
        reflectionTexture = createTextureAttachment(gl, glu, glut, ref_width, ref_height);
        reflectionDepthBuffer = createDepthBufferAttachment(gl, glu, glut, ref_width, ref_height);
        unbindCurrentFrameBuffer(gl, glu, glut);
    }

    private void initialiseRefractionFrameBuffer(GL2 gl, GLU glu, GLUT glut) {
        refractionFrameBuffer = createFrameBuffer(gl, glu, glut);
        refractionTexture = createTextureAttachment(gl, glu, glut, refr_width, refr_height);
        refractionDepthTexture = createDepthBufferAttachment(gl, glu, glut, refr_width, refr_height);
        unbindCurrentFrameBuffer(gl, glu, glut);
    }

    public void bindReflectionFrameBuffer(GL2 gl, GLU glu, GLUT glut){
        bindFrameBuffer(gl, glu, glut, reflectionFrameBuffer, ref_width, ref_height);
    }

    public void bindRefractionFrameBuffer(GL2 gl, GLU glu, GLUT glut){
        bindFrameBuffer(gl, glu, glut, refractionFrameBuffer, refr_width, refr_height);
    }

    public void cleanUp(GL2 gl, GLU glu, GLUT glut){
        gl.glDeleteFramebuffers(1, Buffers.newDirectIntBuffer(reflectionFrameBuffer));
        gl.glDeleteTextures(1, Buffers.newDirectIntBuffer(reflectionTexture));
        gl.glDeleteRenderbuffers(1, Buffers.newDirectIntBuffer(reflectionDepthBuffer));
        gl.glDeleteFramebuffers(1, Buffers.newDirectIntBuffer(refractionFrameBuffer));
        gl.glDeleteTextures(1, Buffers.newDirectIntBuffer(refractionTexture));
        gl.glDeleteTextures(1, Buffers.newDirectIntBuffer(refractionDepthTexture));
    }

    public void unbindCurrentFrameBuffer(GL2 gl, GLU glu, GLUT glut){
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER,0);
    }

    private void bindFrameBuffer(GL2 gl, GLU glu, GLUT glut, int frameBuffer, int width, int height){
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffer);
        gl.glViewport(0, 0, width, height);
    }

    private int createFrameBuffer(GL2 gl, GLU glu, GLUT glut){
        int[] id = new int[1];
        gl.glGenFramebuffers(1, id, 0);
        int frameBuffer = id[0];
        gl.glBindFramebuffer(gl.GL_FRAMEBUFFER, frameBuffer);
        gl.glDrawBuffer(gl.GL_COLOR_ATTACHMENT0);

        return frameBuffer;
    }

    private int createTextureAttachment(GL2 gl, GLU glu, GLUT glut,int width, int height){
        int[] id_t = new int[1];
        gl.glGenTextures(1, id_t, 0);
        int texBuffer = id_t[0];
        gl.glBindTexture(gl.GL_TEXTURE_2D, texBuffer);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_RGB, width, height, 0, gl.GL_RGB, gl.GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
        gl.glFramebufferTexture2D(gl.GL_FRAMEBUFFER, gl.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texBuffer, 0);

        return texBuffer;
    }

    private int createDepthBufferAttachment(GL2 gl, GLU glu, GLUT glut, int width, int height){
        int[] id = new int[1];
        gl.glGenRenderbuffers(1, id, 0);
        int depBuffer = id[0];
        gl.glBindFramebuffer(gl.GL_RENDERBUFFER, depBuffer);
        gl.glRenderbufferStorage(gl.GL_RENDERBUFFER, gl.GL_DEPTH_COMPONENT, width, height);
        gl.glFramebufferRenderbuffer(gl.GL_FRAMEBUFFER, gl.GL_DEPTH_ATTACHMENT, gl.GL_RENDERBUFFER, depBuffer);

        return depBuffer;
    }

    private int createDepthTextureAttachment(GL2 gl, GLU glu, GLUT glut,int width, int height){
        int[] id_d = new int[1];
        gl.glGenTextures(1, id_d, 0);
        int depBuffer = id_d[0];
        gl.glBindTexture(gl.GL_TEXTURE_2D, depBuffer);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_DEPTH_COMPONENT32, width, height, 0, gl.GL_DEPTH_COMPONENT, gl.GL_FLOAT, null);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
        gl.glFramebufferTexture2D(gl.GL_FRAMEBUFFER, gl.GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depBuffer, 0);

        return depBuffer;
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



