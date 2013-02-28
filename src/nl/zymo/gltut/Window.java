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

public class Window
{
	private int width;
	private int height;
	private String title;

	private Cube ground;
	private Matrix4d groundToWorldMatrix = Matrix4d.Identity;


	private Plane plane;
	private Matrix4d planeToWorldMatrix = Matrix4d.Identity;


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

		ground = new Cube(attrib_position, attrib_color);
		groundToWorldMatrix = Matrix4d.ScaleMatrix(5, 0.01, 5);

		plane = new Plane(attrib_position, attrib_color);
		planeToWorldMatrix = Matrix4d.TranslationMatrix(0, 2, 0);

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

		plane.close();
		ground.close();

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


	private int vertexArrayObject;
	private int vertexBufferObject;
	private int elementBufferObject;

	private void createVertexBuffers()
	{
		vertexArrayObject = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayObject);


		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexData.length);
		vertexBuffer.put(vertexData);
		vertexBuffer.flip();

		vertexBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferObject);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);

		int stride = 7 * 4;
		GL20.glEnableVertexAttribArray(attrib_position);
		GL20.glVertexAttribPointer(attrib_position, 3, GL11.GL_FLOAT, false, stride, 0);
		GL20.glEnableVertexAttribArray(attrib_color);
		GL20.glVertexAttribPointer(attrib_color, 4, GL11.GL_FLOAT, false, stride, 3 * 4);


		ByteBuffer elementBuffer = BufferUtils.createByteBuffer(indexData.length);
		elementBuffer.put(indexData);
		elementBuffer.flip();

		elementBufferObject = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBufferObject);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);

		exitOnGLError("createVertexBuffers");
	}

	private void destroyVertexBuffers()
	{
		GL15.glDeleteBuffers(elementBufferObject);
		elementBufferObject = 0;
		GL15.glDeleteBuffers(vertexBufferObject);
		vertexBufferObject = 0;

		GL30.glDeleteVertexArrays(vertexArrayObject);
		vertexArrayObject = 0;

		exitOnGLError("destroyVertexBuffers");
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

	private Matrix4d cameraToClipMatrix = Matrix4d.Identity;

	private void initializeShaderProgram()
	{
		// NOTE!: Update this matrix when window is resized, because width and height will be different then
		cameraToClipMatrix = ComputePerspectiveMatrix(60.0f, (float)height / (float)width, 0.5f, 45.0f);

		// Update uniform
		GL20.glUseProgram(programId);
		FloatBuffer tempMatrix4fBuffer = BufferUtils.createFloatBuffer(16);
		cameraToClipMatrix.store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_cameraToClipMatrix, false, tempMatrix4fBuffer);
		GL20.glUseProgram(0);

		worldToCameraMatrix = ComputeWorldToCameraMatrix(lookAt, camAngle, camTilt);
	}

	private static Matrix4d ComputePerspectiveMatrix(float fov, float aspectRatio, float zNear, float zFar)
	{
		float frustumScale = (float)(1.0 / Math.tan(fov * Math.PI / 360.0));
		return new Matrix4d(
				frustumScale * aspectRatio, 0, 0, 0,
				0, frustumScale, 0, 0,
				0, 0, (zFar + zNear) / (zNear - zFar), (2 * zFar * zNear) / (zNear - zFar),
				0, 0, -1, 0);
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

	private Matrix4d object1ToWorldMatrix = Matrix4d.Identity;
	private Matrix4d object2ToWorldMatrix = Matrix4d.Identity;

	private Vector3d lookAt = new Vector3d(0, 2, 0);
	private double camAngle = 0;
	private double camTilt = -lookAt.y;

	private double planeRotateX = 0;
	private double planeRotateY = 0;
	private double planeRotateZ = 0;

	private static final double TAU = Math.PI * 2;

	private static final double rotateStepBig = TAU / 180;
	private static final double rotateStepSmall = TAU / 1440;

	private void logic()
	{
		double prevCamAngle = camAngle;
		double prevCamTilt = camTilt;
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
		if ((camAngle != prevCamAngle) || (camTilt != prevCamTilt))
		{
			worldToCameraMatrix = ComputeWorldToCameraMatrix(lookAt, camAngle, camTilt);
		}

		double rotateStep = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? rotateStepSmall : rotateStepBig;

		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			planeRotateX -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			planeRotateX += rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
		{
			planeRotateY -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E))
		{
			planeRotateY +=rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			planeRotateZ -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			planeRotateZ += rotateStep;
		}

		planeRotateX = (planeRotateX + TAU) % TAU;
		planeRotateY = (planeRotateY + TAU) % TAU;
		planeRotateZ = (planeRotateZ + TAU) % TAU;


		double time = getTime();
		double theta = (time / 5.0) * TAU;
		double offsetZ = Math.sin(theta) * 0.75 + 0.25;
		object2ToWorldMatrix = Matrix4d.TranslationMatrix(0, 0, -offsetZ);
	}

	private static Matrix4d ComputeWorldToCameraMatrix(Vector3d lookAt, double angle, double tilt)
	{
		Vector3d cameraPos = ResolveCameraPosition(angle, tilt, 5);
		Vector3d up = new Vector3d(0, 1, 0);

		Vector3d lookDir = lookAt.sub(cameraPos).normalize();
		Vector3d upDir = up.normalize();

		Vector3d rightDir = Vector3d.cross(lookDir, upDir).normalize();
		Vector3d perpUpDir = rightDir.cross(lookDir);

		Matrix4d rotMat = new Matrix4d(
				rightDir.x, rightDir.y,  rightDir.z, 0,
				perpUpDir.x, perpUpDir.y, perpUpDir.z, 0,
				-lookDir.x, -lookDir.y, -lookDir.z, 0,
				0, 0, 0, 1);

		Matrix4d transMat = Matrix4d.TranslationMatrix(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		return rotMat.mul(transMat);
	}

	private static Vector3d ResolveCameraPosition(double phi, double theta, double radius)
	{
		double phiRad = ToRadians(phi);
		double thetaRad = ToRadians(theta + 90.0);

		double sinPhi = Math.sin(phiRad);
		double cosPhi = Math.cos(phiRad);
		double sinTheta = Math.sin(thetaRad);
		double cosTheta = Math.cos(thetaRad);

		Vector3d dirToCamera = new Vector3d((sinTheta * cosPhi), cosTheta, (sinTheta * sinPhi));

		return dirToCamera.scale(radius);
	}

	private Matrix4d worldToCameraMatrix = Matrix4d.Identity;

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL20.glUseProgram(programId);

		GL30.glBindVertexArray(vertexArrayObject);

		FloatBuffer tempMatrix4fBuffer = BufferUtils.createFloatBuffer(16);

		worldToCameraMatrix.mul(object1ToWorldMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0, 0);

		worldToCameraMatrix.mul(object2ToWorldMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		GL32.glDrawElementsBaseVertex(GL11.GL_TRIANGLES, indexData.length, GL11.GL_UNSIGNED_BYTE, 0, 18);

		GL30.glBindVertexArray(0);


		worldToCameraMatrix.mul(groundToWorldMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		ground.render();

		Matrix4d planeMatrixRotX = Matrix4d.RotateXMatrix(planeRotateX);
		Matrix4d planeMatrixRotY = Matrix4d.RotateYMatrix(planeRotateY);
		Matrix4d planeMatrixRotZ = Matrix4d.RotateZMatrix(planeRotateZ);
		Matrix4d planeMatrix = planeToWorldMatrix.mul(planeMatrixRotZ).mul(planeMatrixRotY).mul(planeMatrixRotX);

		worldToCameraMatrix.mul(planeMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		plane.render();

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
		return degrees * TAU / 360.0;
	}

	private static double ToDegrees(double radians)
	{
		return radians * 360.0 / TAU;
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
