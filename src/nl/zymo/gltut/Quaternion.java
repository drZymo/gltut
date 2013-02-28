package nl.zymo.gltut;

public class Quaternion
{
	public Quaternion(double x, double y, double z, double w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(Vector3 axis, double angle)
	{
		double angleSin = Math.sin(angle / 2);
		double angleCos = Math.cos(angle / 2);
		x = axis.x * angleSin;
		y = axis.y * angleSin;
		z = axis.z * angleSin;
		w = angleCos;
	}

	public final double x;
	public final double y;
	public final double z;
	public final double w;

	public static final Quaternion Identity = new Quaternion(0, 0, 0, 1);

	public static Quaternion mul(Quaternion left, Quaternion right)
	{
		return new Quaternion(
				left.w * right.x + left.x * right.w + left.y * right.z - left.z * right.y,
				left.w * right.y + left.y * right.w + left.z * right.x - left.x * right.z,
				left.w * right.z + left.z * right.w + left.x * right.y - left.y * right.x,
				left.w * right.w - left.x * right.x - left.y * right.y - left.z * right.z);
	}

	public Quaternion mul(Quaternion right)
	{
		return mul(this, right);
	}

	public Quaternion normalize()
	{
		double magnitude = Math.sqrt(x * x + y * y + z * z + w * w);
		return new Quaternion(x / magnitude, y / magnitude, z / magnitude, w / magnitude);
	}

	public Matrix4 toMatrix()
	{
		return new Matrix4(
				1 - 2 * y * y - 2 * z * z, 2 * x * y - 2 * w * z, 2 * x * z + 2 * w * y, 0,
				2 * x * y + 2 * w * z, 1 - 2 * x * x - 2 * z * z, 2 * y * z - 2 * w * x, 0,
				2 * x * z - 2 * w * y, 2 * y * z + 2 * w * x, 1 - 2 * x * x - 2 * y * y, 0,
				0, 0, 0, 1);
	}
}
