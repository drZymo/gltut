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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Window
{
	private int width;
	private int height;
	private String title;

	private Cube ground;
	private Matrix4 groundToWorldMatrix = Matrix4.Identity;


	private Ship ship;
	private Matrix4 shipToWorldMatrix = Matrix4.Identity;
	private Quaternion shipOrientation = Quaternion.Identity;

	private Cylinder cylinder;
	private Matrix4 cylinderToWorldMatrix = Matrix4.Identity;


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

		ground = new Cube(attrib_position, attrib_color);
		groundToWorldMatrix = Matrix4.ScaleMatrix(5, 0.01, 5);

		ship = new Ship(attrib_position, attrib_color);
		shipToWorldMatrix = Matrix4.TranslationMatrix(0, 3, 0);

		cylinder = new Cylinder(attrib_position, attrib_color);
		cylinderToWorldMatrix = Matrix4.TranslationMatrix(0, 1, 0);

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

		cylinder.close();
		ship.close();
		ground.close();

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

	private Matrix4 cameraToClipMatrix = Matrix4.Identity;

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

	private static Matrix4 ComputePerspectiveMatrix(float fov, float aspectRatio, float zNear, float zFar)
	{
		float frustumScale = (float)(1.0 / Math.tan(fov * Math.PI / 360.0));
		return new Matrix4(
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

	private Vector3 lookAt = new Vector3(0, 3, 0);
	private double camAngle = 90;
	private double camTilt = -30;

	private static final double TAU = Math.PI * 2;

	private static final double rotateStepBig = TAU / 180;
	private static final double rotateStepSmall = TAU / 1440;

	private static final Vector3 axisX = new Vector3(1, 0, 0);
	private static final Vector3 axisY = new Vector3(0, 1, 0);
	private static final Vector3 axisZ = new Vector3(0, 0, 1);

	private void logic()
	{
		while (Keyboard.next())
		{
			int key = Keyboard.getEventKey();
			boolean state = Keyboard.getEventKeyState();

			if (key == Keyboard.KEY_SPACE && state)
			{
				cycleOrientationMode();
			}
		}

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

		double shipRotateX = 0;
		double shipRotateY = 0;
		double shipRotateZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
			shipRotateX -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			shipRotateX += rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
		{
			shipRotateY -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E))
		{
			shipRotateY +=rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			shipRotateZ -= rotateStep;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			shipRotateZ += rotateStep;
		}

		if (shipRotateX != 0)
		{
			OffsetOrientation(axisX, shipRotateX);
		}
		if (shipRotateY != 0)
		{
			OffsetOrientation(axisY, shipRotateY);
		}
		if (shipRotateZ != 0)
		{
			OffsetOrientation(axisZ, shipRotateZ);
		}
		shipOrientation = shipOrientation.normalize();
	}

	private static final int ORIENTATION_MODEL_RELATIVE = 0;
	private static final int ORIENTATION_WORLD_RELATIVE = 1;
	private static final int ORIENTATION_CAMERA_RELATIVE = 2;

	private int orientationMode = ORIENTATION_MODEL_RELATIVE;

	private void cycleOrientationMode()
	{
		orientationMode = (orientationMode + 1) % 3;
	}

	private void OffsetOrientation(Vector3 axis, double angle)
	{
		Quaternion offset = new Quaternion(axis, angle);
		if (orientationMode == ORIENTATION_MODEL_RELATIVE)
		{
			shipOrientation = shipOrientation.mul(offset);
		}
		else if (orientationMode == ORIENTATION_WORLD_RELATIVE)
		{
			shipOrientation = offset.mul(shipOrientation);
		}
		else if (orientationMode == ORIENTATION_CAMERA_RELATIVE)
		{
			Matrix4 camMat = ComputeWorldToCameraMatrix(lookAt, camAngle, camTilt);
			Quaternion camQuat = camMat.toQuaternion();
			Quaternion invCamQuat = camQuat.conjugate();
			Quaternion worldQuat = invCamQuat.mul(offset).mul(camQuat);
			shipOrientation = worldQuat.mul(shipOrientation);
		}
	}

	private static final double CAMERA_DISTANCE = 7;

	private static Matrix4 ComputeWorldToCameraMatrix(Vector3 lookAt, double angle, double tilt)
	{
		Vector3 cameraPos = ResolveCameraPosition(angle, tilt, CAMERA_DISTANCE);
		Vector3 up = new Vector3(0, 1, 0);

		Vector3 lookDir = lookAt.sub(cameraPos).normalize();
		Vector3 upDir = up.normalize();

		Vector3 rightDir = Vector3.cross(lookDir, upDir).normalize();
		Vector3 perpUpDir = rightDir.cross(lookDir);

		Matrix4 rotMat = new Matrix4(
				rightDir.x, rightDir.y,  rightDir.z, 0,
				perpUpDir.x, perpUpDir.y, perpUpDir.z, 0,
				-lookDir.x, -lookDir.y, -lookDir.z, 0,
				0, 0, 0, 1);

		Matrix4 transMat = Matrix4.TranslationMatrix(-cameraPos.x, -cameraPos.y, -cameraPos.z);

		return rotMat.mul(transMat);
	}

	private static Vector3 ResolveCameraPosition(double phi, double theta, double radius)
	{
		double phiRad = ToRadians(phi);
		double thetaRad = ToRadians(theta + 90.0);

		double sinPhi = Math.sin(phiRad);
		double cosPhi = Math.cos(phiRad);
		double sinTheta = Math.sin(thetaRad);
		double cosTheta = Math.cos(thetaRad);

		Vector3 dirToCamera = new Vector3((sinTheta * cosPhi), cosTheta, (sinTheta * sinPhi));

		return dirToCamera.scale(radius);
	}

	private Matrix4 worldToCameraMatrix = Matrix4.Identity;

	private void render()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0f);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GL20.glUseProgram(programId);

		FloatBuffer tempMatrix4fBuffer = BufferUtils.createFloatBuffer(16);

		worldToCameraMatrix.mul(groundToWorldMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		ground.render();

		Matrix4 shipMatrix = shipToWorldMatrix.mul(shipOrientation.toMatrix());
		worldToCameraMatrix.mul(shipMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		ship.render();

		worldToCameraMatrix.mul(cylinderToWorldMatrix).store(tempMatrix4fBuffer);
		GL20.glUniformMatrix4(uniform_modelToCameraMatrix, false, tempMatrix4fBuffer);
		cylinder.render();

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
