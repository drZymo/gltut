package nl.zymo.gltut;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class Matrix4
{
	private FloatBuffer buffer;

	public Matrix4()
	{
		this(false);
	}

	public Matrix4(Boolean identity)
	{
		buffer = BufferUtils.createFloatBuffer(16);
		if (identity)
		{
			put(0, 0, 1f);
			put(1, 1, 1f);
			put(2, 2, 1f);
			put(3, 3, 1f);
		}
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
