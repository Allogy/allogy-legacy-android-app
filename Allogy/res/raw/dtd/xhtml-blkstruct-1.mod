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
<!-- XHTML Block Structural Module  ....................................... -->
<!-- Block Structural

        div, p

     This module declares the elements and their attributes used to
     support block-level structural markup.
-->

<!ENTITY % div.element  "INCLUDE" >
<![%div.element;[
<!ENTITY % div.content
     "( #PCDATA | %Flow.mix; )*"
>
<!ENTITY % div.qname  "div" >
<!ELEMENT %div.qname;  %div.content; >
<!-- end of div.element -->]]>

<!ENTITY % div.attlist  "INCLUDE" >
<![%div.attlist;[
<!ATTLIST %div.qname;
      %Common.attrib;
>
<!-- end of div.attlist -->]]>

<!ENTITY % p.element  "INCLUDE" >
<![%p.element;[
<!ENTITY % p.content
     "( #PCDATA | %Inline.mix; )*" >
<!ENTITY % p.qname  "p" >
<!ELEMENT %p.qname;  %p.content; >
<!-- end of p.element -->]]>

<!ENTITY % p.attlist  "INCLUDE" >
<![%p.attlist;[
<!ATTLIST %p.qname;
      %Common.attrib;
>
<!-- end of p.attlist -->]]>

<!-- end of xhtml-blkstruct-1.mod -->
