
uniform sampler2D texture;
varying vec3 P;
varying vec3 N; 

vec4 shading(vec3 P, vec3 N, gl_LightSourceParameters light, gl_MaterialParameters mat){
	vec4 result = vec4(0,0,0,1);
        result += mat.ambient * light.ambient;

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

void main()
{
	gl_LightSourceParameters light = gl_LightSource[0];
	gl_MaterialParameters mat = gl_FrontMaterial;
	vec4 color = texture2D(texture,gl_TexCoord[0].st);
	gl_FragColor = shading(P, N, light, mat) * color;
}