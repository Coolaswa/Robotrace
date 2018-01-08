varying vec3 P;
varying vec3 N;

varying vec4 color;

void main()
{
	N = normalize(gl_NormalMatrix * gl_Normal);
	P = (gl_ModelViewMatrix*gl_Vertex).xyz/(gl_ModelViewMatrix *gl_Vertex).w;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
        vec3 vertex = gl_Vertex.xyz/gl_Vertex.w;
        if(vertex.z < -8){
            color = vec4(0,0,0.5,1);
        }else if(vertex.z < -3.5){
            color = vec4(0.79,0.73,0.56,1);
        }else{
            color = vec4(0.13,0.54,0.13,1);
        }

 	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_FrontColor = color;
}