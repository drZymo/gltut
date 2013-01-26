#version 330

in vec4 position;
in vec4 color;

uniform mat4 modelToCameraMatrix;
uniform mat4 cameraToClipMatrix;

smooth out vec4 theColor;

void main()
{
	vec4 cameraPos = modelToCameraMatrix * position;
	gl_Position = cameraToClipMatrix * cameraPos;
	theColor = color;
}
