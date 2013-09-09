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

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Shows Legal Information to User
 * 
 * @author Jamie Huson
 * 
 */
public class AboutActivity extends BaseActivity {

  TextView mAboutText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_about);
    super.onCreate(savedInstanceState);

    mAboutText = (TextView) findViewById(R.id.about_text);

    final StringBuilder builder =
        new StringBuilder().append("Allogy Interactive")
            .append("\n\nWe conduct original academic research in:")
            .append("\n\tMobile Technology").append("\n\tWeb 2.0")
            .append("\n\tSocial Networking").append("\n\tVirtual Worlds")
            .append("\n\tDigital Media").append("\n\nwww.allogy.com");
            

    mAboutText.setTextColor(Color.BLACK);
    mAboutText.setText(builder.toString());
  }

}
