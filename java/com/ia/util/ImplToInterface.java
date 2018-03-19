package com.ia.util;

import com.google.common.base.Function;

public final class ImplToInterface<Interface, Impl extends Interface>
        implements Function<Impl, Interface> {
    @Override
    public Interface apply(final Impl instance) {
        return instance;
    }
}
