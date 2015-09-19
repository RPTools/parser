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
package net.rptools.parser.function.impl;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.parser.Parser;
import net.rptools.parser.function.AbstractNumberFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;


public class Lesser extends AbstractNumberFunction {
    public Lesser() {
        super(2, -1, "lt", "<");
    }
    
    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws EvaluationException, ParameterException {
        boolean value = true;
        
        for (int i = 0; i < parameters.size() - 1; i++) {
        	BigDecimal d1 = (BigDecimal) parameters.get(i);
        	BigDecimal d2 = (BigDecimal) parameters.get(i+1);
        	
        	value &= (d1.compareTo(d2) < 0);
        }
    
        return value ? BigDecimal.ONE : BigDecimal.ZERO;
    }
}
