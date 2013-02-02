#version 330

in vec4 position;
in vec4 color;

uniform mat4 modelToClipMatrix;

smooth out vec4 theColor;

void main()
{
	gl_Position = modelToClipMatrix * position;
	theColor = color;
}
