#version 330 core

in vec3 position;
in vec4 color_in;
in vec2 uv_in;
in vec3 normal_in;
in float light_in;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec4 line_color;
uniform vec4 poly_color;
uniform bool textured;
uniform bool lighting;

out vec4 color;
out vec2 uv;
out vec3 normal;
out float light;
out vec3 fragpos;

void main(){
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    
    if(line_color.a == 0 && poly_color.a == 0){
	    uv = uv_in;
	    color = color_in;
    }
	light = light_in;
    if(lighting){
	    normal = vec3(model * vec4(position, 1)) + normal_in;
    	fragpos = vec3(model * vec4(position, 1));
    }
}