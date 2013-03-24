package nl.zymo.gltut;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class Cylinder implements AutoCloseable
{
	private static final float vertexData[] =
	{
		0, 1, 0,                 	1f, 1f, 1f, 1f, // 0 (center top)
		1, 1, 0,                 	1f, 1f, 1f, 1f, // 1
		0.866025404f, 1, 0.5f,   	1f, 1f, 1f, 1f, // 2
		0.5f, 1, 0.866025404f,   	1f, 1f, 1f, 1f, // 3
		0, 1, 1,                 	1f, 1f, 1f, 1f, // 4
		-0.5f, 1, 0.866025404f,  	1f, 1f, 1f, 1f, // 5
		-0.866025404f, 1, 0.5f,  	1f, 1f, 1f, 1f, // 6
		-1, 1, 0,                	1f, 1f, 1f, 1f, // 7
		-0.866025404f, 1, -0.5f, 	1f, 1f, 1f, 1f, // 8
		-0.5f, 1, -0.866025404f, 	1f, 1f, 1f, 1f, // 9
		0, 1, -1,                	1f, 1f, 1f, 1f, // 10
		0.5f, 1, -0.866025404f,  	1f, 1f, 1f, 1f, // 11
		0.866025404f, 1, -0.5f,  	1f, 1f, 1f, 1f, // 12

		0, -1, 0,                	1f, 1f, 1f, 1f, // 13 (center bottom)
		1, -1, 0,                	1f, 1f, 1f, 1f, // 14
		0.866025404f, -1, 0.5f,  	1f, 1f, 1f, 1f, // 15
		0.5f, -1, 0.866025404f,  	1f, 1f, 1f, 1f, // 16
		0, -1, 1,                	1f, 1f, 1f, 1f, // 17
		-0.5f, -1, 0.866025404f, 	1f, 1f, 1f, 1f, // 18
		-0.866025404f, -1, 0.5f, 	1f, 1f, 1f, 1f, // 19
		-1, -1, 0,               	1f, 1f, 1f, 1f, // 20
		-0.866025404f, -1, -0.5f,	1f, 1f, 1f, 1f, // 21
		-0.5f, -1, -0.866025404f,	1f, 1f, 1f, 1f, // 22
		0, -1, -1,               	1f, 1f, 1f, 1f, // 23
		0.5f, -1, -0.866025404f, 	1f, 1f, 1f, 1f, // 24
		0.866025404f, -1, -0.5f, 	1f, 1f, 1f, 1f, // 25
	};

	private static final byte indexData[] =
	{
		0, 1, 2,
		0, 2, 3,
		0, 3, 4,
		0, 4, 5,
		0, 5, 6,
		0, 6, 7,
		0, 7, 8,
		0, 8, 9,
		0, 9, 10,
		0, 10, 11,
		0, 11, 12,
		0, 12, 1,

		2, 1, 14,
		14, 15, 2,

		3, 2, 15,
		15, 16, 3,

		4, 3, 16,
		16, 17, 4,

		5, 4, 17,
		17, 18, 5,

		6, 5, 18,
		18, 19, 6,

		7, 6, 19,
		19, 20, 7,

		8, 7, 20,
		20, 21, 8,

		9, 8, 21,
		21, 22, 9,

		10, 9, 22,
		22, 23, 10,

		11, 10, 23,
		23, 24, 11,

		12, 11, 24,
		24, 25, 12,

		1, 12, 25,
		25, 14, 1,

		13, 25, 24,
		13, 24, 23,
		13, 23, 22,
		13, 22, 21,
		13, 21, 20,
		13, 20, 19,
		13, 19, 18,
		13, 18, 17,
		13, 17, 16,
		13, 16, 15,
		13, 15, 14,
		13, 14, 25,
	};

	private int vertexArrayObject;

	private int vertexBufferObject;
	private int elementBufferObject;

	public Cylinder(int attrib_position, int attrib_color)
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
