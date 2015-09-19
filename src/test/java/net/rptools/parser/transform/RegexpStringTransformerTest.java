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
package net.rptools.parser.transform;

import junit.framework.TestCase;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;

public class RegexpStringTransformerTest extends TestCase {

	public void testSimpleReplacement() throws ParserException {
        Parser p = new Parser();

        p.addTransformer(new RegexpStringTransformer(new String[] { "foo" }, new String[] { "bar" }));
        
        assertEquals("10 + bar + 17", p.parseExpression("10 + foo + 17").format());
        assertEquals("10 + bard + 17", p.parseExpression("10 + food + 17").format());
	}

	public void testReplacementWithSubstitutions() throws ParserException {
        Parser p = new Parser();

        p.addTransformer(new RegexpStringTransformer(new String[] { "(\\d+)\\s*\\^\\s*(\\d*)" }, new String[] { "pow($1, $2)" }));
        
        assertEquals("pow(3, 7)", p.parseExpression("3^7").format());
        assertEquals("pow(3, 7)", p.parseExpression("3 ^ 7").format());
        assertEquals("1 + 2 + pow(3, 7) + 10", p.parseExpression("1 + 2 + 3^7 + 10").format());
	}
}
