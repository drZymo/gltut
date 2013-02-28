package nl.zymo.gltut;

public class Vector3
{
	public Vector3(double x, double y, double z)
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

	public Vector3 normalize()
	{
		double length = getLength();
		return new Vector3(x / length, y / length, z / length);
	}

	public static Vector3 add(Vector3 left, double right)
	{
		return new Vector3(left.x + right, left.y + right, left.z + right);
	}

	public Vector3 add(double right)
	{
		return add(this, right);
	}

	public static Vector3 add(Vector3 left, Vector3 right)
	{
		return new Vector3(left.x + right.x, left.y + right.y, left.z + right.z);
	}

	public Vector3 add(Vector3 right)
	{
		return add(this, right);
	}

	public static Vector3 sub(Vector3 left, double right)
	{
		return new Vector3(left.x - right, left.y - right, left.z - right);
	}

	public Vector3 sub(double right)
	{
		return sub(this, right);
	}

	public static Vector3 sub(Vector3 left, Vector3 right)
	{
		return new Vector3(left.x - right.x, left.y - right.y, left.z - right.z);
	}

	public Vector3 sub(Vector3 right)
	{
		return sub(this, right);
	}

	public static Vector3 scale(Vector3 vector, double scale)
	{
		return new Vector3(vector.x * scale, vector.y * scale, vector.z * scale);
	}

	public Vector3 scale(double scale)
	{
		return scale(this, scale);
	}

	public static double dot(Vector3 left, Vector3 right)
	{
		return left.x * right.x + left.y * right.y + left.z * right.z;
	}

	public double dot(Vector3 right)
	{
		return dot(this, right);
	}

	public static double angle(Vector3 left, Vector3 right)
	{
		double dot = dot(left, right);
		return Math.acos(dot / (left.getLength() * right.getLength()));
	}

	public double angle(Vector3 right)
	{
		return angle(this, right);
	}

	public static Vector3 cross(Vector3 left, Vector3 right)
	{
		return new Vector3(
				(left.y * right.z) - (left.z * right.y),
				(left.z * right.x) - (left.x * right.z),
				(left.x * right.y) - (left.y * right.x));
	}

	public Vector3 cross(Vector3 right)
	{
		return cross(this, right);
	}

	public String toString()
	{
		return "{ " + x + ", " + y + ", " + z + " }";
	}
}
