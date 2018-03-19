package com.ia.core.database;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.google.inject.Provider;

@Singleton
public final class DSLContextProvider implements Provider<DSLContext> {

    private final ConnectionProvider connectionProvider;

    private final SQLDialect sqlDialect;

    @Inject
    public DSLContextProvider(final ConnectionProvider connectionProvider,
            final SQLDialect sqlDialect) {
        super();
        this.connectionProvider = connectionProvider;
        this.sqlDialect = sqlDialect;
    }

    @Override
    public DSLContext get() {
        return DSL.using(connectionProvider, sqlDialect);
    }

}
