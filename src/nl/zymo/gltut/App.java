package nl.zymo.gltut;

public class App
{
	private Window window;

	public App()
	{
		window = new Window(1024, 786, "Hello");
	}

	public void Run()
	{
		window.Show();
	}

	public static void main(String[] args)
	{
		App app = new App();
		app.Run();
	}
}
