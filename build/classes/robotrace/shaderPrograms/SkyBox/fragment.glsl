// simple fragment shader

// 'time' contains seconds since the program was linked.
uniform float time;
uniform sampler2D texture;

varying vec3 R;
float Q= 1.0/4.0;
float T= 1.0/3.0;

mat3 Translate(vec2 t) { return mat3(vec3(1,0,0), vec3(0,1,0), vec3(t.x,t.y,1));}
mat3 Scale(vec2 s) { return mat3(vec3(s.x,0,0),vec3(0,s.y,0),vec3(0,0,1)); }
mat3 Mat(vec2 v0, vec2 v1, vec2 t) { return mat3(vec3(v0,0),vec3(v1,0),vec3(t,1)); }

vec2 computeTextCoords(vec3 R){
	mat3 scalTrans = Mat(vec2(0.5*Q,0),vec2(0,0.5*T),vec2(0.5*Q,0.5*T)); //Create scaling and translation matrix
	float p = max(max(abs(R.x),abs(R.y)),abs(R.z)); //Calculate intersection point with unit cube
	vec3 point = R/p;
	float u,v; 
	vec2 offset = vec2(0,0);

	if(abs(R.x) == p){ // Check on which plane of the unit cube the intersection point lies.
		if(-R.x == p){
			offset = vec2(0,1.0*T); //Calculate corresponding offset towards top-left corner of corresponding cube map plane
			v = -point.y; // Due to the unfolding of the cube (cubemap), some coordinates are mirrored or rotated.
			u = -point.z;
		}else{
			offset = vec2(2.0*Q,1.0*T);
			v = -point.y;
			u = point.z;
		}
	}
	if(abs(R.y) == p){
		if(-R.y == p){
			offset = vec2(1.0*Q,2.0*T);
			u = point.x;
			v = point.z;
		}else{
			offset = vec2(1.0*Q,0.0);
			u = point.x;
			v = -point.z;
		}
	}
	if(abs(R.z) == p){
		if(-R.z == p){
			offset = vec2(1.0*Q,1.0*T);
			u = -point.x;
			v = -point.y;
		}else{
			offset = vec2(3.0*Q,1.0*T);
			u = -point.x;
			v = -point.y;
		}
	}

	vec3 coord = vec3(u,v,1); // Transform unit cube plane coordinates to homogeneous coordinates
	vec3 homogeneous = scalTrans * coord; // Scale and Translate (u,v,1) to the corresponding coordinates
	vec2 cart = homogeneous.xy; // Transform to cartesian coordinates
	cart += offset; // Add the corresponding top left corner as offset to get the correct coordinates in the cube map
		
	return cart;
}

void main()
{
	gl_FragColor = texture2D(texture, computeTextCoords(R));
}
