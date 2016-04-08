/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.rptools.parser.transform;

import junit.framework.TestCase;

public class StringLiteralTransformerTest extends TestCase {
	public void testRemove() {
		StringLiteralTransformer transformer = new StringLiteralTransformer();

		assertEquals("This is a StringLiteralTransformer0TOKEN, dont you think?", transformer.getRemoveTransformer().transform("This is a \"test\", dont you think?"));
	}

	public void testReplace() {
		StringLiteralTransformer transformer = new StringLiteralTransformer();

		String input = "This is a \"test\", dont you think?.   Why does 'this' work? But not this '\"' ";

		assertEquals(input, transformer.getReplaceTransformer().transform(transformer.getRemoveTransformer().transform(input)));
	}
}
