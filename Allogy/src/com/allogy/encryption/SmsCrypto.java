/*
 * Copyright (c) 2013 Allogy Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.allogy.encryption;

public class SmsCrypto {

	private static int[] num35s = new int[6];

	// note the integer must be a positive number between 1 and 35

	private static char IntToChar(int num) {
		if (num < 0 || num > 32) {
			throw new IllegalArgumentException("got: " + num);
		}
		if (num <= 9) {
			return (char) ('0' + num);
		}
		char retval = (char) ('a' + num - 10);
		if (retval == 'l')
			return 'L';
		return retval;
	}

	private static int CharToInt(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		if (c >= 'a' && c <= 'z')
			return c - 'a' + 10;
		if (c == 'L')
			return 'L' - 'A' + 10;
		throw new IllegalArgumentException("got: " + c);
	}

	private static int map(int accum, int value, int inputPosition,
			int outputPosition) {
		return accum | ((value >> inputPosition) & 0x1) << outputPosition;
	}

	// note: x and y must be positive
	// x can be up to a 15 bit integer

	public static String encode(int x, long salt) {

		// NB: a kludge to help out the weak 32-bit PHP server installation...
		String salt_s = Long.toString(salt);
		int salt_l = salt_s.length();
		if (salt_l > 9) {
			salt = Long.parseLong(salt_s.substring(salt_l - 9));
		}

		if ((x & 0x8000) != 0) {
			// using the high bit might work, but still...
			throw new IllegalArgumentException("input too large: " + x);
		}

		// fold the salt in half (it may be a big number), make sure the first
		// bit is zero.
		int y = (int) (((salt >> 16) & 0xFFFF) ^ (salt & 0xFFFF)) & 0x7FFF;

		// System.err.println("y="+y);
		// constant initialization number

		int initNum = 23145;

		int xor1 = x ^ initNum;
		// System.err.println("xor1=0x"+Integer.toHexString(xor1));

		// 14 check bits (30bits(final)-16bit(input)
		int checkBits = xor1 & 0x3FFF;
		// System.err.println("checkBits=0x"+Integer.toHexString(checkBits));

		int y2 = 0;
		y2 = map(y2, xor1, 4, 0);
		y2 = map(y2, xor1, 9, 1);
		y2 = map(y2, xor1, 5, 2);
		y2 = map(y2, xor1, 3, 3);
		y2 = map(y2, xor1, 14, 4);
		y2 = map(y2, xor1, 6, 5);
		y2 = map(y2, xor1, 2, 6);
		y2 = map(y2, xor1, 13, 7);
		y2 = map(y2, xor1, 7, 8);
		y2 = map(y2, xor1, 12, 9);
		y2 = map(y2, xor1, 1, 10);
		y2 = map(y2, xor1, 11, 11);
		y2 = map(y2, xor1, 8, 12);
		y2 = map(y2, xor1, 10, 13);
		y2 = map(y2, xor1, 0, 14);
		y2 = map(y2, xor1, 15, 15);

		// System.err.println("y2=0x"+Integer.toHexString(y2));

		int z1 = (y2 + y);
		// System.err.println("Z1=0x"+Integer.toHexString(z1));

		int z = 0;

		z = map(z, z1, 0, 2);
		z = map(z, z1, 1, 11);
		z = map(z, z1, 2, 19);
		z = map(z, z1, 3, 26);
		z = map(z, z1, 4, 29);
		z = map(z, z1, 5, 6);
		z = map(z, z1, 6, 15);
		z = map(z, z1, 7, 23);
		z = map(z, z1, 8, 8);
		z = map(z, z1, 9, 3);
		z = map(z, z1, 10, 0);
		z = map(z, z1, 11, 13);
		z = map(z, z1, 12, 17);
		z = map(z, z1, 13, 21);
		z = map(z, z1, 14, 27);
		z = map(z, z1, 15, 4);

		z = map(z, checkBits, 0, 28);
		z = map(z, checkBits, 1, 1);
		z = map(z, checkBits, 2, 9);
		z = map(z, checkBits, 3, 16);
		z = map(z, checkBits, 4, 20);
		z = map(z, checkBits, 5, 18);
		z = map(z, checkBits, 6, 22);
		z = map(z, checkBits, 7, 14);
		z = map(z, checkBits, 8, 12);
		z = map(z, checkBits, 9, 10);
		z = map(z, checkBits, 10, 24);
		z = map(z, checkBits, 11, 7);
		z = map(z, checkBits, 12, 5);
		z = map(z, checkBits, 13, 25);

		// System.err.println("Z=0x"+Integer.toHexString((int)z));

		StringBuilder sb = new StringBuilder();

		// Convert the final integer 'z' to a six-character code-string
		for (int i = 0; i < 6; i++) {
			// 0-31 is five bits, as is 0x1F
			int lessThan32 = z & 0x1F;
			char c = IntToChar(lessThan32);
			// System.err.println("_z="+lessThan32+"\t-> "+c);
			sb.insert(0, c);

			// shift-out five bits and continue converting.
			z = z >> 5;
		}

		return sb.toString();
	}

	// note: z is a 5 character long string and y must be a positive integer

	public static int decode(String code, long salt)
			throws InvalidCodeException {

		// NB: a kludge to help out the weak 32-bit PHP server installation...
		{
			String salt_s = Long.toString(salt);
			int salt_l = salt_s.length();
			if (salt_l > 9) {
				salt = Long.parseLong(salt_s.substring(salt_l - 9));
			}
		}

		// fold the salt in half (it may be a big number), make sure the first
		// bit is zero.
		int y = (int) (((salt >> 16) & 0xFFFF) ^ (salt & 0xFFFF)) & 0x7FFF;

		// Convert the code-string into the integer 'z'
		int z = 0;

		for (int i = 0; i < 6; i++) {
			char c = code.charAt(i);
			int lessThan32 = CharToInt(c);
			// System.err.println("+z="+lessThan32+"\t<-- "+c);
			z = z << 5 | lessThan32;
		}

		// System.err.println("Z=0x"+Integer.toHexString((int)z));

		int checkBits = 0;
		int z1 = 0;

		z1 = map(z1, z, 2, 0);
		z1 = map(z1, z, 11, 1);
		z1 = map(z1, z, 19, 2);
		z1 = map(z1, z, 26, 3);
		z1 = map(z1, z, 29, 4);
		z1 = map(z1, z, 6, 5);
		z1 = map(z1, z, 15, 6);
		z1 = map(z1, z, 23, 7);
		z1 = map(z1, z, 8, 8);
		z1 = map(z1, z, 3, 9);
		z1 = map(z1, z, 0, 10);
		z1 = map(z1, z, 13, 11);
		z1 = map(z1, z, 17, 12);
		z1 = map(z1, z, 21, 13);
		z1 = map(z1, z, 27, 14);
		z1 = map(z1, z, 4, 15);

		checkBits = map(checkBits, z, 28, 0);
		checkBits = map(checkBits, z, 1, 1);
		checkBits = map(checkBits, z, 9, 2);
		checkBits = map(checkBits, z, 16, 3);
		checkBits = map(checkBits, z, 20, 4);
		checkBits = map(checkBits, z, 18, 5);
		checkBits = map(checkBits, z, 22, 6);
		checkBits = map(checkBits, z, 14, 7);
		checkBits = map(checkBits, z, 12, 8);
		checkBits = map(checkBits, z, 10, 9);
		checkBits = map(checkBits, z, 24, 10);
		checkBits = map(checkBits, z, 7, 11);
		checkBits = map(checkBits, z, 5, 12);
		checkBits = map(checkBits, z, 25, 13);

		// System.err.println("Z1=0x"+Integer.toHexString(z1));
		// System.err.println("checkBits=0x"+Integer.toHexString(checkBits));

		int y2 = (z1 - y);
		// System.err.println("y2=0x"+Integer.toHexString(y2));

		int xor1 = 0;
		xor1 = map(xor1, y2, 0, 4);
		xor1 = map(xor1, y2, 1, 9);
		xor1 = map(xor1, y2, 2, 5);
		xor1 = map(xor1, y2, 3, 3);
		xor1 = map(xor1, y2, 4, 14);
		xor1 = map(xor1, y2, 5, 6);
		xor1 = map(xor1, y2, 6, 2);
		xor1 = map(xor1, y2, 7, 13);
		xor1 = map(xor1, y2, 8, 7);
		xor1 = map(xor1, y2, 9, 12);
		xor1 = map(xor1, y2, 10, 1);
		xor1 = map(xor1, y2, 11, 11);
		xor1 = map(xor1, y2, 12, 8);
		xor1 = map(xor1, y2, 13, 10);
		xor1 = map(xor1, y2, 14, 0);
		xor1 = map(xor1, y2, 15, 15);

		// System.err.println("xor1=0x"+Integer.toHexString(xor1));

		if (((xor1 ^ checkBits) & 0x3FFF) != 0) {
			throw new InvalidCodeException(code);
		}

		// constant initialization number
		int initNum = 23145;

		int x = xor1 ^ initNum;

		return x;
	}

}
