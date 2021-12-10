package captech.muslimutility.calculator.quibla;

public class MyMath {

	public static double RTD = 180 / Math.PI;
	public static double DTR = Math.PI / 180;

	// degree Sin
	public static double dSin(double DegreeAngle) {
		return Math.sin(DegreeAngle * MyMath.DTR);
	}

	// degree Cos
	public static double dCos(double DegreeAngle) {
		return Math.cos(DegreeAngle * MyMath.DTR);
	}

	// degree Tan
	public static double dTan(double DegreeAngle) {
		return Math.tan(DegreeAngle * MyMath.DTR);
	}

	// degree arctan
	public static double dATan(double x) {
		//
		double result = 0;
		int sign = -1;
		if (Math.abs(x) < 1) {
			result = x;
			int times = 3;
			while (times < 300) {
				result = result + sign * power(x, times) / times;
				sign = sign * -1;
				times += 2;
			}
		}

		else {
			int times = 1;
			while (times < 300) {
				result += sign * 1 / (power(x, times) * times);
				sign = sign * -1;
				times += 2;
			}
			if (x >= 1)
				result = result + Math.PI / 2;
			else
				result = result - Math.PI / 2;
		}

		return result * RTD;
	}

	// degree arccot
	public static double dACot(double x) {
		// return MyMath.RTD * Math.atan(1 / x);
		return 90 - dATan(x);
	}

	public static double power(double base, int pow) {
		double result = 1;
		if (pow == 0)
			result = 1;
		else if (pow > 0) {
			while (pow > 0) {
				result *= base;
				pow--;
			}
		} else if (pow < 0) {
			while (pow < 0) {
				result /= base;
				pow++;
			}
		}
		return result;
	}

}
