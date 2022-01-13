package restful.database;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.cfg.NamingStrategy;

public class DMServer extends ImprovedNamingStrategy {

    public static final NamingStrategy INSTANCE = new DMServer();

    private static final long serialVersionUID = 1383021413247872469L;

    @Override
    public String tableName(String tableName) {
        return tableName;
    }

    @Override
    public String columnName(String columnName) {
        return columnName;
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return propertyName;
    }

    @Override
    public String joinKeyColumnName(String joinedColumn, String joinedTable) {
        return super.joinKeyColumnName(joinedColumn, joinedTable);
    }
}
