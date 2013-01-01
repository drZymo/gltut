package nl.zymo.helloworld;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

public class TextureLoader
{
	private static final int BYTES_PER_PIXEL = 4;// 3 for RGB, 4 for RGBA

	public static int loadTexture(BufferedImage image)
	{
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

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:

		int textureID = GL11.glGenTextures(); // Generate texture ID
		exitOnGLError("Error in make_texture 1"); // BREAKS HERE !!!!!
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); // Bind texture ID
		exitOnGLError("Error in make_texture 2"); // BREAKS HERE !!!!!

		// Setup wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		exitOnGLError("Error in make_texture 3"); // BREAKS HERE !!!!!
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		exitOnGLError("Error in make_texture 4"); // BREAKS HERE !!!!!

		// Setup texture scaling filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		exitOnGLError("Error in make_texture 5"); // BREAKS HERE !!!!!
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		exitOnGLError("Error in make_texture 6"); // BREAKS HERE !!!!!

		// Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE, buffer);
		exitOnGLError("Error in make_texture 7"); // BREAKS HERE !!!!!

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		// Return the texture ID so we can bind it later again
		return textureID;
	}

	public static void exitOnGLError(String errorMessage)
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
}