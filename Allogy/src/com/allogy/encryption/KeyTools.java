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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.allogy.app.util.Util;

public class KeyTools {
	public void saveKey(SecretKeySpec key, String keyDir, String filePath,
			String LOG_TAG, Context context) {
		FileOutputStream output = null;
		try {

			String fileName = Util.getFileNameFromPath(filePath);
			
			Log.i("KeyTools", "save key: " + fileName);

			File fileOut = new File(keyDir + fileName + ".key");
			Toast.makeText(
					context,
					"Key file saved as \'" + keyDir + fileName + ".key"
							+ "\'.", Toast.LENGTH_LONG).show();

			output = new FileOutputStream(fileOut);

			output.write(key.getEncoded());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Cannot close output stream!");
					e.printStackTrace();
				}
		}
	}

	public SecretKeySpec makeKey(String fileName, String seed, String LOG_TAG) {
		try {
			SecureRandom secRand = SecureRandom.getInstance("SHA1PRNG");
			secRand.setSeed(seed.getBytes("UTF-8"));

			byte[] key = (fileName + secRand.toString()).getBytes("UTF-8");
			MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

			key = sha1.digest(key);
			// Reduce key to 128 bytes
			byte[] shorterKey = new byte[16];
			for (int i = 0; i < 16; i++)
				shorterKey[i] = key[i];

			return new SecretKeySpec(shorterKey, "AES");
		} catch (NoSuchAlgorithmException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	public SecretKeySpec getKey(String fileName, String LOG_TAG) {
		FileInputStream input = null;

		try {
			File fileIn = new File(fileName);

			input = new FileInputStream(fileIn);

			byte[] bytes = new byte[(int) fileIn.length()];
			while (input.read(bytes) != -1) {
				continue;
			}
			// TODO Remove AES from hardcoded status, consider binding to
			// encrypted file
			return new SecretKeySpec(bytes, "AES");
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Cannot close input stream!" + e.getMessage());
				}
		}
		return null;
	}
}