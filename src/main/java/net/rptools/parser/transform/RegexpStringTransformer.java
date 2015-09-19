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

import java.util.regex.Pattern;

public class RegexpStringTransformer implements Transformer {
	
	private final Pattern[] patterns;
	private final String[] replacements;
	
	public RegexpStringTransformer(String[][] regexps) {
		this.patterns = new Pattern[regexps.length]; 
		this.replacements = new String[regexps.length];
        
		for (int i = 0; i < regexps.length; i++) {
			this.patterns[i] = Pattern.compile(regexps[i][0]);
			this.replacements[i] = regexps[i][1];
		}
	}
	
	public RegexpStringTransformer(String[] patterns, String[] replacements) {
		this.patterns = new Pattern[patterns.length]; 
		                       
		for (int i = 0; i < patterns.length; i++)
			this.patterns[i] = Pattern.compile(patterns[i]);
		
		this.replacements = new String[replacements.length];
		System.arraycopy(replacements, 0, this.replacements, 0, replacements.length);
	}

	public String transform(String str) {
		String ret = str;

		for (int i = 0; i < patterns.length; i++) {
			Pattern p = patterns[i];
			ret = p.matcher(ret).replaceAll(replacements[i]);
		}
		
		return ret;
	}

}
