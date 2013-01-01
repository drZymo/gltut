#version 150 core

uniform sampler2D textures[2];

in float fade_factor;
in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 out_Color;

void main(void)
{
	out_Color = pass_Color;
	// Override out_Color with our texture pixel
	out_Color = mix(
		texture2D(textures[0], pass_TextureCoord),
		texture2D(textures[1], pass_TextureCoord),
		fade_factor);
}