package com.ia.insightsStatementUploadBeans;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Institution")
public class StatementInstitutionXml {

    // <Institution>
    // <addressAvailable>false</addressAvailable>
    // <id>46</id>
    // <institutionType>bank</institutionType>
    // <name>Yes Bank, India</name>
    // </Institution>

    private int id;

    private String name;

    private String institutionType;

    private String addressAvailable;

    public String getAddressAvailable() {
        return addressAvailable;
    }

    public int getId() {
        return id;
    }

    public String getInstitutionType() {
        return institutionType;
    }

    public String getName() {
        return name;
    }

    public void setAddressAvailable(final String addressAvailable) {
        this.addressAvailable = addressAvailable;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setInstitutionType(final String institutionType) {
        this.institutionType = institutionType;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
