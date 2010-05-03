/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package gov.nasa.jpl.oodt.cas.curation.servlet;

/**
 * 
 * Met keys used in the <code>context.xml</code> file to configure the CAS
 * curator webapp.
 * 
 * @author mattmann
 * @version $Revision$
 * 
 */
public interface CuratorConfMetKeys {

  final String MET_EXTRACTOR_CONF_UPLOAD_PATH = "gov.nasa.jpl.oodt.cas.curator.metExtractorConf.uploadPath";

  final String POLICY_UPLOAD_PATH = "gov.nasa.jpl.oodt.cas.curator.dataDefinition.uploadPath";

  final String FM_URL = "gov.nasa.jpl.oodt.cas.fm.url";

  final String SSO_IMPL_CLASS = "gov.nasa.jpl.oodt.cas.security.sso.implClass";

  final String DEFAULT_TRANSFER_FACTORY = "gov.nasa.jpl.oodt.cas.filemgr.datatransfer.LocalDataTransferFactory";

  final String CRAWLER_CONF_FILE = "classpath:/gov/nasa/jpl/oodt/cas/crawl/crawler-config.xml";

  final String PROJECT_DISPLAY_NAME = "gov.nasa.jpl.oodt.cas.curator.projectName";

  final String STAGING_AREA_PATH = "gov.nasa.jpl.oodt.cas.curator.stagingAreaPath";

  final String MET_AREA_PATH = "gov.nasa.jpl.oodt.cas.curator.metAreaPath";
  
  final String MET_EXTENSION = "gov.nasa.jpl.oodt.cas.curator.metExtension";
  
  final String FM_PROPS = "gov.nasa.jpl.oodt.cas.curator.fmProps";
}
