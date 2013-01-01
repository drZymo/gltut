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
	const mat4 projection = mat4(
		vec4(3.0/4.0, 0.0, 0.0, 0.0),
		vec4(    0.0, 1.0, 0.0, 0.0),
		vec4(    0.0, 0.0, 0.5, 0.5),
		vec4(    0.0, 0.0, 0.0, 1.0)
	);

	mat4 rotation = mat4(
		vec4(1.0,         0.0,         0.0, 0.0),
		vec4(0.0,  cos(timer),  sin(timer), 0.0),
		vec4(0.0, -sin(timer),  cos(timer), 0.0),
		vec4(0.0,         0.0,         0.0, 1.0)
	);

	mat4 scale = mat4(
		vec4(4.0/3.0, 0.0, 0.0, 0.0),
		vec4(    0.0, 1.0, 0.0, 0.0),
		vec4(    0.0, 0.0, 1.0, 0.0),
		vec4(    0.0, 0.0, 0.0, 1.0)
	);

	gl_Position = projection * rotation * scale * in_Position;
	
	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
	fade_factor = sin(timer) * 0.5 + 0.5;
}