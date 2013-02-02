package nl.zymo.gltut;

import java.nio.FloatBuffer;

public class Matrix4d
{
	public Matrix4d(
			double m11, double m12, double m13, double m14,
			double m21, double m22, double m23, double m24,
			double m31, double m32, double m33, double m34,
			double m41, double m42, double m43, double m44)
	{
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m14 = m14;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m24 = m24;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
		this.m34 = m34;
		this.m41 = m41;
		this.m42 = m42;
		this.m43 = m43;
		this.m44 = m44;
	}

	public final double m11;
	public final double m12;
	public final double m13;
	public final double m14;
	public final double m21;
	public final double m22;
	public final double m23;
	public final double m24;
	public final double m31;
	public final double m32;
	public final double m33;
	public final double m34;
	public final double m41;
	public final double m42;
	public final double m43;
	public final double m44;

	public static final Matrix4d Zero = new Matrix4d(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

	public static final Matrix4d Identity = new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

	public static Matrix4d TranslationMatrix(Vector3d vec)
	{
		return TranslationMatrix(vec.x, vec.y, vec.z);
	}

	public static Matrix4d TranslationMatrix(double x, double y, double z)
	{
		return new Matrix4d(
				1, 0, 0, x,
				0, 1, 0, y,
				0, 0, 1, z,
				0, 0, 0, 1);
	}

	public static Matrix4d ScaleMatrix(Vector3d vec)
	{
		return ScaleMatrix(vec.x, vec.y, vec.z);
	}

	public static Matrix4d ScaleMatrix(double x, double y, double z)
	{
		return new Matrix4d(
				x, 0, 0, 0,
				0, y, 0, 0,
				0, 0, z, 0,
				0, 0, 0, 1);
	}

	public static Matrix4d RotateXMatrix(double rad)
	{
		return new Matrix4d(
				1, 0, 0, 0,
				0, Math.cos(rad), -Math.sin(rad), 0,
				0, Math.sin(rad), Math.cos(rad), 0,
				0, 0, 0, 1);
	}

	public static Matrix4d RotateYMatrix(double rad)
	{
		return new Matrix4d(
				Math.cos(rad), 0, Math.sin(rad), 0,
				0, 1, 0, 0,
				-Math.sin(rad), 0, Math.cos(rad), 0,
				0, 0, 0, 1);
	}

	public static Matrix4d RotateZMatrix(double rad)
	{
		return new Matrix4d(
				Math.cos(rad), -Math.sin(rad), 0, 0,
				Math.sin(rad), Math.cos(rad), 0, 0,
				0, 0, 1, 0,
				0, 0, 0, 1);
	}

	public static Matrix4d add(Matrix4d left, double right)
	{
		return new Matrix4d(
				left.m11 + right, left.m12 + right, left.m13 + right, left.m14 + right,
				left.m21 + right, left.m22 + right, left.m23 + right, left.m24 + right,
				left.m31 + right, left.m32 + right, left.m33 + right, left.m34 + right,
				left.m41 + right, left.m42 + right, left.m43 + right, left.m44 + right);
	}

	public static Matrix4d add(Matrix4d left, Matrix4d right)
	{
		return new Matrix4d(
				left.m11 + right.m11, left.m12 + right.m12, left.m13 + right.m13, left.m14 + right.m14,
				left.m21 + right.m21, left.m22 + right.m22, left.m23 + right.m23, left.m24 + right.m24,
				left.m31 + right.m31, left.m32 + right.m32, left.m33 + right.m33, left.m34 + right.m34,
				left.m41 + right.m41, left.m42 + right.m42, left.m43 + right.m43, left.m44 + right.m44);
	}

	public static Matrix4d sub(Matrix4d left, double right)
	{
		return new Matrix4d(
				left.m11 - right, left.m12 - right, left.m13 - right, left.m14 - right,
				left.m21 - right, left.m22 - right, left.m23 - right, left.m24 - right,
				left.m31 - right, left.m32 - right, left.m33 - right, left.m34 - right,
				left.m41 - right, left.m42 - right, left.m43 - right, left.m44 - right);
	}

	public static Matrix4d sub(Matrix4d left, Matrix4d right)
	{
		return new Matrix4d(
				left.m11 - right.m11, left.m12 - right.m12, left.m13 - right.m13, left.m14 - right.m14,
				left.m21 - right.m21, left.m22 - right.m22, left.m23 - right.m23, left.m24 - right.m24,
				left.m31 - right.m31, left.m32 - right.m32, left.m33 - right.m33, left.m34 - right.m34,
				left.m41 - right.m41, left.m42 - right.m42, left.m43 - right.m43, left.m44 - right.m44);
	}

	public static Matrix4d mul(Matrix4d left, double right)
	{
		return new Matrix4d(
				left.m11 * right, left.m12 * right, left.m13 * right, left.m14 * right,
				left.m21 * right, left.m22 * right, left.m23 * right, left.m24 * right,
				left.m31 * right, left.m32 * right, left.m33 * right, left.m34 * right,
				left.m41 * right, left.m42 * right, left.m43 * right, left.m44 * right);
	}

	public static Matrix4d mul(Matrix4d left, Vector4d right)
	{
		return new Matrix4d(
				left.m11 * right.x, left.m12 * right.y, left.m13 * right.z, left.m14 * right.w,
				left.m21 * right.x, left.m22 * right.y, left.m23 * right.z, left.m24 * right.w,
				left.m31 * right.x, left.m32 * right.y, left.m33 * right.z, left.m34 * right.w,
				left.m41 * right.x, left.m42 * right.y, left.m43 * right.z, left.m44 * right.w);
	}

	public static Matrix4d mul(Matrix4d left, Matrix4d right)
	{
		return new Matrix4d(
				left.m11 * right.m11 + left.m12 * right.m21 + left.m13 * right.m31 + left.m14 * right.m41,
				left.m11 * right.m12 + left.m12 * right.m22 + left.m13 * right.m32 + left.m14 * right.m42,
				left.m11 * right.m13 + left.m12 * right.m23 + left.m13 * right.m33 + left.m14 * right.m43,
				left.m11 * right.m14 + left.m12 * right.m24 + left.m13 * right.m34 + left.m14 * right.m44,

				left.m21 * right.m11 + left.m22 * right.m21 + left.m23 * right.m31 + left.m24 * right.m41,
				left.m21 * right.m12 + left.m22 * right.m22 + left.m23 * right.m32 + left.m24 * right.m42,
				left.m21 * right.m13 + left.m22 * right.m23 + left.m23 * right.m33 + left.m24 * right.m43,
				left.m21 * right.m14 + left.m22 * right.m24 + left.m23 * right.m34 + left.m24 * right.m44,

				left.m31 * right.m11 + left.m32 * right.m21 + left.m33 * right.m31 + left.m34 * right.m41,
				left.m31 * right.m12 + left.m32 * right.m22 + left.m33 * right.m32 + left.m34 * right.m42,
				left.m31 * right.m13 + left.m32 * right.m23 + left.m33 * right.m33 + left.m34 * right.m43,
				left.m31 * right.m14 + left.m32 * right.m24 + left.m33 * right.m34 + left.m34 * right.m44,

				left.m41 * right.m11 + left.m42 * right.m21 + left.m43 * right.m31 + left.m44 * right.m41,
				left.m41 * right.m12 + left.m42 * right.m22 + left.m43 * right.m32 + left.m44 * right.m42,
				left.m41 * right.m13 + left.m42 * right.m23 + left.m43 * right.m33 + left.m44 * right.m43,
				left.m41 * right.m14 + left.m42 * right.m24 + left.m43 * right.m34 + left.m44 * right.m44);
	}

	public static Matrix4d transpose(Matrix4d matrix)
	{
		return new Matrix4d(
				matrix.m11, matrix.m21, matrix.m31, matrix.m41,
				matrix.m12, matrix.m22, matrix.m32, matrix.m42,
				matrix.m13, matrix.m23, matrix.m33, matrix.m43,
				matrix.m14, matrix.m24, matrix.m34, matrix.m44);
	}

	public Matrix4d add(double right)
	{
		return add(this, right);
	}

	public Matrix4d add(Matrix4d right)
	{
		return add(this, right);
	}

	public Matrix4d sub(double right)
	{
		return sub(this, right);
	}

	public Matrix4d sub(Matrix4d right)
	{
		return sub(this, right);
	}

	public Matrix4d mul(double right)
	{
		return mul(this, right);
	}

	public Matrix4d mul(Vector4d right)
	{
		return mul(this, right);
	}

	public Matrix4d mul(Matrix4d right)
	{
		return mul(this, right);
	}

	public Matrix4d transpose()
	{
		return transpose(this);
	}

	public void store(FloatBuffer buffer)
	{
		buffer.put(0, (float)m11);
		buffer.put(1, (float)m12);
		buffer.put(2, (float)m13);
		buffer.put(3, (float)m14);
		buffer.put(4, (float)m21);
		buffer.put(5, (float)m22);
		buffer.put(6, (float)m23);
		buffer.put(7, (float)m24);
		buffer.put(8, (float)m31);
		buffer.put(9, (float)m32);
		buffer.put(10, (float)m33);
		buffer.put(11, (float)m34);
		buffer.put(12, (float)m41);
		buffer.put(13, (float)m42);
		buffer.put(14, (float)m43);
		buffer.put(15, (float)m44);
	}
}
