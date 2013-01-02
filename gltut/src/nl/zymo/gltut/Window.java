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

		setupVertices();
		setupShaders();

		while (!Display.isCloseRequested())
		{
			// Do a single loop (logic/render)
			this.loopCycle();

			// Force a maximum FPS of about 60
			Display.sync(60);
			// Let the CPU synchronize with the GPU if GPU is tagging behind
			Display.update();
		}

		// Destroy OpenGL (Display)
		this.destroyOpenGL();
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

		this.exitOnGLError("setupOpenGL");
	}

	private void destroyOpenGL()
	{
		this.exitOnGLError("destroyOpenGL");

		Display.destroy();
	}

	private static final float vertexPositions[] = { 0.75f, 0.75f, 0.0f, 1.0f, 0.75f, -0.75f, 0.0f, 1.0f, -0.75f,
			-0.75f, 0.0f, 1.0f, };

	private int vertexArrayId;
	private int positionBufferObject;

	private void setupVertices()
	{
		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
		vertexPositionsBuffer.put(vertexPositions);
		vertexPositionsBuffer.flip();

		vertexArrayId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayId);
		
		positionBufferObject = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexPositionsBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);

		this.exitOnGLError("setupVertices");
	}

	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;

	private void setupShaders()
	{
		// Load the vertex shader
		vertexShaderId = this.loadShader("shaders/vertex_shader.glsl", GL20.GL_VERTEX_SHADER);
		// Load the fragment shader
		fragmentShaderId = this.loadShader("shaders/fragment_shader.glsl", GL20.GL_FRAGMENT_SHADER);

		// Create a new shader program that links both shaders
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShaderId);
		GL20.glAttachShader(programId, fragmentShaderId);
		GL20.glLinkProgram(programId);

		GL20.glValidateProgram(programId);

		this.exitOnGLError("setupShaders");
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

		this.exitOnGLError("loadShader");

		return shaderId;
	}

	private void loopCycle()
	{
		render();
	}

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		this.exitOnGLError("render 1");

		GL20.glUseProgram(programId);
		this.exitOnGLError("render 2");

		GL30.glBindVertexArray(vertexArrayId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferObject);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);
		this.exitOnGLError("render 3");

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
		this.exitOnGLError("render 4");

		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
		GL20.glUseProgram(0);

		this.exitOnGLError("render 5");
	}

	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public double getTime()
	{
		return (Sys.getTime() * 1000.0) / Sys.getTimerResolution();
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
