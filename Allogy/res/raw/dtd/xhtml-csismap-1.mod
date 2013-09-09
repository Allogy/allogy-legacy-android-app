<!--
  ~ Copyright (c) 2013 Allogy Interactive.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not
  ~ use this file except in compliance with the License. You may obtain a copy of
  ~ the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  ~ WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing permissions and limitations under
  ~ the License.
  -->

<!-- ...................................................................... -->
<!-- XHTML Client-side Image Map Module  .................................. -->
<!-- Client-side Image Maps

        area, map

     This module declares elements and attributes to support client-side
     image maps. This requires that the Image Module (or a module
     declaring the img element type) be included in the DTD.

     These can be placed in the same document or grouped in a
     separate document, although the latter isn't widely supported
-->

<!ENTITY % area.element  "INCLUDE" >
<![%area.element;[
<!ENTITY % area.content  "EMPTY" >
<!ENTITY % area.qname  "area" >
<!ELEMENT %area.qname;  %area.content; >
<!-- end of area.element -->]]>

<!ENTITY % Shape.datatype "( rect | circle | poly | default )">
<!ENTITY % Coords.datatype "CDATA" >

<!ENTITY % area.attlist  "INCLUDE" >
<![%area.attlist;[
<!ATTLIST %area.qname;
      %Common.attrib;
      href         %URI.datatype;           #IMPLIED
      shape        %Shape.datatype;         'rect'
      coords       %Coords.datatype;        #IMPLIED
      nohref       ( nohref )               #IMPLIED
      alt          %Text.datatype;          #REQUIRED
      tabindex     %Number.datatype;        #IMPLIED
      accesskey    %Character.datatype;     #IMPLIED
>
<!-- end of area.attlist -->]]>

<!-- modify anchor attribute definition list
     to allow for client-side image maps
-->
<!ATTLIST %a.qname;
      shape        %Shape.datatype;         'rect'
      coords       %Coords.datatype;        #IMPLIED
>

<!-- modify img attribute definition list
     to allow for client-side image maps
-->
<!ATTLIST %img.qname;
      usemap       IDREF                    #IMPLIED
>

<!-- modify form input attribute definition list
     to allow for client-side image maps
-->
<!ATTLIST %input.qname;
      usemap       IDREF                    #IMPLIED
>

<!-- modify object attribute definition list
     to allow for client-side image maps
-->
<!ATTLIST %object.qname;
      usemap       IDREF                    #IMPLIED
>

<!-- 'usemap' points to the 'id' attribute of a <map> element,
     which must be in the same document; support for external
     document maps was not widely supported in HTML and is
     eliminated in XHTML.

     It is considered an error for the element pointed to by
     a usemap IDREF to occur in anything but a <map> element.
-->

<!ENTITY % map.element  "INCLUDE" >
<![%map.element;[
<!ENTITY % map.content
     "(( %Block.mix; ) | %area.qname; )+"
>
<!ENTITY % map.qname  "map" >
<!ELEMENT %map.qname;  %map.content; >
<!-- end of map.element -->]]>

<!ENTITY % map.attlist  "INCLUDE" >
<![%map.attlist;[
<!ATTLIST %map.qname;
      %XHTML.xmlns.attrib;
      id           ID                       #REQUIRED
      %class.attrib;
      %title.attrib;
      %Core.extra.attrib;
      %I18n.attrib;
      %Events.attrib;
>
<!-- end of map.attlist -->]]>

<!-- end of xhtml-csismap-1.mod -->
