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

package com.allogy.app;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allogy.app.provider.Academic;
import com.allogy.app.util.ContentLocation;

public class PublisherInfoActivity extends Activity {

  public static final String INTENT_EXTRA_ID = "com.allogy.app.publisher.id";

  private static ImageView mLogo;
  private static TextView mTitle, mWebsite, mPhone, mEmail, mAddress,
      mDescription;

  private static PublisherInfo mInfo;

  public static final class PublisherInfo {
    public long id;
    public String logo;
    public String title;
    public String description;
    public String website;
    public String phone;
    public String email;
    public String address_line_1;
    public String address_line_2;
    public String city;
    public String region;
    public String country;
    public String postal_code;
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_publisher_info);

    mLogo = (ImageView) findViewById(R.id.publisher_logo);
    mTitle = (TextView) findViewById(R.id.publisher_title);
    mWebsite = (TextView) findViewById(R.id.publisher_website);
    mAddress = (TextView) findViewById(R.id.publisher_address);
    mDescription = (TextView) findViewById(R.id.publisher_description);
    mPhone = (TextView) findViewById(R.id.publisher_phone);
    mEmail = (TextView) findViewById(R.id.publisher_email);

    mInfo = new PublisherInfo();

    Intent i = getIntent();

    if (i.hasExtra(INTENT_EXTRA_ID)) {
      mInfo.id = i.getLongExtra(INTENT_EXTRA_ID, -1);
    } else {
      Toast.makeText(this, "Could Not Find Publisher", Toast.LENGTH_SHORT)
          .show();
      finish();
    }

    loadContent();
    displayContent();
  }

  private void loadContent() {

    Uri publisher =
        Uri.withAppendedPath(Academic.Publishers.CONTENT_URI,
            Long.toString(mInfo.id));

    Log.i("PublisherInfo", publisher.toString());
    
    Cursor pubContent =
        managedQuery(publisher, null, null, null,
            Academic.Publishers.SORT_ORDER_DEFAULT);

    if (pubContent != null) {
      pubContent.moveToFirst();

      mInfo.title =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.TITLE));
      mInfo.description =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.DESCRIPTION));
      mInfo.logo =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.LOGO));
      mInfo.website =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.WEBSITE));
      mInfo.email =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.EMAIL));
      mInfo.phone =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.PHONE));
      mInfo.address_line_1 =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.ADDRESS_LINE_1));
      mInfo.address_line_2 =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.ADDRESS_LINE_2));
      mInfo.city =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.CITY));
      mInfo.region =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.REGION));
      mInfo.country =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.COUNTRY));
      mInfo.postal_code =
          pubContent.getString(pubContent
              .getColumnIndex(Academic.Publishers.POSTAL_CODE));


    } else {
      Toast.makeText(this, "Could Not Find Publisher", Toast.LENGTH_SHORT)
          .show();
      finish();
    }
  }

  private void displayContent() {

    mTitle.setText(mInfo.title);
    mDescription.setText(mInfo.description);
    mPhone.setText(mInfo.phone);
    mEmail.setText(mInfo.email);
    mWebsite.setText(mInfo.website);

    // Address
    String address = "";
    
    if(mInfo.address_line_1 != null && mInfo.address_line_1.length() > 0)
      address += mInfo.address_line_1 + "\n";
    
    if(mInfo.address_line_2 != null && mInfo.address_line_2.length() > 0)
      address += mInfo.address_line_2 + "\n";
      
    if(mInfo.city != null && mInfo.city.length() > 0)
      address += mInfo.city;
    
    if(mInfo.region != null && mInfo.region.length() > 0)
      address += ", " + mInfo.region;
    
    if(mInfo.country != null && mInfo.country.length() > 0)
      address += ", " + mInfo.country;
    
    if(mInfo.postal_code != null && mInfo.postal_code.length() > 0)
      address += "\n" + mInfo.postal_code;
    
    mAddress.setText(address);

    // Logo
    if (mInfo.logo != null && mInfo.logo.length() > 0) {
    	String logo = ContentLocation.getContentLocation(this) + "/Icons/" + mInfo.logo;
    	File logoFile = new File(logo);

      if (logoFile.exists() && !logoFile.isDirectory()) {
        mLogo.setImageBitmap(BitmapFactory.decodeFile(logo));
      }
    }


  }

  /**
   * Launch Browser
   */
  public void onWebsiteClick(View v) {
	  
	try{
	  Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(mInfo.website));
	  startActivity(i);
	}catch(Exception e){}

  }

  /**
   * Launch Dialer
   */
  public void onPhoneClick(View v) {
	if(mInfo.phone != null)
	{
	  Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mInfo.phone));
	  startActivity(i);
	}
  }

  /**
   * Launch Compose Email
   */
  public void onEmailClick(View v) {
	if(mInfo.email != null)
	{
	  Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:"+mInfo.email));
	  startActivity(i);
	}
  }

  /**
   * Launch Maps
   */
  public void onAddressClick(View v) {
	
	// Address for google maps intent
    String address = "";
    
    if(mInfo.address_line_1 != null && mInfo.address_line_1.length() > 0)
      address += mInfo.address_line_1;
    
    // typically address line 2 is for building #, apt # ect. so it is omitted 
    
    if(mInfo.city != null && mInfo.city.length() > 0)
      address += " " + mInfo.city;
    
    if(mInfo.region != null && mInfo.region.length() > 0)
      address += ", " + mInfo.region;
    
    
    if(mInfo.country != null && mInfo.country.length() > 0)
      address += ", " + mInfo.country;
    
    if(mInfo.postal_code != null && mInfo.postal_code.length() > 0)
      address += " " + mInfo.postal_code;
	
    if(address != "")
    {
      Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+address));
      startActivity(i);
    }
  }
  

  /** Action Bar Home Button */
  public void onHomeClick(View v) {
    Intent i = new Intent();
    i.setClass(PublisherInfoActivity.this, HomeActivity.class);
    startActivity(i);
    finish();
  }

  /** Action Bar Search Button */
  public void onSearchClick(View v) {
    onSearchRequested();
  }
}
