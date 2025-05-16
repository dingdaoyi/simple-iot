package com.github.dingdaoyi;


import com.github.dingdaoyi.proto.model.DataTypeEnum;
import com.github.dingdaoyi.proto.model.DecodeResult;
import com.github.dingdaoyi.proto.model.DeviceData;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.sql.SQLException;
import java.util.List;

public class SqlParseTest {
    @Test
    void testQuery() throws SQLException, JSQLParserException {
        DecodeResult decodeResult = new DecodeResult();
        List<DeviceData> dataList = List.of(
                new DeviceData("pressure", DataTypeEnum.INT, 30),
                new DeviceData("pressure", DataTypeEnum.INT, 32),
                new DeviceData("temperature", DataTypeEnum.INT, 25)
        );
        decodeResult.setDataList(dataList);
        String sql = "SELECT pressure as level FROM dataList WHERE identifier= 'pressure' and  value > 30";
        // 使用 SpEL 进行查询
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("dataList", decodeResult.getDataList());
        String expression = "#dataList.?[identifier == 'pressure' and value > 30]"; // 两个查询条件
        List<DeviceData> result = (List<DeviceData>) parser.parseExpression(expression).getValue(context);
        result.forEach(device -> System.out.println(device.getIdentifier() + " : " + device.getValue()));
    }

    @Test
    void testParseNumber() {
        // 假设输入的原始数据
        double x = 22;

        // 转换公式： (x - 4) / (20 - 4) * 10
        String formula = "(#x - 4) / (20 - 4) * 10";

        // 使用 SpEL 解析并计算
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 设置 x 为变量
        context.setVariable("x", x);

        // 解析并计算结果
        double result = (double) parser.parseExpression(formula).getValue(context);

        // 输出结果
        System.out.println("SpEL 计算结果: " + result);
    }
}
