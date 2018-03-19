package com.ia.core.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.jooq.ConnectionProvider;
import org.jooq.exception.DataAccessException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ia.common.IARuntimeException;

@Singleton
public final class ConnectionProviderImpl implements ConnectionProvider {

    private final DataSource dataSource;

    @Inject
    public ConnectionProviderImpl(final DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    @Override
    public Connection acquire() throws DataAccessException {
        try {
            return dataSource.getConnection();

        }
        catch (final SQLException e) {
            throw new IARuntimeException("SQLException acquiring connection", e);
        }
    }

    @Override
    public void release(final Connection connection) throws DataAccessException {
        try {
            connection.close();
        }
        catch (final SQLException ignore) {
        }

    }

}
