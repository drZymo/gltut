#version 150 core

uniform float timer;

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

out float fade_factor;

void main(void)
{
	gl_Position = in_Position;
	
	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
	fade_factor = sin(timer) * 0.5 + 0.5;
}