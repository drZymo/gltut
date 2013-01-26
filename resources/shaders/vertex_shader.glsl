#version 330

in vec4 position;
in vec4 color;

uniform vec3 offset;
uniform mat4 cameraToClipMatrix;

smooth out vec4 theColor;

void main()
{
	vec4 cameraPos = position + vec4(offset.x, offset.y, offset.z, 0.0);

	gl_Position = cameraToClipMatrix * cameraPos;
	theColor = color;
}
