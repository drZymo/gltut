#version 150 core

uniform float timer;

in vec4 in_Position;
in vec4 in_Color;
in vec2 in_TextureCoord;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

out float fade_factor;

mat4 view_frustum(float angle_of_view, float aspect_ratio, float z_near, float z_far)
{
	return mat4(
		vec4(1.0/tan(angle_of_view),           0.0, 0.0, 0.0),
		vec4(0.0, aspect_ratio/tan(angle_of_view),  0.0, 0.0),
		vec4(0.0, 0.0,    (z_far+z_near)/(z_far-z_near), 1.0),
		vec4(0.0, 0.0, -2.0*z_far*z_near/(z_far-z_near), 0.0)
	);
}

mat4 scale(float x, float y, float z)
{
	return mat4(
		vec4(x,   0.0, 0.0, 0.0),
		vec4(0.0, y,   0.0, 0.0),
		vec4(0.0, 0.0, z,   0.0),
		vec4(0.0, 0.0, 0.0, 1.0)
	);
}

mat4 translate(float x, float y, float z)
{
	return mat4(
		vec4(1.0, 0.0, 0.0, 0.0),
		vec4(0.0, 1.0, 0.0, 0.0),
		vec4(0.0, 0.0, 1.0, 0.0),
		vec4(x,   y,   z,   1.0)
	);
}

mat4 rotate_x(float theta)
{
	return mat4(
		vec4(1.0,         0.0,         0.0, 0.0),
		vec4(0.0,  cos(theta),  sin(theta), 0.0),
		vec4(0.0, -sin(theta),  cos(theta), 0.0),
		vec4(0.0,         0.0,         0.0, 1.0)
	);
}

void main()
{
	gl_Position = view_frustum(radians(60.0), 4.0/3.0, 0.5, 5.0)
		* translate(cos(timer), 0.0, 3.0+sin(timer))
		* rotate_x(timer)
		* scale(4.0/3.0, 1.0, 1.0)
		* in_Position;

	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
	fade_factor = sin(timer) * 0.5 + 0.5;
}
