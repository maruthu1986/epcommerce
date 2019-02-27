package liquibase.ext.elasticpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ConvertValangToSpringExpressionTest {

	private ConvertValangToSpringExpression expression = new ConvertValangToSpringExpression();

	@Test
	public void replaceValangKeyWordsTest() {
		// default db data
		assertThat(expression.replaceValangKeyWords("isValidConditionType(this) is true")).isEqualTo("(#isValidConditionType((#this))) == true");
		assertThat(expression.replaceValangKeyWords("isValidConditionType(this) is true AND tagValue >= 0 AND tagValue <= 120"))
				.isEqualTo("(#isValidConditionType((#this))) == true AND tagValue >= 0 AND tagValue <= 120");
		assertThat(expression.replaceValangKeyWords("isValidConditionType(this) is true AND tagValue IN 'M', 'F'"))
				.isEqualTo("(#isValidConditionType((#this))) == true AND ({'M', 'F'}.contains(tagValue))");
		assertThat(expression.replaceValangKeyWords("isValidConditionType(this) is true AND tagValue >= 0"))
				.isEqualTo("(#isValidConditionType((#this))) == true AND tagValue >= 0");
		assertThat(expression.replaceValangKeyWords("isValidConditionType(this) is true AND length(tagValue) <= 10"))
				.isEqualTo("(#isValidConditionType((#this))) == true AND (#length(tagValue)) <= 10");

		// functions
		assertThat(expression.replaceValangKeyWords("length('word')")).isEqualTo("(#length('word'))");
		assertThat(expression.replaceValangKeyWords("matches('[a-z,A-Z]', tagValue)")).isEqualTo("(tagValue.matches('[a-z,A-Z]'))");
		assertThat(expression.replaceValangKeyWords("upper(tagValue)")).isEqualTo("(tagValue.toUpperCase())");
		assertThat(expression.replaceValangKeyWords("lower('word')")).isEqualTo("('word'.toLowerCase())");
		assertThat(expression.replaceValangKeyWords("typeof(tagValue, 'java.lang.Integer')"))
				.isEqualTo("(tagValue instanceof T(java.lang.Integer))");
		assertThat(expression.replaceValangKeyWords("this func(this)")).isEqualTo("(#this) (#func((#this)))");
		assertThat(expression.replaceValangKeyWords("#typeof(var, 'int')")).isEqualTo("var instanceof T(int)");

		// dates
		assertThat(expression.replaceValangKeyWords("[20180113] [2018-01-13]"))
				.isEqualTo("(new java.util.Date(2018,01 - 1,13)) (new java.util.Date(2018,01 - 1,13))");
		assertThat(expression.replaceValangKeyWords("[20180113 04:20:01] [20180113 042001] [2018-01-13 042001]"))
				.isEqualTo("(new java.util.Date(2018,01 - 1,13,04,20,01)) (new java.util.Date(2018,01 - 1,13,04,20,01)) "
						+ "(new java.util.Date(2018,01 - 1,13,04,20,01))");

		// operators
		assertThat(expression.replaceValangKeyWords("true WHERE false")).isEqualTo("true && false");
		assertThat(expression.replaceValangKeyWords("a = b a EQUALS b a is b")).isEqualTo("a == b a == b a == b");
		assertThat(expression.replaceValangKeyWords("a <> b a >< b a is not b a not equals b")).isEqualTo("a != b a != b a != b a != b");
		assertThat(expression.replaceValangKeyWords("a greater than or equals b a is greater than or equals b")).isEqualTo("a >= b a >= b");
		assertThat(expression.replaceValangKeyWords("a less than or equals b a is less than or equals b")).isEqualTo("a <= b a <= b");
		assertThat(expression.replaceValangKeyWords("a less than b a is less than b")).isEqualTo("a < b a < b");
		assertThat(expression.replaceValangKeyWords("a greater than b a is greater than b")).isEqualTo("a > b a > b");

		// strings
		assertThat(expression.replaceValangKeyWords("string has text")).isEqualTo("(string.matches(\\s*[^\\s]+\\s*))");
		assertThat(expression.replaceValangKeyWords("string has no text")).isEqualTo("(!string.matches(\\s*[^\\s]+\\s*))");
		assertThat(expression.replaceValangKeyWords("string has length")).isEqualTo("(string.length() > 0)");
		assertThat(expression.replaceValangKeyWords("string has no length")).isEqualTo("(string.length() == 0)");
		assertThat(expression.replaceValangKeyWords("string is blank")).isEqualTo("(string == null || string.length() == 0)");
		assertThat(expression.replaceValangKeyWords("string is not blank")).isEqualTo("(string != null && string.length() > 0)");
		assertThat(expression.replaceValangKeyWords("string is uppercase string is upper case string is upper"))
				.isEqualTo("(string.equals(string.toUpperCase())) (string.equals(string.toUpperCase())) (string.equals(string.toUpperCase()))");
		assertThat(expression.replaceValangKeyWords("string is not uppercase string is not upper case string is not upper"))
				.isEqualTo("(!string.equals(string.toUpperCase())) (!string.equals(string.toUpperCase())) (!string.equals(string.toUpperCase()))");
		assertThat(expression.replaceValangKeyWords("string is lowercase string is lower case string is lower"))
				.isEqualTo("(string.equals(string.toLowerCase())) (string.equals(string.toLowerCase())) (string.equals(string.toLowerCase()))");
		assertThat(expression.replaceValangKeyWords("string is not lowercase string is not lower case string is not lower"))
				.isEqualTo("(!string.equals(string.toLowerCase())) (!string.equals(string.toLowerCase())) (!string.equals(string.toLowerCase()))");
		assertThat(expression.replaceValangKeyWords("string is word")).isEqualTo("(string.matches('[\\w]+'))");
		assertThat(expression.replaceValangKeyWords("string is not word")).isEqualTo("(!string.matches('[\\w]+'))");
		assertThat(expression.replaceValangKeyWords("1 is between 0 and 2")).isEqualTo("(1 >= 0 && 1 <= 2)");
		assertThat(expression.replaceValangKeyWords("1 is not between 0 and 2")).isEqualTo("(1 < 0 || 1 > 2)");

		// list
		assertThat(expression.replaceValangKeyWords("x in 1,'2', 3")).isEqualTo("({1,'2', 3}.contains(x))");
		assertThat(expression.replaceValangKeyWords("x not in 1,'2', 3")).isEqualTo("(!{1,'2', 3}.contains(x))");
		assertThat(expression.replaceValangKeyWords("a equals b a is b a = b")).isEqualTo("a == b a == b a == b");
	}
}
