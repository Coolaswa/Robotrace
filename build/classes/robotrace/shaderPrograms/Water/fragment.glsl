#version 120
varying vec3 P;
varying vec3 N; // fragment normal in eye space.


vec4 shading(vec3 P, vec3 N, gl_LightSourceParameters light, gl_MaterialParameters mat){
	vec4 result = vec4(0,0,0,1);
	vec4 scene = vec4(0.2,0.2,0.2,1);
	result += mat.ambient * scene;

	vec3 L;
	if(light.position.w != 0){
		vec3 LnonInf = light.position.xyz/light.position.w;
		L = normalize(LnonInf - P);
	}else{
		L = normalize(light.position.xyz);
	}

	result += mat.diffuse * light.diffuse * max(dot(N,L),0);
	vec3 V = normalize(-P);
	result += mat.specular * light.specular * pow(max(0.0,dot(normalize(L+V),N)),mat.shininess);
	return result;
}

void main() {
	gl_LightSourceParameters light = gl_LightSource[0];
	gl_MaterialParameters mat = gl_FrontMaterial;

	gl_FragColor = gl_Color * shading(P, N, light, mat);
}
