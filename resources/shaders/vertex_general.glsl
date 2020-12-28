#version 330 core

in vec3 position;
//in vec3 color_in;
in vec2 uv_in;
in vec3 normal_in;
in float light_in;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

//out vec3 color;
out vec2 uv;
out vec3 normal;
out float light;

void main(){
    uv = uv_in;
    //color = color_in;
    normal = normal_in;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    light = light_in;
}