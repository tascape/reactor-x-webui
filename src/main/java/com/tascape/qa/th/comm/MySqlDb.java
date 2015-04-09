package com.tascape.qa.th.comm;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class MySqlDb extends EntityCommunication {
    private static final Logger LOG = LoggerFactory.getLogger(MySqlDb.class);

    public static final String TEMP_TABLE_NAME_SUFFIX = "_TEMP_TBL_TH_SUFFIX";

    private final String dbUrl;

    private final String dbInfo;

    private final String user;

    private final String password;

    private boolean readOnly = false;

    private String schemaName;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("Cannot load database driver", ex);
        }
    }

    /**
     *
     * @param dbUrl    such as "jdbc:mysql://localhost:3570/testharness");
     * @param user
     * @param password
     */
    public MySqlDb(String dbUrl, String user, String password) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;

        this.dbInfo = dbUrl.split("\\?")[0];
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public void disconnect() throws IOException {
    }

    public boolean tableExists(String tableName) throws SQLException {
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "';";
            LOG.debug("{} executing query: {}", this.dbInfo, sql);
            ResultSet rs = stmt.executeQuery(sql);
            return rs.next() && rs.getBoolean("COUNT(*)");
        }
    }

    /**
     * Replicates table structure and data from source to temporary table.
     *
     * @param tableName
     *
     * @return
     *
     * @throws SQLException
     */
    public String replicateTable(String tableName) throws SQLException {
        return this.replicateTable(tableName, true);
    }

    /**
     * Replicates table structure and data from source to temporary table.
     *
     * @param tableName
     * @param toCopyData
     *
     * @return
     *
     * @throws SQLException
     */
    public String replicateTable(String tableName, boolean toCopyData) throws SQLException {
        String tempTableName = MySqlDb.getTempTableName(tableName);
        LOG.debug("Replicate table {} into {}", tableName, tempTableName);
        if (this.tableExists(tempTableName)) {
            this.truncateTempTable(tempTableName);
        } else {
            this.createTempTable(tableName);
        }
        if (toCopyData) {
            final String sql = "INSERT INTO " + tempTableName + " SELECT * FROM " + tableName + ";";
            this.executeUpdate(sql);
        }
        return tempTableName;
    }

    /**
     * Restores table content.
     *
     * @param tableName     table to be restored
     * @param tempTableName
     *
     * @throws SQLException
     */
    public void restoreTable(String tableName, String tempTableName) throws SQLException {
        LOG.debug("Restore table {} from {}", tableName, tempTableName);
        try {
            this.setForeignKeyCheckEnabled(false);
            this.truncateTable(tableName);
            final String sql = "INSERT INTO " + tableName + " SELECT * FROM " + tempTableName + ";";
            this.executeUpdate(sql);
        } finally {
            this.setForeignKeyCheckEnabled(true);
        }
    }

    /**
     * Drops a temporary table.
     *
     * @param tempTableName
     *
     * @throws SQLException
     */
    public void removeTempTable(String tempTableName) throws SQLException {
        if (tempTableName == null) {
            return;
        }
        if (!tempTableName.contains(TEMP_TABLE_NAME_SUFFIX)) {
            throw new SQLException(tempTableName + " is not a valid temp table name");
        }
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            final String sql = "DROP TABLE " + tempTableName + ";";
            LOG.debug("{} executing update: {}", this.dbInfo, sql);
            int row = stmt.executeUpdate(sql);
        }
    }

    public void createTempTable(String tableName) throws SQLException {
        String tempTableName = MySqlDb.getTempTableName(tableName);
        final String sql = "CREATE TABLE " + tempTableName + " LIKE " + tableName + ";";
        this.executeUpdate(sql);
    }

    public void truncateTempTable(String tempTableName) throws SQLException {
        if (tempTableName == null) {
            return;
        }
        if (!tempTableName.contains(TEMP_TABLE_NAME_SUFFIX)) {
            throw new SQLException(tempTableName + " is not a valid temp table name");
        }
        this.truncateTable(tempTableName);
    }

    public void truncateTable(String tableName) throws SQLException {
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            final String sql = "TRUNCATE TABLE " + tableName + ";";
            LOG.debug("{} executing update: {}", this.dbInfo, sql);
            int row = stmt.executeUpdate(sql);
        }
    }

    public void renameTable(String tableNameOld, String tableNameNew) throws SQLException {
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            final String sql = "ALTER TABLE " + tableNameOld + " RENAME TO " + tableNameNew + ";";
            LOG.debug("{} executing update: {}", this.dbInfo, sql);
            int row = stmt.executeUpdate(sql);
        }
    }

    /**
     *
     * @param sql
     *
     * @return
     *
     * @throws SQLException
     */
    public List<Map<String, Object>> dumpQueryResultSetToList(String sql) throws SQLException {
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            LOG.debug("{} executing query: {}", this.dbInfo, sql);
            ResultSet rs = stmt.executeQuery(sql);
            return this.dumpQueryResultSetToList(rs);
        }
    }

    /**
     *
     * @param stmt
     *
     * @return
     *
     * @throws SQLException
     */
    public List<Map<String, Object>> dumpQueryResultSetToList(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery();) {
            LOG.debug("{} executing query: {}", this.dbInfo, this.getSql(stmt));
            return this.dumpQueryResultSetToList(rs);
        }
    }

    public void populateTempTable(String tempTableName, List<Map<String, Object>> rows) throws SQLException {
        if (tempTableName == null) {
            return;
        }
        if (!tempTableName.contains(TEMP_TABLE_NAME_SUFFIX)) {
            throw new SQLException(tempTableName + " is not a valid temp table name");
        }
        final String sql = "SELECT * FROM " + tempTableName;
        try (Connection conn = this.getConn();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            LOG.debug("{} executing query: {}", this.dbInfo, sql);
            ResultSet rs = stmt.executeQuery(sql);
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                int index = 0;
                for (Map<String, Object> row : rows) {
                    rs.moveToInsertRow();
                    for (String col : row.keySet()) {
                        rs.updateObject(col, row.get(col));
                    }
                    rs.insertRow();
                    rs.updateRow();
                    if (index++ % 200 == 0) {
                        conn.commit();
                    }
                }
                conn.commit();
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        }
    }

    /**
     * @return such as jdbc:mysql://eng005:3570/marketplace
     */
    public String getDbUrl() {
        return this.dbUrl;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public PreparedStatement prepareUpdatableStatement(String sql) throws SQLException {
        return this.getConn().prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    public List<Map<String, Object>> executeUpdate(PreparedStatement stmt) throws SQLException {
        LOG.debug("{} executing update {}", this.dbInfo, this.getSql(stmt));
        if (this.readOnly) {
            throw new SQLException("Database is read only");
        }
        int rows = stmt.executeUpdate();
        LOG.debug("{} row{} affected", rows, rows > 1 ? "s" : "");
        try (ResultSet rs = stmt.getGeneratedKeys();) {
            return this.dumpQueryResultSetToList(rs);
        }
    }

    public void executeUpdate(String sql) throws SQLException {
        LOG.debug("{} executing update {}", this.dbInfo, sql);
        if (this.readOnly) {
            throw new SQLException("Database is read only");
        }
        try (Connection conn = this.getConn(); Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            LOG.debug("{} row{} affected", rows, rows > 1 ? "s" : "");
        }
    }

    public ResultSet executeQuery(PreparedStatement stmt) throws SQLException {
        LOG.debug("{} executing query {}", this.dbInfo, this.getSql(stmt));
        return stmt.executeQuery();
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        LOG.debug("{} executing query {}", this.dbInfo, sql);
        Statement stmt = this.getConn().createStatement();
        return stmt.executeQuery(sql);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static String getTempTableName(String tableName) {
        if (tableName.endsWith(TEMP_TABLE_NAME_SUFFIX)) {
            return tableName;
        }
        return tableName + TEMP_TABLE_NAME_SUFFIX;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    private Connection getConn() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("useJDBCCompliantTimezoneShift", "true");
        properties.setProperty("useLegacyDatetimeCode", "false");
        properties.setProperty("user", this.user);
        properties.setProperty("password", this.password);

        return DriverManager.getConnection(this.dbUrl, properties);
    }

    private List<Map<String, Object>> dumpQueryResultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> rsml = new ArrayList<>();
        if (rs != null) {
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> d = new LinkedHashMap<>();
                for (int col = 1; col <= rsmd.getColumnCount(); col++) {
                    d.put(rsmd.getColumnLabel(col), rs.getObject(col));
                }
                rsml.add(d);
            }
            LOG.trace("{} row{} loaded", rsml.size(), rsml.size() > 1 ? "s" : "");
        }
        return rsml;
    }

    private void setForeignKeyCheckEnabled(boolean enabled) throws SQLException {
        String sql = "SET GLOBAL FOREIGN_KEY_CHECKS = " + (enabled ? 1 : 0) + ";";
        this.executeUpdate(sql);
    }

    private String getSql(Statement stmt) {
        String sql = stmt.toString();
        int index = sql.indexOf(": ");
        index = index < 0 ? 0 : index + 2;
        return sql.substring(index);
    }
}
