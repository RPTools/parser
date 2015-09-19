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
import net.rptools.parser.function.AbstractFunction;
import net.rptools.parser.function.EvaluationException;
import net.rptools.parser.function.ParameterException;


public class Addition extends AbstractFunction {
    public Addition() {
        super(1, -1, "add", "sum", "+", "concat");
    }
    
    @Override
    public Object childEvaluate(Parser parser, String functionName, List<Object> parameters) throws EvaluationException, ParameterException {
        if (parameters.size() == 1) {
            // unary usage
            return parameters.get(0);
        } else {
        	
        	if (containsString(parameters)) {
        		StringBuilder sb = new StringBuilder();
        		for (Object param : parameters) {
        			sb.append(param.toString());
        		}
        		
        		return sb.toString();
        	} else {
                BigDecimal total = new BigDecimal(0);
                
                for (Object param : parameters) {
                    BigDecimal n = (BigDecimal) param;

                    total = total.add(n);
                }
            
                return total;
        	}
        }
    }
    
    @Override
    public void checkParameters(List<Object> parameters) throws ParameterException {
        super.checkParameters(parameters);
        
        for (Object param : parameters) {
            if (!(param instanceof BigDecimal || param instanceof String)) throw new ParameterException(String.format("Illegal argument type %s, expecting %s or %s", param.getClass().getName(), BigDecimal.class.getName(), String.class.getName()));
            
        }
    }
}
