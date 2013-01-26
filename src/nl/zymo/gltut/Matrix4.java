package nl.zymo.gltut;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4
{
	private FloatBuffer buffer;

	public Matrix4()
	{
		buffer = BufferUtils.createFloatBuffer(16);
	}

	public float get(int col, int row)
	{
		return buffer.get(col * 4 + row);
	}

	public void put(int col, int row, float value)
	{
		buffer.put(col * 4 + row, value);
	}

	public FloatBuffer getBuffer()
	{
		return buffer;
	}
}
