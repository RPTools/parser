package net.rptools.parser.ast;

import java.math.BigDecimal;
import java.util.List;

public record NumberNode(BigDecimal value) implements ExpressionNode {
    @Override
    public List<String> getParts() {
        return List.of(value.toPlainString());
    }
}
