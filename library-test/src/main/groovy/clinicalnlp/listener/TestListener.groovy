package clinicalnlp.listener

import gov.va.vinci.leo.listener.BaseListener
import org.apache.uima.cas.CAS
import org.apache.uima.collection.EntityProcessStatus

class TestListener extends BaseListener {
    public String typeName;
    public Collection<String> collected = [];

    @Override
    void initializationComplete(EntityProcessStatus aStatus) {
        super.initializationComplete(aStatus)
    }

    @Override
    void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);
        Class CollectClass = Class.forName(this.typeName)
        aCas.getJCas().select(type:CollectClass).each {
            this.collected << it.coveredText
        }
    }

    @Override
    void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus)
    }
}