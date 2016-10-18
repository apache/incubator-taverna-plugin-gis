<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
# Apache Taverna Plugin GIS

Apache Taverna Plugin GIS is a plugin for Apache Taverna that provides
support for Geographical Information Systems (GIS), in particular 
[OGC web services](http://www.opengeospatial.org/standards/common).

Status: **Under development**


## License

* (c) 2016 Apache Software Foundation

This product includes software developed at The [Apache Software
Foundation](http://www.apache.org/).

Licensed under the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), see the file
[LICENSE](LICENSE) for details.

The file [NOTICE](NOTICE) contains any additional attributions and
details about embedded third-party libraries and source code.


# Contribute

Please subscribe to and contact the
[dev@taverna](http://taverna.incubator.apache.org/community/lists#dev mailing list)
for any questions, suggestions and discussions about
Apache Taverna.

Bugs and feature plannings are tracked in the Jira
[Issue tracker](https://issues.apache.org/jira/browse/TAVERNA/component/12326808)
under the `TAVERNA` component _Taverna Plugin GIS._ Feel free
to [add an issue](http://taverna.incubator.apache.org/community/issue-tracker)!

To suggest changes to this source code, feel free to raise a
[GitHub pull request](https://github.com/apache/incubator-taverna-plugin-gis/pulls).
Any contributions received are assumed to be covered by the [Apache License
2.0](https://www.apache.org/licenses/LICENSE-2.0). We might ask you
to sign a [Contributor License Agreement](https://www.apache.org/licenses/#clas)
before accepting a larger contribution.



## Disclaimer

Apache Taverna is an effort undergoing incubation at the
[Apache Software Foundation (ASF)](http://www.apache.org/),
sponsored by the [Apache Incubator PMC](http://incubator.apache.org/).

[Incubation](http://incubator.apache.org/incubation/Process_Description.html)
is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process
have stabilized in a manner consistent with other successful ASF projects.

While incubation status is not necessarily a reflection of the completeness
or stability of the code, it does indicate that the project has yet to be
fully endorsed by the ASF.

## Modules

Experimental modules:

**TODO**: Update the below.

* [taverna-ogc-activity](taverna-ogc-activity/) - Activity for running OGC web services
* [taverna-ogc-activity-ui](taverna-ogc-activity-ui/) - Activity for browsing OGC web services


## Prerequisites

* Java 1.8 or newer (tested with OpenJDK 1.8)
* [Apache Maven](https://maven.apache.org/download.html) 3.2.5 or newer (older
  versions probably also work)


# Building

To build Apache Taverna Language, use:

    mvn clean install

This will build each module and run their tests, producing JARs like
`taverna-gis-activity/target/taverna-gis-activity-0.1.0-incubating.jar`.

## Skipping tests

To skip the tests (these can be timeconsuming), use:

    mvn clean install -DskipTests


If you are modifying this source code independent of the
Apache Taverna project, you may not want to run the
[Rat Maven plugin](https://creadur.apache.org/rat/apache-rat-plugin/)
that enforces Apache headers in every source file - to disable it, try:

    mvn clean install -Drat.skip=true



Installation
-----

**TODO:** Document how to install this plugin.
	
	
Usage
-----

**TODO:** Document how to use this plugin.



