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

package com.hsl.txtreader;


public class DocMgr {
    public static final int STATE_INIT = 0;
    public static final int STATE_FILE_OPEN = 2;

    private DocFile docFile;
    private State curState;

    public DocMgr() {
        curState = new InitState();
    }

    public void openDoc(String name) {
        curState.OpenDoc(name);
    }

    public int getPageNo() {
        return curState.getPageNo();
    }

    public StringBuffer getPageContent(int pNo) {
        return curState.getPageContent(pNo);
    }

    public DocOutline getOutline() {
        return curState.getOutline();
    }

    public int getNumPages() {
        return curState.getNumPages();
    }

    public void setState(State newState) {
        curState = newState;
    }

    private class State {
        public void OpenDoc(String name) {}
        public StringBuffer getPageContent(int pNo) {
            return null;
        }
        public DocOutline getOutline() {
            return null;
        }
        public int getNumPages() {
            return 0;
        }
        public int getPageNo() {
            return 0;
        }
    }

    private class InitState extends State {
        public void OpenDoc(String name) {
            docFile = new DocFile(name);
            if (docFile.getErrorCode() == DocFile.ERR_OK) {
                docFile.getOutline();
                setState(new FileOpenedState());
            }
        }
    }

    private class FileOpenedState extends State {
        public void OpenDoc(String name) {
            if (!docFile.getFileName().equals(name)) {
                setState(new InitState());
                curState.OpenDoc(name);
            }
        }

        public StringBuffer getPageContent(int pNo) {
            return docFile.getPageContent(pNo);
        }

        public DocOutline getOutline() {
            return docFile.getOutline();
        }

        public int getNumPages() {
            return docFile.getNumPages();
        }

        public int getPageNo() {
            return docFile.getPageNo();
        }
    }
}
