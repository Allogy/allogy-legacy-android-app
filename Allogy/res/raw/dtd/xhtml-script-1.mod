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
<!-- XHTML Document Scripting Module  ..................................... -->
<!-- Scripting

        script, noscript

     This module declares element types and attributes used to provide
     support for executable scripts as well as an alternate content
     container where scripts are not supported.
-->

<!-- script: Scripting Statement ....................... -->

<!ENTITY % script.element  "INCLUDE" >
<![%script.element;[
<!ENTITY % script.content  "( #PCDATA )" >
<!ENTITY % script.qname  "script" >
<!ELEMENT %script.qname;  %script.content; >
<!-- end of script.element -->]]>

<!ENTITY % script.attlist  "INCLUDE" >
<![%script.attlist;[
<!ATTLIST %script.qname;
      %XHTML.xmlns.attrib;
	  %id.attrib;
      xml:space    ( preserve )             #FIXED 'preserve'
      charset      %Charset.datatype;       #IMPLIED
      type         %ContentType.datatype;   #REQUIRED
      src          %URI.datatype;           #IMPLIED
      defer        ( defer )                #IMPLIED
>
<!-- end of script.attlist -->]]>

<!-- noscript: No-Script Alternate Content ............. -->

<!ENTITY % noscript.element  "INCLUDE" >
<![%noscript.element;[
<!ENTITY % noscript.content
     "( %Block.mix; )+"
>
<!ENTITY % noscript.qname  "noscript" >
<!ELEMENT %noscript.qname;  %noscript.content; >
<!-- end of noscript.element -->]]>

<!ENTITY % noscript.attlist  "INCLUDE" >
<![%noscript.attlist;[
<!ATTLIST %noscript.qname;
      %Common.attrib;
>
<!-- end of noscript.attlist -->]]>

<!-- end of xhtml-script-1.mod -->
