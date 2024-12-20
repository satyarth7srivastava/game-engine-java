#type vertex
#version 330 core
#extension GL_ARB_explicit_attribute_location : require
#extension GL_ARB_explicit_uniform_location : require
layout (location=0) in vec3 aPos;
layout (loaction=1) in vec4 aColor;

out vec4 fColor;

void main(){
    fColor = aColor;
    gl_Position = vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;

out vec4 color;

void main(){
    color = fColor;
}