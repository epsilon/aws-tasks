/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datameer.awstasks.exec.command;

import java.util.List;

import datameer.awstasks.exec.ShellCommand;
import datameer.awstasks.exec.handler.ExecCaptureLinesHandler;

public class LsCommand extends ShellCommand<List<String>> {

    public LsCommand(String filePath) {
        super(new String[] { "ls", filePath }, true);
        setDefaultHandler(new ExecCaptureLinesHandler());
    }

    public LsCommand(String options, String filePath) {
        super(new String[] { "ls", options, filePath }, true);
        setDefaultHandler(new ExecCaptureLinesHandler());
    }

}
