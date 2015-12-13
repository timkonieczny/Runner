package com.timkonieczny.runner;

public class Matrix3 {

	public static float[] fromMatrix4(float[] matrix4){
		return new float[]{
				matrix4[0],	matrix4[1],	matrix4[2],
				matrix4[4],	matrix4[5],	matrix4[6],
				matrix4[8],	matrix4[9],	matrix4[10],
		};
	}

	public static float[] transpose(float[] matrix3, float[] out){

		out[0] = matrix3[0];
		out[1] = matrix3[3];
		out[2] = matrix3[6];

		out[3] = matrix3[1];
		out[4] = matrix3[4];
		out[5] = matrix3[7];

		out[6] = matrix3[2];
		out[7] = matrix3[5];
		out[8] = matrix3[8];

		return out;
	}
}
