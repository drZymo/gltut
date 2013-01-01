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
	mat3 projection = mat3(
		vec3(3.0/4.0, 0.0, 0.0),
		vec3(    0.0, 1.0, 0.0),
		vec3(    0.0, 0.0, 1.0)
	);

	mat3 rotation = mat3(
		vec3( cos(timer),  sin(timer),  0.0),
		vec3(-sin(timer),  cos(timer),  0.0),
		vec3(        0.0,         0.0,  1.0)
	);

	mat3 scale = mat3(
		vec3(4.0/3.0, 0.0, 0.0),
		vec3(    0.0, 1.0, 0.0),
		vec3(    0.0, 0.0, 1.0)
	);

	gl_Position = vec4(projection * rotation * scale * in_Position.xyz, 1.0);
	
	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
	fade_factor = sin(timer) * 0.5 + 0.5;
}