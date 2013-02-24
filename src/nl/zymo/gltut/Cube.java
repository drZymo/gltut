package nl.zymo.gltut;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class Cube implements AutoCloseable
{
	private static final float vertexData[] =
	{
        // Object positions        	// Object colors
		-1f,  1f, -1f,				1f, 1f, 1f, 1f, //0
		 1f,  1f, -1f,				1f, 1f, 0f, 1f, //1
		 1f,  1f,  1f,				1f, 0f, 1f, 1f, //2
		-1f,  1f,  1f,				0f, 1f, 1f, 1f, //3
		-1f, -1f, -1f,				0f, 0f, 1f, 1f, //4
		 1f, -1f, -1f,				0f, 1f, 0f, 1f, //5
		 1f, -1f,  1f,				1f, 0f, 0f, 1f, //6
		-1f, -1f,  1f,				0f, 0f, 0f, 1f, //7
	};

	private static final byte indexData[] =
	{
		// front
		0, 4, 5,
		5, 1, 0,

		// back
		2, 6, 7,
		7, 3, 2,

		// right
		1, 5, 6,
		6, 2, 1,

		// left
		3, 7, 4,
		4, 0, 3,

		// top
		3, 0, 1,
		1, 2, 3,

		//bottom
		4, 7, 6,
		6, 5, 4,
	};

	private int vertexArrayObject;

	private int vertexBufferObject;
	private int elementBufferObject;

	public Cube(int attrib_position, int attrib_color)
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
