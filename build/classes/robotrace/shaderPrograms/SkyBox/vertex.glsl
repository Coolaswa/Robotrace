// simple vertex shader
varying vec3 R;

void main()
{

	R = gl_Vertex.xyz/gl_Vertex.w;
        R = normalize(R);

	mat4 view = gl_ModelViewMatrix;
        view[3][0] = 0.0;
        view[3][1] = 0.0;
        view[3][2] = 0.0;
	gl_Position    = gl_ProjectionMatrix * view * gl_Vertex;
	gl_FrontColor  = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}
