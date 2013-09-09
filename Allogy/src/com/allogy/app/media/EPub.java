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

package com.allogy.app.media;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class extracts and parses and EPUB format EBook
 * 
 * @author Yazen Ghannam
 */


public class EPub extends EBook {
  
  private final String LOG_TAG = EPub.class.getName();

  // File paths required for parsing epub
  private static String mTempPath = "/sdcard/temp/epubReader/";
  private static String mContainerPath = mTempPath + "META-INF/container.xml";
  private static String mOpsPath = mTempPath + "OPS/";
  private static String mOpfPath;

  private final static int BUFFER = 2048;
  private String mSectionContent;

  
  private int numPages;
  private int bookLength;
  private ArrayList<String> TabOfConts;
  private ArrayList<String> Sections;
  private MetaData metadata;

  private static XMLparser parser;

  // File paths to all content and image files.
  private static ArrayList<String> contentPaths = new ArrayList<String>();
  private static ArrayList<String> imagePaths = new ArrayList<String>();

  public EPub(String filename) {
    ExtractBook(filename);
    System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    parser = new XMLparser();

    // Parse the container file and return the location of the OPF
    mOpfPath = mTempPath + parser.parseContainer(mContainerPath);

    // Parse the packaging file and store all the content paths
    TabOfConts = new ArrayList<String>();
    parser.parsePackaging(mOpfPath);

    Sections = new ArrayList<String>();
    bookLength = 0;

    // Loop through content paths and parse each content file
    for (String s : contentPaths) {
      mSectionContent = new String();
      parser.parseSection(mOpsPath + s);
      Sections.add(mSectionContent);
      bookLength += mSectionContent.length();
    }

  }

  /*
   * Returns the Array list that contains all of the epub's text
   */
  public ArrayList<String> getSections() {
    return Sections;
  }

  /*
   * Returns the epub's table of contents
   */
  protected ArrayList<String> getTabOfConts() {
    return TabOfConts;
  }

  // Extracts the epub archive to local disk
  private static void ExtractBook(String path) {

    try {

      File temp = new File(mTempPath);
      temp.mkdirs();

      BufferedOutputStream dest = null;

      File fin = new File(path);
      ZipInputStream zin =
          new ZipInputStream(new BufferedInputStream(new FileInputStream(fin)));
      ZipEntry entry;

      // Loop through all files in the archive
      while ((entry = zin.getNextEntry()) != null) {
        if (entry.isDirectory()) {
          new File(mTempPath + "//" + entry.getName()).mkdir();
        } else {
          FileOutputStream fout =
              new FileOutputStream(mTempPath + "\\" + entry.getName());
          dest = new BufferedOutputStream(fout, BUFFER);
          int count;
          byte data[] = new byte[BUFFER];
          while ((count = zin.read(data, 0, BUFFER)) != -1) {
            dest.write(data, 0, count);
          }
          dest.flush();
          dest.close();
        }

      }
      zin.close();
    } catch (Exception e) {
       // TODO: proper error handling
    }
  }

  public class XMLparser extends DefaultHandler {

    /**
     * @param args
     */
    int count;
    DefaultHandler contentHandler;
    PrintStream fout;

    public String type = null;
    public String mOpfPath = null;

    public String elementName = "x";

    // Booleans for HTML tags
    public boolean paragraph;
    public boolean link;
    public boolean body;


    // Used to parse the "container.xml" file
    public String parseContainer(String mContainerPath) {
      contentHandler = new ContainerContentHandler();
      perform(mContainerPath);
      return mOpfPath;
    }

    // Used to parse the OPF file
    public void parsePackaging(String packagingPath) {
      metadata = new MetaData();
      contentHandler = new PackagingContentHandler();
      perform(packagingPath);
    }

    // Used to parse the content files
    public void parseSection(String sectionPath) {
      contentHandler = new SectionContentHandler();
      perform(sectionPath);
    }

    public void perform(String uri) {

      try {
        XMLReader sp = XMLReaderFactory.createXMLReader();
        sp.setEntityResolver(new DTDResolver());
        sp.setContentHandler(contentHandler);
        sp.parse(uri);
      }

      catch (IOException e) {
        // TODO: proper error handling
      }

      catch (SAXException e) {
        // TODO: proper error handling
      }
    }

    class ContainerContentHandler extends DefaultHandler {
      @Override
      public void startElement(String namespaceURI, String localName,
          String rawName, Attributes atts) throws SAXException {
        for (int i = 0; i < atts.getLength(); i++)
          // Return path to "OPF" file, only applies when parsing
          // "container.xml"
          if (atts.getValue(i).contains(".opf")) mOpfPath = atts.getValue(i);
      }
    }

    class PackagingContentHandler extends DefaultHandler {

      @Override
      public void characters(char[] ch, int start, int end) throws SAXException {
        String s = new String(ch, start, end);

        // TODO: Add all MetaData according to EPUB Specifications
        // Parse MetaData information from OPF
        if (elementName != null) {
          if (elementName.contains("title")) {
            if (metadata.title == null)
              metadata.title = s;
            else
              metadata.title = metadata.title.concat(s);
          }
          if (elementName.contains("creator")) {
            if (metadata.creator == null)
              metadata.creator = s;
            else
              metadata.creator = metadata.creator.concat(s);
          }

        }

      }

      @Override
      public void endElement(String namespaceURI, String localName,
          String rawName) throws SAXException {

        if (localName.equals("a"))
          link = false;

        else if (localName.equals("p")) {
          fout.println("\n");
          paragraph = false;
        }
      }

      @Override
      public void startElement(String namespaceURI, String localName,
          String rawName, Attributes atts) throws SAXException {


        String ref = null;
        String id = null;
        for (int i = 0; i < atts.getLength(); i++) {

          if (rawName.compareTo("item") == 0) {
            if (atts.getLocalName(i).compareTo("href") == 0)
              ref = atts.getValue(i);
            else if (atts.getLocalName(i).compareTo("id") == 0)
              id = atts.getValue(i);
            else if (atts.getLocalName(i).compareTo("media-type") == 0)
              if (atts.getValue(i).contains("xhtml")) {
                contentPaths.add(ref);
                TabOfConts.add(id);
              } else if (atts.getValue(i).contains("image"))
                imagePaths.add(ref);

          }


        }
      }
    }

    class SectionContentHandler extends DefaultHandler {
      public void characters(char[] ch, int start, int end) throws SAXException {
        String s = new String(ch, start, end);
        if (body) mSectionContent = mSectionContent.concat(s);

      }

      @Override
      public void endElement(String namespaceURI, String localName,
          String rawName) throws SAXException {

        if (localName.equals("a"))
          link = false;

        else if (localName.equals("body")) {
          mSectionContent = mSectionContent.concat("\n");
          body = false;
        }
      }

      @Override
      public void startElement(String namespaceURI, String localName,
          String rawName, Attributes atts) throws SAXException {

        if (localName.equals("body")) body = true;

      }

    }
    // Used to look up XHTML DTD locally
    public class DTDResolver implements EntityResolver {
      @Override
      public InputSource resolveEntity(String publicID, String systemID)
          throws SAXException {
        if (systemID.equals("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd")) {
          return new InputSource("/res/raw/dtd/xhtml11.dtd");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-inlstyle-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-inlstyle-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-framework-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-framework-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-text-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-text-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-hypertext-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-hypertext-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-list-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-list-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-edit-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-edit-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-bdo-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-bdo-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-pres-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-pres-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-inlpres-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-inlpres-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-link-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-link-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-meta-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-meta-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-base-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-base-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-script-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-script-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-style-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-style-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-image-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-image-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-csismap-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-csismap-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-ssismap-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-ssismap-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-param-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-param-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-object-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-object-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-table-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-table-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-form-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-form-1.mod");
        } else if (systemID
            .equals("http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-struct-1.mod")) {
          return new InputSource("/res/raw/dtd/xhtml-struct-1.mod");
        }
        return null;
      }
    }
  }

  public class MetaData {

    public String title;
    public String creator;
    public ArrayList<String> subjects = new ArrayList<String>();
    public String description;
    public String publisher;
    public String contributor;
    public String date;
    public String type;
    public String format;
    public String identifier;
    public String source;
    public String language;
    public String relation;
    public String converage;
    public String rights;

  }
}
