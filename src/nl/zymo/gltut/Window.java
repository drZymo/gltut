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
import org.lwjgl.input.Keyboard;
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
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

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

	private int uniform_modelToWorldMatrix;
	private int uniform_worldToCameraMatrix;
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

		uniform_modelToWorldMatrix = GL20.glGetUniformLocation(programId, "modelToWorldMatrix");
		uniform_worldToCameraMatrix = GL20.glGetUniformLocation(programId, "worldToCameraMatrix");
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

		uniform_modelToWorldMatrix = 0;
		uniform_worldToCameraMatrix = 0;
		uniform_cameraToClipMatrix = 0;

		exitOnGLError("destroyShaderProgram");
	}

	private void initializeShaderProgram()
	{
		// NOTE!: Update this matrix when window is resized, because width and height will be different then
		Matrix4 cameraToClipMatrix = GetPerspectiveMatrix(60.0f, (float)height / (float)width, 0.5f, 45.0f);

		GL20.glUseProgram(programId);
		GL20.glUniformMatrix4(uniform_cameraToClipMatrix, false, cameraToClipMatrix.getBuffer());
		GL20.glUseProgram(0);
	}

	private static Matrix4 GetPerspectiveMatrix(float fov, float aspectRatio, float zNear, float zFar)
	{
		float frustumScale = (float)(1.0 / Math.tan(fov * Math.PI / 360.0));
		Matrix4 matrix = new Matrix4();
		matrix.put(0, 0, frustumScale * aspectRatio);
		matrix.put(1, 1, frustumScale);
		matrix.put(2, 2, (zFar + zNear) / (zNear - zFar));
		matrix.put(3, 2, (2 * zFar * zNear) / (zNear - zFar));
		matrix.put(2, 3, -1);
		return matrix;
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

	private double camAngle = 0;
	private double camTilt = 0;

	private void logic()
	{
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
		{
			camAngle += 0.5;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
		{
			camAngle -= 0.5;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
		{
			camTilt -= 0.5;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
		{
			camTilt += 0.5;
		}
		camTilt = Math.min(Math.max(camTilt, -90), 90);
		camAngle = camAngle % 360;

		double time = getTime();
		double theta = (time / 5.0) * 2 * Math.PI;
		float offsetZ = (float)Math.sin(theta) * 0.75f + 0.25f;
		object2Transform.setOffset(0, 0, -offsetZ);

		UpdateWorldToCameraMatrix(camAngle, camTilt);
	}

	private void UpdateWorldToCameraMatrix(double angle, double tilt)
	{
		Vector3f cameraPos = ResolveCameraPosition(angle, tilt, 5);
		Vector3f lookAt = new Vector3f(0, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);

		Vector3f lookDir = Vector3f.sub(lookAt, cameraPos, null).normalise(null);
		Vector3f upDir = up.normalise(null);

		Vector3f rightDir = Vector3f.cross(lookDir, upDir, null).normalise(null);
		Vector3f perpUpDir = Vector3f.cross(rightDir, lookDir, null);

		Matrix4f rotMat = new Matrix4f();
		rotMat.m00 = rightDir.x;
		rotMat.m01 = rightDir.y;
		rotMat.m02 = rightDir.z;
		rotMat.m03 = 0;
		rotMat.m10 = perpUpDir.x;
		rotMat.m11 = perpUpDir.y;
		rotMat.m12 = perpUpDir.z;
		rotMat.m13 = 0;
		rotMat.m20 = -lookDir.x;
		rotMat.m21 = -lookDir.y;
		rotMat.m22 = -lookDir.z;
		rotMat.m23 = 0;
		rotMat.m30 = 0;
		rotMat.m31 = 0;
		rotMat.m32 = 0;
		rotMat.m33 = 1;

		rotMat.transpose();

		Matrix4f transMat = Matrix4f.setIdentity(new Matrix4f());
		transMat.m30 = -cameraPos.x;
		transMat.m31 = -cameraPos.y;
		transMat.m32 = -cameraPos.z;
		transMat.m33 = 1;

		worldToCameraMatrix = Matrix4f.mul(rotMat, transMat, null);
	}

	private Vector3f ResolveCameraPosition(double phi, double theta, double radius)
	{
		double phiRad = ToRadians(phi);
		double thetaRad = ToRadians(theta + 90.0);

		double sinPhi = Math.sin(phiRad);
		double cosPhi = Math.cos(phiRad);
		double sinTheta = Math.sin(thetaRad);
		double cosTheta = Math.cos(thetaRad);

		Vector3f dirToCamera = new Vector3f((float)(sinTheta * cosPhi), (float)cosTheta, (float)(sinTheta * sinPhi));

		dirToCamera.scale((float)radius);

		// TODO: dirToCamera.translate(target.x, target.y, target.z);
		return dirToCamera;
	}

	private Matrix4f worldToCameraMatrix = Matrix4f.setIdentity(new Matrix4f());

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL20.glUseProgram(programId);

		GL30.glBindVertexArray(vertexArrayObject);

		FloatBuffer worldToCameraMatrixBuffer = BufferUtils.createFloatBuffer(16);
		worldToCameraMatrix.store(worldToCameraMatrixBuffer);
		worldToCameraMatrixBuffer.flip();
		GL20.glUniformMatrix4(uniform_worldToCameraMatrix, false, worldToCameraMatrixBuffer);

		GL20.glUniformMatrix4(uniform_modelToWorldMatrix, false, object1Transform.getBuffer());
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0);

		GL20.glUniformMatrix4(uniform_modelToWorldMatrix, false, object2Transform.getBuffer());
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

	private static double ToRadians(double degrees)
	{
		return degrees * 2 * Math.PI / 360.0;
	}

	private static double ToDegrees(double radians)
	{
		return radians * 360.0 / (2 * Math.PI);
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
