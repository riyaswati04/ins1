package com.ia.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.ia.processing.ReadOutFile;
import com.ia.processing.impl.ReadOutFileImpl;
import com.ia.util.Digest;
import com.ia.util.KeyReader;
import com.ia.util.XMLCompacter;
import com.ia.util.impl.DigestImpl;
import com.ia.util.impl.KeyReaderImpl;
import com.ia.util.impl.XMLCompacterImpl;

public class UtilsModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(KeyReader.class).to(KeyReaderImpl.class).in(Singleton.class);

        bind(XMLCompacter.class).to(XMLCompacterImpl.class).in(Singleton.class);

        bind(Digest.class).to(DigestImpl.class).in(Singleton.class);

        bind(ReadOutFile.class).to(ReadOutFileImpl.class).in(Singleton.class);
    }

}
