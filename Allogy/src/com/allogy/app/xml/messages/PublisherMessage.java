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

package com.allogy.app.xml.messages;

/**
 * A message that holds information about publisher.
 * 
 * @author Pramod Chakrapani
 **/
public class PublisherMessage extends Message{
	
	/**
	 * Exact names of the xml tags for the publisher information
	 * access the elements using the class indexing contants.*/
	public static final String[] xmlFields = new String[]{ "publisher","id", "title",
		                                                   "description", "logo",
		                                                   "address_line_1","address_line_2",
		                                                   "city", "region", "country",
		                                                   "postal_code", "institution",
		                                                   "website", "email",
		                                                   "phone"
		                                                   };
	public static final int XML_FIELDS_ROOT_INDEX = 0;
	public static final int XML_FIELDS_ROOT_CHILD_ID = 1;
	public static final int XML_FIELDS_ROOT_CHILD_TITLE = 2;
	public static final int XML_FIELDS_ROOT_CHILD_DESCRIPTION = 3;
	public static final int XML_FIELDS_ROOT_CHILD_LOGO = 4;
	public static final int XML_FIELDS_ROOT_CHILD_ADDRESS_LINE_1 = 5;
	public static final int XML_FIELDS_ROOT_CHILD_ADDRESS_LINE_2 = 6;
    public static final int XML_FIELDS_ROOT_CHILD_CITY = 7;
    public static final int XML_FIELDS_ROOT_CHILD_REGION = 8;
    public static final int XML_FIELDS_ROOT_CHILD_COUNTRY = 9;
    public static final int XML_FIELDS_ROOT_CHILD_POSTAL_CODE = 10;
    public static final int XML_FIELDS_ROOT_CHILD_INSTITUTION = 11;
    public static final int XML_FIELDS_ROOT_CHILD_WEBSITE = 12;
    public static final int XML_FIELDS_ROOT_CHILD_EMAIL = 13;
    public static final int XML_FIELDS_ROOT_CHILD_PHONE = 14;

	private String id;
	private String description;
	private String logo;
	private String address_line_1;
	private String address_line_2;
	private String city;
	private String region;
	private String country;
	private String postal_code;
	private String institution;
	private String website;
	private String email;
	private String phone;
	
	@Override
	public PublisherMessage copy()
	{
		PublisherMessage duplicate = new PublisherMessage();
		duplicate.Title = Title;
		duplicate.id = id;
		duplicate.description = description;
		duplicate.logo = logo;
		duplicate.address_line_1 = address_line_1;
		duplicate.address_line_2 = address_line_2;
		duplicate.city = city;
		duplicate.region = region;
		duplicate.country = country;
		duplicate.postal_code = postal_code;
		duplicate.institution = institution;
		duplicate.website = website;
		duplicate.email = email;
		duplicate.phone = phone;
		return duplicate;
	}

    public void setId(String data) {
      this.id = data.trim();
    }
  
    public String getId() {
      return id;
    }
    
    public void setDescription(String data) {
      this.description = data.trim();
    }

    public String getDescription() {
      return description;
    }

    public void setLogo(String data) {
      this.logo = data.trim();
    }

    public String getLogo() {
      return logo;
    }

    public void setAddress_line_1(String data) {
      this.address_line_1 = data.trim();
    }

    public String getAddress_line_1() {
      return address_line_1;
    }

    public void setAddress_line_2(String data) {
      this.address_line_2 = data.trim();
    }

    public String getAddress_line_2() {
      return address_line_2;
    }

    public void setCity(String data) {
      this.city = data.trim();
    }

    public String getCity() {
      return city;
    }

    public void setRegion(String data) {
      this.region = data.trim();
    }

    public String getRegion() {
      return region;
    }

    public void setCountry(String data) {
      this.country = data.trim();
    }

    public String getCountry() {
      return country;
    }

    public void setPostal_code(String data) {
      this.postal_code = data.trim();
    }

    public String getPostal_code() {
      return postal_code;
    }

    public void setInstitution(String data) {
      this.institution = data.trim();
    }

    public String getInstitution() {
      return institution;
    }

    public void setWebsite(String data) {
      this.website = data.trim();
    }

    public String getWebsite() {
      return website;
    }

    public void setEmail(String data) {
      this.email = data.trim();
    }

    public String getEmail() {
      return email;
    }

    public void setPhone(String data) {
      this.phone = data.trim();
    }

    public String getPhone() {
      return phone;
    }
	
}
