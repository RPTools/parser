/*
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
package net.rptools.parser;

/**
 * Interface that does variable resolution for the parser.  If the parser is constructed
 * with an instance of this interface, then all variable resolution will go through
 * that instance instead of through the default {@link MapVariableResolver} instance.
 */
public interface VariableResolver {
    public boolean containsVariable(String name) throws ParserException;

    public void setVariable(String name, Object value) throws ParserException;

    public Object getVariable(String variableName) throws ParserException;

    public boolean containsVariable(String name, VariableModifiers vType) throws ParserException;

    public void setVariable(String name, VariableModifiers vType, Object value) throws ParserException;

    public Object getVariable(String variableName, VariableModifiers vType) throws ParserException;
}
