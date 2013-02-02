package nl.zymo.gltut;

public class Vector3d
{
	public Vector3d(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final double x;
	public final double y;
	public final double z;

	public double getLength()
	{
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3d norm()
	{
		double length = getLength();
		return new Vector3d(x / length, y / length, z / length);
	}

	public static Vector3d add(Vector3d left, double right)
	{
		return new Vector3d(left.x + right, left.y + right, left.z + right);
	}

	public Vector3d add(double right)
	{
		return add(this, right);
	}

	public static Vector3d add(Vector3d left, Vector3d right)
	{
		return new Vector3d(left.x + right.x, left.y + right.y, left.z + right.z);
	}

	public Vector3d add(Vector3d right)
	{
		return add(this, right);
	}

	public static Vector3d sub(Vector3d left, double right)
	{
		return new Vector3d(left.x - right, left.y - right, left.z - right);
	}

	public Vector3d sub(double right)
	{
		return sub(this, right);
	}

	public static Vector3d sub(Vector3d left, Vector3d right)
	{
		return new Vector3d(left.x - right.x, left.y - right.y, left.z - right.z);
	}

	public Vector3d sub(Vector3d right)
	{
		return sub(this, right);
	}

	public static Vector3d scale(Vector3d vector, double scale)
	{
		return new Vector3d(vector.x * scale, vector.y * scale, vector.z * scale);
	}

	public Vector3d scale(double scale)
	{
		return scale(this, scale);
	}

	public static double dot(Vector3d left, Vector3d right)
	{
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	public double dot(Vector3d right)
	{
		return dot(this, right);
	}

	public static double angle(Vector3d left, Vector3d right)
	{
		double dot = dot(left, right);
		return Math.acos(dot / (left.getLength() * right.getLength()));
	}

	public double angle(Vector3d right)
	{
		return angle(this, right);
	}

	public static Vector3d cross(Vector3d left, Vector3d right)
	{
		return new Vector3d(
				(left.y * right.z) - (left.z * right.y),
				(left.z * right.x) - (left.x * right.z),
				(left.x * right.y) - (left.y * right.x));
	}

	public Vector3d cross(Vector3d right)
	{
		return cross(this, right);
	}

	public String toString()
	{
		return "{ " + x + ", " + y + ", " + z + " }";
	}
}
