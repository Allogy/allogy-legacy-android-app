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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

import com.allogy.app.util.Util;

public class DecryptFile {

	private static final String LOG_TAG = DecryptFile.class.getName();

	/**
	 * Decrypt a file using the specified Key and save to the Decrypted
	 * Directory
	 * 
	 * @param encryptionType
	 *            AES or DES
	 * @param filePath
	 *            The path to the encrypted file
	 * @param keyFileName
	 *            The path to the key file
	 * @param outputName
	 *            The name of the resulting decrypted file
	 */
	public static void decryptFile(String filePath) {
		try {
			String fileName = Util.getFileNameFromPath(filePath);
			String outputPath = Util.DECRYPTED_DIRECTORY + fileName;
			String keyFileName = Util.KEY_DIRECTORY + fileName + ".key";

			Log.i(LOG_TAG, fileName);

			Cipher decCipher = null;
			decCipher = Cipher.getInstance("AES");

			// get the key give it to the cipher
			KeyTools kTool = new KeyTools();
			SecretKeySpec key = kTool.getKey(keyFileName, LOG_TAG);
			decCipher.init(Cipher.DECRYPT_MODE, key);

			// go through the encrypted directory and find the number of
			// partials
			int num_of_files = 0;
			String[] filenames = new File(Util.ENCRYPTED_DIRECTORY).list();
			for (int k = 0; k < filenames.length; k++) {
				if (filenames[k].contains(fileName))
					num_of_files++;
			}

			FileOutputStream output = null;

			if (num_of_files > 0) {
				// output name removes the extension so other file systems dont
				// recoginize files
				output = new FileOutputStream(new File(outputPath.replace(
						".mp4", "").trim()));
			}

			Log.i(LOG_TAG, "There are " + num_of_files + " partitions");

			for (int i = 0; i < num_of_files; i++) {
				String partialFileName = Util.ENCRYPTED_DIRECTORY + fileName
						+ "_part" + i + ".enc";

				Log.i(LOG_TAG, "decrypting " + partialFileName);

				// open and readin the encrypted partial file
				File part = new File(partialFileName);
				BufferedInputStream buf = new BufferedInputStream(
						new FileInputStream(part));
				byte[] encrypted = new byte[(int) part.length()];
				buf.read(encrypted);

				// create the decrypted file in-memory
				byte[] decrypt = decCipher.doFinal(encrypted);

				// write the output to the decrypted file
				output.write(decrypt);

				Log.i(LOG_TAG, "decrypted part " + (i + 1) + " of "
						+ num_of_files);
			}

			if (output != null)
				output.close();

			// delete the encrypted files
			for (int j = 0; j < num_of_files; j++) {
				(new File(Util.ENCRYPTED_DIRECTORY + fileName + "_part" + j
						+ ".enc")).delete();

			}

			Log.i(LOG_TAG, "Decrypted \"" + outputPath);
		} catch (NoSuchPaddingException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (InvalidKeyException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (IllegalBlockSizeException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (BadPaddingException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		} 

	}
}
