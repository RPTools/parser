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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringLiteralTransformer {
	private int id = 0;
	private HashMap<String, String> strings = new HashMap<String, String>();

	public StringLiteralTransformer() {
	}

	private String getNextToken() {
		return String.format("StringLiteralTransformer%dTOKEN", id++);
	}

	private synchronized String removeStringsTransform(String str) {

		StringBuilder ret = new StringBuilder();

		StringBuilder currentString = new StringBuilder();
		boolean inString = false;
		char currentStringChar = '\'';

		for (char c : str.toCharArray()) {
			if (inString) {
				currentString.append(c);

				if (c == currentStringChar) {
					inString = false;

					String token = getNextToken();

					strings.put(token, currentString.toString());

					currentString = new StringBuilder();
					ret.append(token);
				}
			} else {
				switch (c) {
				case '\'':
				case '"':
					currentStringChar = c;
					currentString.append(c);
					inString = true;
					break;
				default:
					ret.append(c);
					break;
				}
			}
		}

		return ret.toString();
	}

	private Pattern tokenRegex = Pattern.compile("StringLiteralTransformer\\d+TOKEN");

	private synchronized String replaceStringsTransform(String str) {
		Matcher m = tokenRegex.matcher(str);
		StringBuffer sb = new StringBuffer();

		while (m.find()) {
			String token = m.group();

			m.appendReplacement(sb, strings.remove(token));
		}

		m.appendTail(sb);

		return sb.toString();
	}

	public Transformer getRemoveTransformer() {
		return new Transformer() {
			public String transform(String str) {
				return removeStringsTransform(str);
			}
		};
	}

	public Transformer getReplaceTransformer() {
		return new Transformer() {
			public String transform(String str) {
				return replaceStringsTransform(str);
			}
		};
	}

}
