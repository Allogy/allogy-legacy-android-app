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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.allogy.app.util.Util;

public class EncryptFile {

	private static final String LOG_TAG = EncryptFile.class.getName();

	public static SecretKeySpec matchKeyToFile(String fileName, Context enccon) {
		String fileOut = "";
		SecretKeySpec key = null;
		try {

			if (fileName.compareTo("") == 0) {
				throw new KeyGenFailException(
						KeyGenFailException.failureTypes.EMPTY_FIELD);
			}

			String seed = makeRandomFileName();
			fileOut = Util.ENCRYPTED_DIRECTORY + Util.getFileNameFromPath(fileName);

			String keyFileName = Util.KEY_DIRECTORY + fileName;
			KeyTools kTool = new KeyTools();

			key = kTool.makeKey(keyFileName, seed, LOG_TAG);
			if (key == null)
				throw new KeyGenFailException(
						KeyGenFailException.failureTypes.KEY_NULL);
			else {
				kTool.saveKey(key, Util.KEY_DIRECTORY, fileName, LOG_TAG,
						enccon);
			}

			// if(!encact.getComponentName().getClassName().equals(EncryptedEditActivity.class)){
			encryptFile(fileName, fileOut, key, enccon);
			// }
		} catch (KeyGenFailException e) {
			String msg = null;
			if (e.fail == KeyGenFailException.failureTypes.KEY_NULL) {
				msg = "Was unable to generate a key!";
				Log.e(LOG_TAG, msg);
				Toast.makeText(enccon, msg, Toast.LENGTH_SHORT).show();
			}
			if (e.fail == KeyGenFailException.failureTypes.DATABASE_ERROR) {
				msg = "Corrupted value for directory in databse.";
				Log.e(LOG_TAG, msg);
				Toast.makeText(enccon, msg, Toast.LENGTH_SHORT).show();
			}
			if (e.fail == KeyGenFailException.failureTypes.EMPTY_FIELD) {
				msg = "Please enter a file name to encrypt.";
				Toast.makeText(enccon, msg, Toast.LENGTH_SHORT).show();
			}
			Toast.makeText(
					enccon,
					"Successfuly encrypted \"" + fileName + "\" as \"" + " \""
							+ fileOut + " \" ", Toast.LENGTH_SHORT).show();
		}
		return key;
	}

	public static String makeRandomFileName() {
		// Create random value generator
		Random gen = new Random(System.currentTimeMillis());

		// A character in java is two bytes, so this will be a string between 1
		// and 15 characters long
		int stringSize = 1 + gen.nextInt(15) * 2;

		gen.setSeed(System.currentTimeMillis()); // Set to new seed
		String output = "";
		int nInt = 0;
		for (int i = 0; i < stringSize; i++) {
			nInt = gen.nextInt(75) + 48;
			while (nInt == 33 || nInt == 34 || nInt == 124 || nInt == 92
					|| nInt == 62 || nInt == 63 || nInt == 42 || nInt == 58
					|| nInt == 43 || nInt == 91 || nInt == 93 || nInt == 47
					|| nInt == 39)
				// A reserved character -> "|\\?*<\":>+[]/'"
				nInt = gen.nextInt(75) + 48;
			output += String.valueOf((char) nInt);
		}
		return output;
	}

	public static void encryptFile(String fileInName, String fileOutName, SecretKeySpec key, Context enccon) {
		Log.i(LOG_TAG, fileInName);
		Log.i(LOG_TAG, fileOutName);
		
		FileInputStream input = null;
		FileOutputStream output = null;
		Cipher encCipher = null;

		try {
			encCipher = Cipher.getInstance("AES");
			encCipher.init(Cipher.ENCRYPT_MODE, key);
			
			File fileIn = new File(fileInName);
			if (fileIn.length() > Integer.MAX_VALUE)
				throw new FileTooBigException("File\'" + fileInName
						+ "\' is too large too encrypt.");

			input = new FileInputStream(fileIn);
			
			//partition the file into pieces, each 5Mb in size
			int sizeOf5MB = 5242880;
			int num_of_files = ((int) fileIn.length() / sizeOf5MB) + 1;
			
			for(int i = 0; i < num_of_files; i++){
				byte[] plainText = new byte[sizeOf5MB];
				input.read(plainText, 0 , sizeOf5MB);

				byte[] encrypted = encCipher.doFinal(plainText);

				output = new FileOutputStream(new File(fileOutName + "_part"+ i + ".enc"));
				output.write(encrypted);
				
				Log.i(LOG_TAG, "encrypted: " +  fileOutName + "_part" + i);
			}
			

		} catch (BadPaddingException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (IllegalBlockSizeException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (FileTooBigException e) {
			Log.e(LOG_TAG, e.getMessage());
			Toast.makeText(enccon, "Was unable to encrypt file \'" + fileInName
					+ "\' is too large too encrypt.", Toast.LENGTH_SHORT);
		} catch (IOException ioe) {
			Log.e(LOG_TAG, ioe.getMessage());
			Toast.makeText(enccon, "Was unable to encrypt file \'" + fileInName
					+ "\'.", Toast.LENGTH_SHORT);
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (NoSuchPaddingException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (InvalidKeyException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Cannot close input stream!");
					e.printStackTrace();
				}
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Cannot close output stream!");
					e.printStackTrace();
				}
		}
	}// End encryptFile
}
