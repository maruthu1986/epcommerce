/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package liquibase.ext.elasticpath;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.ValidationErrors;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;

/**
 * Custom liquibase change that converts valang expression to spring expression.
 */
public class ConvertValangToSpringExpression  implements CustomSqlChange {

	private static final Logger LOG = LogFactory.getInstance().getLog();

	private static final int YEAR_GROUP = 1;
	private static final int MONTH_GROUP = 2;
	private static final int DATE_GROUP = 3;
	private static final int GROUP_NUM_FOR_OPTIONAL_DATA = 4;
	private static final int MAX_NUM_OF_DATA = 7;

	private String tableName;
	private String columnName;
	private String primaryKey;

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public void setColumnName(final String columnName) {
		this.columnName = columnName;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	@Override
	public SqlStatement[] generateStatements(final Database database) throws CustomChangeException {
		if (!(database.getConnection() instanceof JdbcConnection)) {
			throw new UnexpectedLiquibaseException("Unable to get connection from database");
		}
		// prevent SQL injection
		if (!tableName.matches("[\\w-]+")) {
			throw new CustomChangeException("Table name must only contain word characters or a dash [a-zA-Z_0-9-]");
		}
		JdbcConnection connection = (JdbcConnection) database.getConnection();
		try {
			try (Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
				try (ResultSet resultSet = statement.executeQuery(String.format("SELECT %s, %s FROM %s", primaryKey, columnName, tableName))) {
					while (resultSet.next()) {
						String valang = resultSet.getString(columnName);
						String replacement = valang.substring(1, valang.length() - 1).split("\\s*:\\s*")[1];
						replacement = replaceValangKeyWords(replacement);
						resultSet.updateString(columnName, replacement);
						resultSet.updateRow();
						LOG.info(String.format("Successfully updated %s to %s\n", valang, replacement));
					}
				}
			}
		} catch (DatabaseException | SQLException e) {
			throw new CustomChangeException(e);
		}

		return new SqlStatement[0];
	}

	String replaceValangKeyWords(final String valang) {
		return replaceValangDate(valang)
				// len(val) , size(val), count(val) -> (#length(val))
				.replaceAll("(?<![^\\s(),])(len|size|count)(\\([\\w'.]+\\))(?![^\\s(),])", "(#length$2)")
				// matches('\\s+', val), match("\s+", val) -> (val.matches("\s"))
				.replaceAll("(?<![^\\s(),])(matches|match)\\(('.+')\\s*,\\s*([\\w'.]+)\\)(?![^\\s(),])", "($3.matches($2))")
				// upper(val) -> (val.toUpperCase())
				.replaceAll("(?<![^\\s(),])upper\\(([\\w'.]+)\\)(?![^\\s(),])", "($1.toUpperCase())")
				// lower(val) -> (val.toLowerCase())
				.replaceAll("(?<![^\\s(),])lower\\(([\\w'.]+)\\)(?![^\\s(),])", "($1.toLowerCase())")
				// WHERE -> &&
				.replaceAll("(?i) WHERE ", " && ")
				// =>, GREATER THAN OR EQUALS, IS GREATER THAN OR EQUALS -> >=
				.replaceAll("(?i) (=>|GREATER\\s+THAN\\s+OR\\s+EQUALS|IS\\s+GREATER\\s+THAN\\s+OR\\s+EQUALS) ", " >= ")
				// =<, LESS THAN OR EQUALS, IS LESS THAN OR EQUALS -> <=
				.replaceAll("(?i) (=<|LESS\\s+THAN\\s+OR\\s+EQUALS|IS\\s+LESS\\s+THAN\\s+OR\\s+EQUALS) ", " <= ")
				// GREATER THAN, IS GREATER THAN -> >
				.replaceAll("(?i) (GREATER\\s+THAN|IS\\s+GREATER\\s+THAN) ", " > ")
				// LESS THAN, IS LESS THAN -> <
				.replaceAll("(?i) (LESS\\s+THAN|IS\\s+LESS\\s+THAN) ", " < ")
				// val HAS TEXT -> (val.matches("\s*[^\s]+\s*"))
				.replaceAll("(?i)([\\w_']+)\\s+HAS\\s+TEXT(?![^\\s])", "($1.matches(\\\\s*[^\\\\s]+\\\\s*))")
				// val HAS NO TEXT -> (!val.matches("\s*[^\s]+\s*"))
				.replaceAll("(?i)([\\w_']+)\\s+HAS\\s+NO\\s+TEXT(?![^\\s])", "(!$1.matches(\\\\s*[^\\\\s]+\\\\s*))")
				// val HAS LENGTH -> (val.length() > 0)
				.replaceAll("(?i)([\\w_']+)\\s+HAS\\s+LENGTH(?![^\\s])", "($1.length() > 0)")
				// val HAS NO LENGTH -> (val.length() == 0)
				.replaceAll("(?i)([\\w_']+)\\s+HAS\\s+NO\\s+LENGTH(?![^\\s])", "($1.length() == 0)")
				// val IS BLANK -> (val == null || val.length() == 0)
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+BLANK(?![^\\s])", "($1 == null || $1.length() == 0)")
				// val IS NOT BLANK -> (val != null && val.length() > 0)
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+NOT\\s+BLANK(?![^\\s])", "($1 != null && $1.length() > 0)")
				// val IS UPPERCASE, val IS UPPER CASE, val IS UPPER -> (val.equals(val.toUpperCase()))
				.replaceAll("(?i)([\\w_']+)\\s+(IS\\s+UPPERCASE|IS\\s+UPPER\\s+CASE|IS\\s+UPPER)(?![^\\s])", "($1.equals($1.toUpperCase()))")
				// val IS NOT UPPERCASE, val IS NOT UPPER CASE, val IS NOT UPPER -> (!val.equals(val.toUpperCase()))
				.replaceAll("(?i)([\\w_']+)\\s+(IS\\s+NOT\\s+UPPERCASE|IS\\s+NOT\\s+UPPER\\s+CASE|IS\\s+NOT\\s+UPPER)(?![^\\s])",
						"(!$1.equals($1.toUpperCase()))")
				// val IS LOWERCASE, val IS LOWER CASE, val IS LOWER -> (val.equals(val.toLowerCase())
				.replaceAll("(?i)([\\w_']+)\\s+(IS\\s+LOWERCASE|IS\\s+LOWER\\s+CASE|IS\\s+LOWER)(?![^\\s])", "($1.equals($1.toLowerCase()))")
				// val IS NOT LOWERCASE, val IS NOT LOWER CASE, val IS NOT LOWER -> (!val.equals(val.toLowerCase())
				.replaceAll("(?i)([\\w_']+)\\s+(IS\\s+NOT\\s+LOWERCASE|IS\\s+NOT\\s+LOWER\\s+CASE|IS\\s+NOT\\s+LOWER)(?![^\\s])",
						"(!$1.equals($1.toLowerCase()))")
				// val IS WORD -> (val.matches('[\w]+'))
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+WORD", "($1.matches('[\\\\w]+'))")
				// val IS NOT WORD -> (!val.matches('[\w]+'))
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+NOT\\s+WORD", "(!$1.matches('[\\\\w]+'))")
				// val IS BETWEEN 12 AND 14 -> (val >= 12 && val <= 14)
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+BETWEEN\\s+([\\w_'-]+)\\s+AND\\s+([\\w'-]+)", "($1 >= $2 && $1 <= $3)")
				// val IS NOT BETWEEN 12 AND 14 -> (val < 12 || val > 14)
				.replaceAll("(?i)([\\w_']+)\\s+IS\\s+NOT\\s+BETWEEN\\s+([\\w_'-]+)\\s+AND\\s+([\\w'-]+)", "($1 < $2 || $1 > $3)")
				// val NOT IN 1, 'df', 4 -> (!{1, 'df', 4}.contains(val))
				.replaceAll("(?i)([\\w_']+)\\s+NOT\\s+IN\\s+(['\\w\\s]+(,\\s*['\\w]+)*)", "(!{$2}.contains($1))")
				// val IN 1, 'df', 4 -> ({1, 'df', 4}.contains(val))
				.replaceAll("(?i)([\\w_']+)\\s+IN\\s+(['\\w\\s]+(,\\s*['\\w]+)*)", "({$2}.contains($1))")
				// <>, ><, IS NOT, NOT EQUALS -> !=
				.replaceAll("(?i) (<>|><|IS\\s+NOT|NOT\\s+EQUALS) ", " != ")
				// EQUALS, IS, = -> ==
				.replaceAll("(?i) (EQUALS|IS|=) ", " == ")
				// function(arg1, arg1.var, 'string') -> (#function(arg1, arg1.var, 'string'))
				.replaceAll("(?<![^\\s(),])([\\w_]+\\([\\w'.]+(,\\s*[\\w'.]+)*\\))(?![^\\s(),])", "(#$1)")
				// this -> (#this)
				.replaceAll("(?i)(?<![^\\s,(])(this)(?![^\\s,)])", "(#$1)")
				// #typeof(val, 'java.lang.Integer'), typeof(val, 'java.lang.Integer') -> (val instanceof T(java.lang.Integer))
				.replaceAll("(?<![^\\s(),])#?typeof\\(([\\w'.]+)\\s*,\\s*'([\\w.]+)'\\)(?![^\\s(),])", "$1 instanceof T($2)");
	}

	// [yyyyMMdd], [yyyy-MM-dd] -> new java.util.Date(yyyy,MM - 1,dd)
	// [yyyy-MM-dd HH:mm:ss], [yyyyMMdd HHmmss], [yyyyMMdd HH:mm:ss], [yyyy-MM-dd HHmmss] -> new java.util.Date(yyyy,MM - 1,dd,HH,mm,ss)
	private String replaceValangDate(final String valang) {
		String newValang = valang;
		Pattern pattern = Pattern.compile("\\[([0-9]{4})-?([0-9]{2})-?([0-9]{2})(\\s+([0-9]{2}):?([0-9]{2}):?([0-9]{2}))?\\]");
		Matcher matcher = pattern.matcher(newValang);
		while (matcher.find()) {
			StringBuilder replacement = new StringBuilder("(new java.util.Date(");
			replacement.append(matcher.group(YEAR_GROUP)).append(',').append(matcher.group(MONTH_GROUP)).append(" - 1,") // need to decrement month
					.append(matcher.group(DATE_GROUP));
			if (matcher.group(GROUP_NUM_FOR_OPTIONAL_DATA) != null) {
				replacement.append(',');
				for (int i = GROUP_NUM_FOR_OPTIONAL_DATA + 1; i <= MAX_NUM_OF_DATA; i++) {
					replacement.append(matcher.group(i)).append(',');
				}
				replacement.deleteCharAt(replacement.length() - 1);
			}
			replacement.append("))");
			newValang = newValang.replace(matcher.group(0), replacement);
		}
		return newValang;
	}

	@Override
	public String getConfirmationMessage() {
		return String.format("Finished updating table %s\n", tableName);
	}

	@Override
	public void setUp() throws SetupException {
		// no setup
	}

	@Override
	public void setFileOpener(final ResourceAccessor resourceAccessor) {
		// not used
	}

	@Override
	public ValidationErrors validate(final Database database) {
		return null; // no validation needed
	}
}
