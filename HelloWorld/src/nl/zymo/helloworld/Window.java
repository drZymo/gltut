package nl.zymo.helloworld;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
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

		setupQuad();
		setupShaders();
		setupTextures();

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
			ContextAttribs contextAtrributes = new ContextAttribs(3, 2);
			contextAtrributes.withForwardCompatible(true);
			contextAtrributes.withProfileCore(true);

			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setTitle(title);
			Display.create(pixelFormat, contextAtrributes);

			GL11.glViewport(0, 0, width, height);
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}

		// Setup an XNA like background color
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);

		// Map the internal OpenGL coordinate system to the entire screen
		GL11.glViewport(0, 0, width, height);

		this.exitOnGLError("setupOpenGL");
	}

	private void destroyOpenGL()
	{
		// Delete the texture
		GL11.glDeleteTextures(textureIds[0]);
		GL11.glDeleteTextures(textureIds[1]);

		// Delete the shaders
		GL20.glUseProgram(0);
		GL20.glDetachShader(programId, vertexShaderId);
		GL20.glDetachShader(programId, fragmentShaderId);

		GL20.glDeleteShader(vertexShaderId);
		GL20.glDeleteShader(fragmentShaderId);
		GL20.glDeleteProgram(programId);

		// Select the VAO
		GL30.glBindVertexArray(vertexArrayId);

		// Disable the VBO index from the VAO attributes list
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		// Delete the vertex VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(vboId);

		// Delete the index VBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(elementArrayId);

		// Delete the VAO
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vertexArrayId);

		this.exitOnGLError("destroyOpenGL");

		Display.destroy();
	}

	private int indicesCount;

	private int vertexArrayId;
	private int vboId;
	private int elementArrayId;

	private void setupQuad()
	{
		// We'll define our quad using 4 vertices of the custom 'TexturedVertex'
		// class
		TexturedVertex v0 = new TexturedVertex();
		v0.setXYZW(-0.75f, 0.75f, 0, 1.0f);
		v0.setST(0, 0);
		TexturedVertex v1 = new TexturedVertex();
		v1.setXYZW(-0.75f, -0.75f, 0, 1.0f);
		v1.setST(0, 1);
		TexturedVertex v2 = new TexturedVertex();
		v2.setXYZW(0.75f, -0.75f, 0, 1.0f);
		v2.setST(1, 1);
		TexturedVertex v3 = new TexturedVertex();
		v3.setXYZW(0.75f, 0.75f, 0, 1.0f);
		v3.setST(1, 0);

		TexturedVertex[] vertices = new TexturedVertex[] { v0, v1, v2, v3 };

		// Put each 'Vertex' in one FloatBuffer
		FloatBuffer verticesBuffer = BufferUtils.createFloatBuffer(vertices.length * TexturedVertex.elementCount);
		for (int i = 0; i < vertices.length; i++)
		{
			// Add position, color and texture floats to the buffer
			verticesBuffer.put(vertices[i].getElements());
		}
		verticesBuffer.flip();

		// OpenGL expects to draw vertices in counter clockwise order by default
		byte[] indices = { 0, 1, 2, 2, 3, 0 };
		indicesCount = indices.length;
		ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
		indicesBuffer.put(indices);
		indicesBuffer.flip();

		// Create a new Vertex Array Object in memory and select it (bind)
		vertexArrayId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vertexArrayId);

		// Create a new Vertex Buffer Object in memory and select it (bind)
		vboId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

		// Put the position coordinates in attribute list 0
		GL20.glVertexAttribPointer(0, TexturedVertex.positionElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride,
				TexturedVertex.positionByteOffset);
		// Put the color components in attribute list 1
		GL20.glVertexAttribPointer(1, TexturedVertex.colorElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride,
				TexturedVertex.colorByteOffset);
		// Put the texture coordinates in attribute list 2
		GL20.glVertexAttribPointer(2, TexturedVertex.textureElementCount, GL11.GL_FLOAT, false, TexturedVertex.stride,
				TexturedVertex.textureByteOffset);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Deselect (bind to 0) the VAO
		GL30.glBindVertexArray(0);

		// Create a new VBO for the indices and select it (bind) - INDICES
		elementArrayId = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementArrayId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		this.exitOnGLError("setupQuad");
	}

	private int vertexShaderId;
	private int fragmentShaderId;
	private int programId;

	private int uniform_fade_factor;
	private int[] uniform_textures = new int[2];

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

		// Position information will be attribute 0
		GL20.glBindAttribLocation(programId, 0, "in_Position");
		// Color information will be attribute 1
		GL20.glBindAttribLocation(programId, 1, "in_Color");
		// Texture information will be attribute 2
		GL20.glBindAttribLocation(programId, 2, "in_TextureCoord");

		uniform_fade_factor = GL20.glGetUniformLocation(programId, "fade_factor");
		uniform_textures[0] = GL20.glGetUniformLocation(programId, "textures[0]");
		uniform_textures[1] = GL20.glGetUniformLocation(programId, "textures[1]");

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

	private int[] textureIds = new int[2];

	private void setupTextures()
	{
		textureIds[0] = this.loadPNGTexture("textures/gl2-hello-1.png", GL13.GL_TEXTURE0);
		textureIds[1] = this.loadPNGTexture("textures/gl2-hello-2.png", GL13.GL_TEXTURE1);

		this.exitOnGLError("setupTexture");
	}

	private static final int BYTES_PER_PIXEL = 4;// 3 for RGB, 4 for RGBA

	private int loadPNGTexture(String ref, int textureUnit)
	{
		BufferedImage image = loadImage(ref);

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
				buffer.put((byte) (pixel & 0xFF)); // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component.
															// Only for RGBA
			}
		}

		buffer.flip(); // FOR THE LOVE OF GOD DO NOT FORGET THIS

		// Create a new texture object in memory and bind it
		int texId = GL11.glGenTextures();
		GL13.glActiveTexture(textureUnit);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		// All RGB bytes are aligned to each other and each component is 1 byte
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

		// Upload the texture data and generate mip maps (for scaling)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, buffer);
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

		// Setup the ST coordinate system
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		// Setup what to do when the texture has to be scaled
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

		this.exitOnGLError("loadPNGTexture");

		return texId;
	}

	public BufferedImage loadImage(String ref)
	{
		// otherwise, go away and grab the sprite from the resource
		// loader
		BufferedImage sourceImage = null;

		try
		{
			// The ClassLoader.getResource() ensures we get the sprite
			// from the appropriate place, this helps with deploying the game
			// with things like webstart. You could equally do a file look
			// up here.
			URL url = this.getClass().getClassLoader().getResource(ref);

			if (url == null)
			{
				System.err.println("Can't find ref: " + ref);
				System.exit(0);
			}

			// use ImageIO to read the image in
			sourceImage = ImageIO.read(url);
		} catch (IOException e)
		{
			System.err.println("Failed to load: " + ref);
			System.exit(0);
		}
		return sourceImage;
	}

	private void loopCycle()
	{
		// Logic
		while (Keyboard.next())
		{
			// Only listen to events where the key was pressed (down event)
			if (!Keyboard.getEventKeyState())
				continue;

			// Switch textures depending on the key released
			switch (Keyboard.getEventKey())
			{
			case Keyboard.KEY_1:
				fade_factor = 0;
				break;
			case Keyboard.KEY_2:
				fade_factor = 0.5f;
				break;
			case Keyboard.KEY_3:
				fade_factor = 1;
				break;
			}
		}

		double milliseconds = getTime();
		fade_factor = (float) Math.sin(milliseconds * 0.001) * 0.5f + 0.5f;

		render();
	}

	private float fade_factor = 0;

	private void render()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(programId);

		GL20.glUniform1f(uniform_fade_factor, fade_factor);

		// Bind the texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[0]);
		GL20.glUniform1i(uniform_textures[0], 0);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIds[1]);
		GL20.glUniform1i(uniform_textures[1], 1);

		// Bind to the VAO that has all the information about the vertices
		GL30.glBindVertexArray(vertexArrayId);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		// Bind to the index VBO that has all the information about the order of
		// the vertices
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, elementArrayId);

		// Draw the vertices
		GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

		// Put everything back to default (deselect)
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);

		GL20.glUseProgram(0);

		this.exitOnGLError("loopCycle");
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
