package nl.zymo.gltut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Window
{
	private int width;
	private int height;
	private String title;

	public Window(int width, int height, String title)
	{
		this.width = width;
		this.height = height;
		this.title = title;
	}

	public void Show()
	{
		setupOpenGL();

		createVertexArray();
		createShaderProgram();

		initializeShaderProgram();

		while (!Display.isCloseRequested())
		{
			// Do a single loop (logic/render)
			logic();
			render();

			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		destroyShaderProgram();
		destroyVertexArray();

		destroyOpenGL();
	}

	private void setupOpenGL()
	{
		// Setup an OpenGL context with API version 3.2
		try
		{
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(3, 3);
			contextAtrributes.withForwardCompatible(true);
			contextAtrributes.withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(title);
			Display.create(pixelFormat, contextAtrributes);
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		GL11.glViewport(0, 0, width, height);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		exitOnGLError("setupOpenGL");
	}

	private void destroyOpenGL()
	{
		Display.destroy();
	}

	private static final float vertexData[] =
		{
		0.25f,  0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,

		0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,	0.0f, 0.0f, 1.0f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,	0.8f, 0.8f, 0.8f, 1.0f,

		-0.25f,  0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,

		-0.25f,  0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 0.0f, 1.0f,

		0.25f,  0.25f, -1.25f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,

		0.25f,  0.25f, -1.25f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,
		0.25f,  0.25f, -2.75f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,
		0.25f, -0.25f, -2.75f, 1.0f,	0.5f, 0.5f, 0.0f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,
		0.25f,  0.25f, -1.25f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,

		0.25f,  0.25f, -2.75f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,
		-0.25f,  0.25f, -1.25f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,
		-0.25f,  0.25f, -2.75f, 1.0f,	1.0f, 0.0f, 0.0f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,
		0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,

		0.25f, -0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,
		-0.25f, -0.25f, -2.75f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,
		-0.25f, -0.25f, -1.25f, 1.0f,	0.0f, 1.0f, 1.0f, 1.0f,
		};

	private int vertexArrayObject;
	private int vertexBufferObject;

	private void createVertexArray()
	{
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();

		vertexArrayObject = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayObject);

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);

		exitOnGLError("createVertexArray");
	}

	private void destroyVertexArray()
	{
		GL15.glDeleteBuffers(vertexBufferObject);
		vertexBufferObject = 0;
		GL30.glDeleteVertexArrays(vertexArrayObject);
		vertexArrayObject = 0;

		exitOnGLError("destroyVertexArray");
	}

	private int programId;

	private int attrib_position;
	private int attrib_color;

	private int uniform_offset;
	private int uniform_perspectiveMatrix;

	private void createShaderProgram()
	{
		int vertexShaderId = this.loadShader("shaders/vertex_shader.glsl", GL20.GL_VERTEX_SHADER);
		int fragmentShaderId = this.loadShader("shaders/fragment_shader.glsl", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderId);
		GL20.glAttachShader(programId, fragmentShaderId);
		GL20.glLinkProgram(programId);

		GL20.glValidateProgram(programId);

		attrib_position = GL20.glGetAttribLocation(programId, "position");
		attrib_color = GL20.glGetAttribLocation(programId, "color");

		uniform_offset = GL20.glGetUniformLocation(programId, "offset");
		uniform_perspectiveMatrix = GL20.glGetUniformLocation(programId, "perspectiveMatrix");

		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);

		GL20.glDeleteShader(vertexArrayObject);
		GL20.glDeleteShader(fragmentShaderId);

		exitOnGLError("createShaderProgram");
	}

	private void destroyShaderProgram()
	{
		GL20.glDeleteProgram(programId);
		programId = 0;

		attrib_position = 0;
		attrib_color = 0;

		uniform_offset = 0;
		uniform_perspectiveMatrix = 0;

		exitOnGLError("destroyShaderProgram");
	}

	private void initializeShaderProgram()
	{
		float zNear = 0.5f; float zFar = 3.0f;
		float ex = 0f; float ey = 0f; float ez = 1f;
		float fov = 60.0f;

		float frustumScale = (float)(1.0 / Math.tan(fov * Math.PI / 360.0));
		float[] perspectiveMatrix = new float[16];
		perspectiveMatrix[0] = (frustumScale * height) / width; // NOTE!: Redo this when window is resized
		perspectiveMatrix[5] = frustumScale;
		perspectiveMatrix[12] = ex;
		perspectiveMatrix[13] = ey;
		perspectiveMatrix[10] = (zFar + zNear) / (zNear - zFar);
		perspectiveMatrix[14] = (2 * zFar * zNear) / (zNear - zFar);
		perspectiveMatrix[11] = -ez;

		FloatBuffer perspectiveMatrixBuffer = BufferUtils.createFloatBuffer(16);
		perspectiveMatrixBuffer.put(perspectiveMatrix);
		perspectiveMatrixBuffer.flip();

		GL20.glUseProgram(programId);
		GL20.glUniformMatrix4(uniform_perspectiveMatrix, false, perspectiveMatrixBuffer);
		GL20.glUseProgram(0);
	}

	private int loadShader(String ref, int type)
	{
		String shaderSource = ResourceAsString(ref);

		int shaderId = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderId, shaderSource);
		GL20.glCompileShader(shaderId);

		if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}

		exitOnGLError("loadShader");

		return shaderId;
	}

	private float offsetX;
	private float offsetY;

	private void logic()
	{
		double time = getTime();
		double theta = (Math.PI * 2.0) * time / 5;
		offsetX = (float)Math.cos(theta) * 0.75f;
		offsetY = (float)Math.sin(theta) * 0.75f;
	}

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(programId);

		GL20.glUniform2f(uniform_offset, offsetX, offsetY);

		GL30.glBindVertexArray(vertexArrayObject);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);

		GL20.glEnableVertexAttribArray(attrib_position);
		GL20.glVertexAttribPointer(attrib_position, 4, GL11.GL_FLOAT, false, 32, 0);
		GL20.glEnableVertexAttribArray(attrib_color);
		GL20.glVertexAttribPointer(attrib_color, 4, GL11.GL_FLOAT, false, 32, 16);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);

		GL20.glDisableVertexAttribArray(attrib_position);
		GL20.glDisableVertexAttribArray(attrib_color);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);

		exitOnGLError("render");
	}

	/**
	 * Get the time in seconds
	 *
	 * @return The system time in seconds
	 */
	public double getTime()
	{
		return (double)Sys.getTime() / (double)Sys.getTimerResolution();
	}

	private String ResourceAsString(String ref)
	{
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream(ref);
		Reader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();
		try
		{
			char[] buffer = new char[8192];
			int read;
			while ((read = reader.read(buffer, 0, buffer.length)) > 0)
			{
				builder.append(buffer, 0, read);
			}
			stream.close();
		} catch (IOException e)
		{
			System.err.println("Failed to load: " + ref);
			System.exit(0);
		}
		return builder.toString();
	}

	public void exitOnGLError(String errorMessage)
	{
		int errorValue = GL11.glGetError();

		if (errorValue != GL11.GL_NO_ERROR)
		{
			String errorString = GLU.gluErrorString(errorValue);
			System.err.println("ERROR - " + errorMessage + ": " + errorString);

			if (Display.isCreated())
				Display.destroy();
			System.exit(-1);
		}
	}
}
