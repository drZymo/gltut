package nl.zymo.gltut;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class Plane implements AutoCloseable
{
	private static final float vertexData[] =
	{
        // Object positions        	// Object colors
		 0, 0.2f, 1, 				0f, 0f, 1f, 1f, //0
		 -1, 0, -1,					0.75f, 0.75f, 1f, 1f, //1
		 0, 0.5f, -0.7f,			0f, 0f, 0.5f, 1f, //2
		 1, 0, -1,					0.75f, 0.75f, 1f, 1f, //3
		 0, 0.2f, -0.8f,			0f, 0f, 0.5f, 1f, //4
	};

	private static final byte indexData[] =
	{
		0, 1, 2,
		0, 2, 3,
		2, 1, 4,
		2, 4, 3,
		4, 1, 0,
		4, 0, 3,
	};

	private int vertexArrayObject;

	private int vertexBufferObject;
	private int elementBufferObject;

	public Plane(int attrib_position, int attrib_color)
	{
		vertexArrayObject = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayObject);

		// vertex buffer
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);


		// element buffer
		ByteBuffer elementBuffer = BufferUtils.createByteBuffer(indexData.length);
		elementBuffer.put(indexData);
		elementBuffer.flip();

		elementBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);


		int stride = 7 * 4;
		GL20.glEnableVertexAttribArray(attrib_position);
		GL20.glVertexAttribPointer(attrib_position, 3, GL11.GL_FLOAT, false, stride, 0);
		GL20.glEnableVertexAttribArray(attrib_color);
		GL20.glVertexAttribPointer(attrib_color, 4, GL11.GL_FLOAT, false, stride, 3 * 4);

		GL30.glBindVertexArray(0);
	}

	public void close()
	{
		GL15.glDeleteBuffers(elementBufferObject);
		elementBufferObject = 0;
		GL15.glDeleteBuffers(vertexBufferObject);
		vertexBufferObject = 0;

		GL30.glDeleteVertexArrays(vertexArrayObject);
		vertexArrayObject = 0;
	}

	public void render()
	{
		GL30.glBindVertexArray(vertexArrayObject);

		GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0, 0);

		GL30.glBindVertexArray(0);
	}
}
