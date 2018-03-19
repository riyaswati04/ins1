package com.ia.util.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jdom.output.Format.getCompactFormat;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.google.inject.Singleton;
import com.ia.util.XMLCompacter;

@Singleton
public final class XMLCompacterImpl implements XMLCompacter {

    private final XMLOutputter xmlOutputter;

    public XMLCompacterImpl() {
        super();
        xmlOutputter = new XMLOutputter(createFormat());
    }

    @Override
    public String compact(final Document document) {

        /* Sanity check. */
        checkArgument(null != document);

        /* Format it to a compact, space-normalised string */
        return xmlOutputter.outputString(document.getRootElement());
    }

    private Format createFormat() {
        return getCompactFormat().setLineSeparator("");
    }

}
