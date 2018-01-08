
varying vec3 P;
varying vec3 N;

void main()
{
	N = normalize(gl_NormalMatrix * gl_Normal);
	P = (gl_ModelViewMatrix*gl_Vertex).xyz/(gl_ModelViewMatrix *gl_Vertex).w;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
 	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_FrontColor = gl_Color;
}