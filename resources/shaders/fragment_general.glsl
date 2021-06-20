#version 330 core

in vec4 color;
in vec2 uv;
in vec3 normal;
in float light;

uniform sampler2D tex_sampler;
uniform vec4 line_color;
uniform vec4 poly_color;
uniform bool textured;

out vec4 color_out;

void main(){
	if(line_color.a > 0) color_out = line_color;
	else if(poly_color.a > 0) color_out = poly_color;
	else if(color.a > 0 && !textured) color_out = color;
	else{
		color_out = texture(tex_sampler, uv).rgba;
		if(color_out.a < 0.1) discard;
		if(light > -1){
			color_out = vec4(color_out.rgb * light, color_out.a);
		}
	}
}
