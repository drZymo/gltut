#version 330

smooth in vec4 theColor;

out vec4 outputColor;

uniform float fragLoopDuration;
uniform float time;

const vec4 secondColor = vec4(1.0f, 1.0f, 1.0f, 1.0f);

#define TAU  6.2831853071795864769253f

void main()
{
	float currTime = mod(time, fragLoopDuration);
	float currLerp = sin(TAU * currTime / fragLoopDuration) * 0.5f + 0.5f;

	outputColor = mix(theColor, secondColor, currLerp);
}
