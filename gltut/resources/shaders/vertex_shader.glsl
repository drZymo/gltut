#version 330

in vec4 position;
in vec4 color;

uniform vec2 offset;

smooth out vec4 theColor;

void main()
{
	vec4 totalOffset = vec4(offset.x, offset.y, 0.0, 0.0);
	gl_Position = position + totalOffset;
	theColor = color;
}
