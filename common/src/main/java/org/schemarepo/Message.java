/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.schemarepo;

/**
 * Static Strings used to communicate a message to the end-user.
 */
public class Message {
  public static final String SCHEMA_WITH_NEWLINE_ERROR =
    "ERROR: One of the schemas for this topic contains a new line and won't be parse-able properly. "
      + "Please use a non-plain text format instead (e.g.: JSON).";

  public static final String SUBJECT_DOES_NOT_EXIST_ERROR = "This subject does not exist.";

  public static final String SCHEMA_DOES_NOT_EXIST_ERROR = "This schema does not exist.";

  public static final String SCHEMA_IS_NOT_LEGAL_SYNTAX_ERROR = "Not a legal schema syntax.";

  public static final String CONTENT_TYPE_ERROR = "Content-Type is not set correctly.";

  public static final String ACCEPT_ERROR = "Accept is not set correctly.";
}
