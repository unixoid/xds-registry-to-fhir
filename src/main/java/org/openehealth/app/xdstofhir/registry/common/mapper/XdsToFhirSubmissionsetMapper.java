package org.openehealth.app.xdstofhir.registry.common.mapper;

import java.util.Collections;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Identifier;
import org.openehealth.app.xdstofhir.registry.common.MappingSupport;
import org.openehealth.app.xdstofhir.registry.common.fhir.MhdSubmissionSet;
import org.openehealth.ipf.commons.ihe.xds.core.metadata.SubmissionSet;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XdsToFhirSubmissionsetMapper extends AbstractXdsToFhirMapper
        implements Function<SubmissionSet, MhdSubmissionSet> {
    @Override
    public MhdSubmissionSet apply(SubmissionSet xdsSub) {
        var mhdList = new MhdSubmissionSet();
        mhdList.addIdentifier(fromIdentifier(xdsSub.getEntryUuid(), Identifier.IdentifierUse.OFFICIAL));
        mhdList.addIdentifier(
                fromIdentifier(MappingSupport.OID_URN + xdsSub.getUniqueId(), Identifier.IdentifierUse.USUAL));
        mhdList.setSubject(patientReferenceFrom(xdsSub));
        Identifier sourceId = new Identifier();
        sourceId.setId(MappingSupport.toUrnCoded(xdsSub.getSourceId()));
        mhdList.setSourceId(sourceId);
        mhdList.setDateElement(fromTimestamp(xdsSub.getSubmissionTime()));
        mhdList.setDesignationType(fromCode(xdsSub.getContentTypeCode()));
        if (!xdsSub.getAuthors().isEmpty())
            mhdList.setSource(fromAuthor(xdsSub.getAuthors().get(0)));
        if (xdsSub.getTitle() != null)
            mhdList.setTitle(xdsSub.getTitle().getValue());
        if (xdsSub.getComments() != null) {
            Annotation annotation = new Annotation();
            annotation.setText(xdsSub.getComments().getValue());
            mhdList.setNote(Collections.singletonList(annotation));
        }
        return mhdList;
    }

}