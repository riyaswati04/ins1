package com.ia.insightsStatementUploadBeans;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Institutions")
public class StatementInstitutionResponse {

    @XStreamAlias("Institution")
    @XStreamImplicit
    private List<StatementInstitutionXml> institution;

    public List<StatementInstitutionXml> getInstitution() {
        return institution;
    }

    public void setInstitution(final List<StatementInstitutionXml> institution) {
        this.institution = institution;
    }

}
