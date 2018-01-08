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
 * Represents the terrain, to be implemented according to the Assignments.
 */
class Terrain {

    float[][] heights;
    int num_vertex;
    float MAX_HEIGHT;
    float MAX_PIXEL_COLOUR;
    Camera camera;
    float[][] trees;
    float[][] skybox;
    float cube_size;
    Material terrain;
    int mapSize;
    
    BufferedImage[] height_maps;
    Vector[][][] normal_maps;
    
    public Terrain(Camera camera) {
        mapSize = 100;
        cube_size = 200;
        MAX_HEIGHT = 10;
        MAX_PIXEL_COLOUR = 256 * 256 * 256;
        
        heightMap();  
        init_trees();
        
        this.camera = camera;
        this.skybox = new float[][]{    
            {cube_size, cube_size,-cube_size}, 
            {-cube_size,cube_size,-cube_size},
            {-cube_size, cube_size, cube_size},    
            {cube_size, cube_size, cube_size},    

            {cube_size,-cube_size, cube_size},   
            {-cube_size,-cube_size, cube_size},    
            {-cube_size,-cube_size,-cube_size},    
            {cube_size,-cube_size,-cube_size},    

            {cube_size, cube_size, cube_size},    
            {-cube_size, cube_size, cube_size},    
            {-cube_size,-cube_size, cube_size},    
            { cube_size,-cube_size, cube_size},    

            { cube_size,-cube_size,-cube_size},   
            {-cube_size,-cube_size,-cube_size},    
            {-cube_size, cube_size,-cube_size},    
            { cube_size, cube_size,-cube_size},    

            {-cube_size, cube_size, cube_size},    
            {-cube_size, cube_size,-cube_size},    
            {-cube_size,-cube_size,-cube_size},    
            {-cube_size,-cube_size, cube_size},    

            { cube_size, cube_size,-cube_size},    
            { cube_size, cube_size, cube_size},    
            { cube_size,-cube_size, cube_size},    
            { cube_size,-cube_size,-cube_size}   
	};
        terrain = Material.TERRAIN;
        
    }
    
    private void init_trees(){
        float[][] coords = new float[][]{
            {1,20},{1,30},
            {-6,24},{-6,34},
            {-13,22},{-13,32},
            {-20,-15},{-20,-5},{-20,5},{-20,15},{-20,25},
            {-27,-18},{-27,-8},{-27,2},{-27,12},{-27,22},
            {-34,-12},{-34,-2},{-34,8},
            {-16,13},
            {-32,18},
            
            {40,-15},{30,-13},{20,-11},
            {35,-22},{25,-20},{15,-18},
            {34,-29},{24,-27},{14,-25},
        };
        
        trees = new float[coords.length][3];
        for(int i = 0; i < coords.length; i++){
            float x = coords[i][0];
            float y = coords[i][1];
            float z = getHeight((int)x+(mapSize/2),(int)y+(mapSize/2),height_maps[1]);
            trees[i] = new float[] {x,y,z};
        }
    }
    /**
     * Draws the terrain.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        gl.glMaterialfv(GL_FRONT,GL_DIFFUSE,terrain.diffuse, 0);
        gl.glMaterialfv(GL_FRONT,GL_SPECULAR,terrain.specular, 0);
        gl.glMaterialf(GL_FRONT,GL_SHININESS,terrain.shininess);
        
        
        draw_skybox(gl,glu,glut);
        gl.glColor3f(0f,0f,0f);
        draw_island(gl,glu,glut,1);
        gl.glPushMatrix();
        gl.glTranslated(0,0,1);

        gl.glUseProgram(waterShader.getProgramID());
        for(float[] tree : trees){
            draw_tree(gl,glu,glut,tree[0],tree[1],tree[2]);
        }
        gl.glTranslated(0,0,-1);
        gl.glPopMatrix();
        
    }
    
    private void draw_island(GL2 gl, GLU glu, GLUT glut,int texture){  
        BufferedImage island = height_maps[texture];
        Vector[][] normals = normal_maps[texture];
        
        gl.glUseProgram(terrainShader.getProgramID());
        gl.glPushMatrix();
        gl.glTranslated(-mapSize/2,-mapSize/2,1);
        gl.glEnable(GL_TEXTURE_2D);
        Textures.grass.bind(gl);
        gl.glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        gl.glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
        gl.glColor3f(0f, 0f, 0f);
        
        for(int i = 0; i < mapSize-1; i += 1){
            gl.glBegin(GL_QUAD_STRIP);
            for(int j = 0; j < mapSize-1; j += 1){
                gl.glTexCoord2d(((double)i)/mapSize,((double)j)/mapSize);
                gl.glNormal3f((float)normals[i][j].x,(float)normals[i][j].y,(float)normals[i][j].z); 
                gl.glVertex3f(i,j,getHeight(i,j,island));
                
                gl.glTexCoord2d((((double)i+1)/mapSize),((double)j/mapSize));
                gl.glNormal3f((float)normals[i+1][j].x,(float)normals[i+1][j].y,(float)normals[i+1][j].z); 
                gl.glVertex3f(i+1,j,getHeight(i+1,j,island));
            }
            gl.glEnd();
        }
        gl.glTranslated(mapSize/2,mapSize/2,-1);
        
        Textures.grass.disable(gl);
        gl.glDisable(GL_TEXTURE_2D);
        
        gl.glPopMatrix();        
    }
    
    private void draw_skybox(GL2 gl, GLU glu, GLUT glut){
        gl.glUseProgram(skyboxShader.getProgramID());
        gl.glEnable(GL_TEXTURE_2D);
        Textures.skybox.bind(gl);
        
        gl.glPushMatrix();
            gl.glRotated(-90,1,0,0);
            gl.glBegin(GL_QUADS);        
                for(float[] vertex : skybox){
                    gl.glVertex3f(vertex[0],vertex[1],vertex[2]);
                } 
            gl.glEnd(); 
            gl.glRotated(90,1,0,0);
        gl.glPopMatrix();
        
        Textures.skybox.disable(gl);
        gl.glDisable(GL_TEXTURE_2D);
    }
    
    private void draw_tree(GL2 gl, GLU glu, GLUT glut,float x, float y, float z){
        gl.glPushMatrix();
            gl.glTranslated(x,y,z);
        
            gl.glColor3f(0.55f,0.27f,0.07f);
            gl.glScaled(0.5,0.5,2);
            glut.glutSolidCylinder(0.5,1.7,10,10);
            
            gl.glColor3f(0f,0.5f,0f);
            gl.glScaled(2,2,0.5);
            gl.glTranslated(0,0,1.5);
            glut.glutSolidCone(1.5,2,10,10);
            gl.glTranslated(0,0,0.5);
            glut.glutSolidCone(1,2,10,10);
            gl.glTranslated(0,0,0.5);
            glut.glutSolidCone(0.5,2,10,10);
            gl.glTranslated(0,0,-2.5);
            
            gl.glTranslated(-x,-y,-z);
        gl.glPopMatrix();
    }
    
    private void heightMap(){
        String[] maps = new String[] {"src/robotrace/textures/uluru.png","src/robotrace/textures/island.png"};
        height_maps = new BufferedImage[maps.length];
        normal_maps = new Vector[maps.length][][];
        
        for(int f = 0; f < maps.length; f++){
            try{
                BufferedImage map = ImageIO.read(new File(maps[f]));
                Vector[][] normals_buf = new Vector[map.getWidth()][map.getHeight()];
                System.out.println(map.getWidth());
                for(int i = 0; i < map.getWidth(); i++){
                    for(int j = 0; j < map.getHeight(); j++){
                        normals_buf[i][j] = getNormal(i,j,map);
                    }
                }
                
                height_maps[f] = map;
                normal_maps[f] = normals_buf;
               
            }catch(IOException e){
                e.printStackTrace();
            }
            

            
        }
    }   
    
    private float getHeight(int x, int z, BufferedImage map){
        if(x < 0 || x >= map.getWidth() || z < 0 || z >= map.getHeight()){
            return 0;
        }
        
        float height = map.getRGB(x,z);
        height += MAX_PIXEL_COLOUR/2f;
        height /= MAX_PIXEL_COLOUR/2f;
        height *= MAX_HEIGHT;
        return height;
    }
    
    private Vector getNormal(int x, int z, BufferedImage map){
        float v_1 = getHeight(x-1, z, map);
        float v_2 = getHeight(x+1, z, map);
        float v_3 = getHeight(x, z-1, map);
        float v_4 = getHeight(x, z+1, map);
        
        return new Vector(v_1-v_2, 2f, v_3-v_4).normalized();
    }
}
