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
<!-- XHTML Server-side Image Map Module  .................................. -->
<!-- Server-side Image Maps

     This adds the 'ismap' attribute to the img and input elements
     to support server-side processing of a user selection.
-->

<!ATTLIST %img.qname;
      ismap        ( ismap )                #IMPLIED
>

<!ATTLIST %input.qname;
      ismap        ( ismap )                #IMPLIED
>

<!-- end of xhtml-ssismap-1.mod -->
