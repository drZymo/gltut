package nl.zymo.gltut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.ByteBuffer;
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
import org.lwjgl.opengl.GL32;
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

		createShaderProgram();
		createVertexBuffers();
		createVertexArray();

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

		destroyVertexArray();
		destroyVertexBuffers();
		destroyShaderProgram();

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

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDepthRange(0.0f, 1.0f);

		exitOnGLError("setupOpenGL");
	}

	private void destroyOpenGL()
	{
		Display.destroy();
	}

	private static final float vertexData[] =
	{
        //Object 1 positions        //Object 1 colors
        -0.8f, 0.2f, -1.75f,        0.75f, 0.75f, 1.0f, 1.0f,
        -0.8f, 0.0f, -1.25f,        0.75f, 0.75f, 1.0f, 1.0f,
        0.8f, 0.0f, -1.25f,         0.75f, 0.75f, 1.0f, 1.0f,
        0.8f, 0.2f, -1.75f,         0.75f, 0.75f, 1.0f, 1.0f,

        -0.8f, -0.2f, -1.75f,       0.0f, 0.5f, 0.0f, 1.0f,
        -0.8f, 0.0f, -1.25f,        0.0f, 0.5f, 0.0f, 1.0f,
        0.8f, 0.0f, -1.25f,         0.0f, 0.5f, 0.0f, 1.0f,
        0.8f, -0.2f, -1.75f,        0.0f, 0.5f, 0.0f, 1.0f,

        -0.8f, 0.2f, -1.75f,        1.0f, 0.0f, 0.0f, 1.0f,
        -0.8f, 0.0f, -1.25f,        1.0f, 0.0f, 0.0f, 1.0f,
        -0.8f, -0.2f, -1.75f,       1.0f, 0.0f, 0.0f, 1.0f,

        0.8f, 0.2f, -1.75f,         0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, 0.0f, -1.25f,         0.8f, 0.8f, 0.8f, 1.0f,
        0.8f, -0.2f, -1.75f,        0.8f, 0.8f, 0.8f, 1.0f,

        -0.8f, -0.2f, -1.75f,       0.5f, 0.5f, 0.0f, 1.0f,
        -0.8f, 0.2f, -1.75f,        0.5f, 0.5f, 0.0f, 1.0f,
        0.8f, 0.2f, -1.75f,         0.5f, 0.5f, 0.0f, 1.0f,
        0.8f, -0.2f, -1.75f,        0.5f, 0.5f, 0.0f, 1.0f,

        //Object 2 positions        //Object 2 colors
        0.2f, 0.8f, -1.75f,         1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, 0.8f, -1.25f,         1.0f, 0.0f, 0.0f, 1.0f,
        0.0f, -0.8f, -1.25f,        1.0f, 0.0f, 0.0f, 1.0f,
        0.2f, -0.8f, -1.75f,        1.0f, 0.0f, 0.0f, 1.0f,

        -0.2f, 0.8f, -1.75f,        0.5f, 0.5f, 0.0f, 1.0f,
        0.0f, 0.8f, -1.25f,         0.5f, 0.5f, 0.0f, 1.0f,
        0.0f, -0.8f, -1.25f,        0.5f, 0.5f, 0.0f, 1.0f,
        -0.2f, -0.8f, -1.75f,       0.5f, 0.5f, 0.0f, 1.0f,

        0.2f, 0.8f, -1.75f,         0.0f, 0.5f, 0.0f, 1.0f,
        0.0f, 0.8f, -1.25f,         0.0f, 0.5f, 0.0f, 1.0f,
        -0.2f, 0.8f, -1.75f,        0.0f, 0.5f, 0.0f, 1.0f,

        0.2f, -0.8f, -1.75f,        0.75f, 0.75f, 1.0f, 1.0f,
        0.0f, -0.8f, -1.25f,        0.75f, 0.75f, 1.0f, 1.0f,
        -0.2f, -0.8f, -1.75f,       0.75f, 0.75f, 1.0f, 1.0f,

        -0.2f, 0.8f, -1.75f,        0.8f, 0.8f, 0.8f, 1.0f,
        0.2f, 0.8f, -1.75f,         0.8f, 0.8f, 0.8f, 1.0f,
        0.2f, -0.8f, -1.75f,        0.8f, 0.8f, 0.8f, 1.0f,
        -0.2f, -0.8f, -1.75f,       0.8f, 0.8f, 0.8f, 1.0f,
	};

	private static final byte indexData[] =
	{
		0, 2, 1,
		3, 2, 0,

		4, 5, 6,
		6, 7, 4,

		8, 9, 10,
		11, 13, 12,

		14, 16, 15,
		17, 16, 14,
	};

	private int vertexBufferObject;
	private int elementBufferObject;

	private void createVertexBuffers()
	{
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		ByteBuffer elementBuffer = BufferUtils.createByteBuffer(indexData.length);
		elementBuffer.put(indexData);
		elementBuffer.flip();

		elementBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		exitOnGLError("createVertexBuffers");
	}

	private void destroyVertexBuffers()
	{
		GL15.glDeleteBuffers(vertexBufferObject);
		vertexBufferObject = 0;

		exitOnGLError("destroyVertexBuffers");
	}

	private int vertexArrayObject;

	private void createVertexArray()
	{
		vertexArrayObject = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayObject);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);

		int stride = 7 * 4;
		GL20.glEnableVertexAttribArray(attrib_position);
		GL20.glVertexAttribPointer(attrib_position, 3, GL11.GL_FLOAT, false, stride, 0);
		GL20.glEnableVertexAttribArray(attrib_color);
		GL20.glVertexAttribPointer(attrib_color, 4, GL11.GL_FLOAT, false, stride, 3 * 4);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);

		GL30.glBindVertexArray(0);

		exitOnGLError("createVertexArray");
	}

	private void destroyVertexArray()
	{
		GL30.glDeleteVertexArrays(vertexArrayObject);
		vertexArrayObject = 0;

		exitOnGLError("destroyVertexArray");
	}

	private int programId;

	private int attrib_position;
	private int attrib_color;

	private int uniform_modelToCameraMatrix;
	private int uniform_cameraToClipMatrix;

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

		uniform_modelToCameraMatrix = GL20.glGetUniformLocation(programId, "modelToCameraMatrix");
		uniform_cameraToClipMatrix = GL20.glGetUniformLocation(programId, "cameraToClipMatrix");

		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);

		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);

		exitOnGLError("createShaderProgram");
	}

	private void destroyShaderProgram()
	{
		GL20.glDeleteProgram(programId);
		programId = 0;

		attrib_position = 0;
		attrib_color = 0;

		uniform_modelToCameraMatrix = 0;
		uniform_cameraToClipMatrix = 0;

		exitOnGLError("destroyShaderProgram");
	}

	private void initializeShaderProgram()
	{
		float zNear = 0.5f; float zFar = 45.0f;
		float ex = 0f; float ey = 0f; float ez = 1f;

		float frustumScale = CalculateFrustumScale(60.0f);

		// NOTE!: Update this matrix when window is resized, because width and height will be different then
		Matrix4 cameraToClipMatrix = new Matrix4();
		cameraToClipMatrix.put(0, 0, (frustumScale * height) / width);
		cameraToClipMatrix.put(1, 1, frustumScale);
		cameraToClipMatrix.put(3, 0, ex);
		cameraToClipMatrix.put(3, 1, ey);
		cameraToClipMatrix.put(2, 2, (zFar + zNear) / (zNear - zFar));
		cameraToClipMatrix.put(3, 2, (2 * zFar * zNear) / (zNear - zFar));
		cameraToClipMatrix.put(2, 3, -ez);

		GL20.glUseProgram(programId);
		GL20.glUniformMatrix4(uniform_cameraToClipMatrix, false, cameraToClipMatrix.getBuffer());
		GL20.glUseProgram(0);
	}

	private static float CalculateFrustumScale(float fov)
	{
		return (float) (1.0 / Math.tan(fov * Math.PI / 360.0));
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

	private Matrix4 object1Transform = new Matrix4(true);
	private Matrix4 object2Transform = new Matrix4(true);

	private void logic()
	{
		double time = getTime();
		double theta = (time / 5.0) * 2 * Math.PI;
		float offsetZ = (float)Math.sin(theta) * 0.75f + 0.25f;
		object2Transform.setOffset(0, 0, -offsetZ);
	}

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL20.glUseProgram(programId);

		GL30.glBindVertexArray(vertexArrayObject);

		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, object1Transform.getBuffer());
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0);

		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, object2Transform.getBuffer());
		GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0, 18);

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
