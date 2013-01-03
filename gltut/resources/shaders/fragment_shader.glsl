#version 330

smooth in vec4 theColor;

out vec4 outputColor;

uniform float fragLoopDuration;
uniform float time;

const vec4 secondColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);

void main()
{
	float currTime = mod(time, fragLoopDuration);
	float currLerp = currTime / fragLoopDuration;

	outputColor = mix(theColor, secondColor, currLerp);
}
