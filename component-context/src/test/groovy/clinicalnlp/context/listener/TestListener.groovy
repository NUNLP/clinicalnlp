package clinicalnlp.context.listener

import gov.va.vinci.leo.listener.BaseListener
import org.apache.uima.cas.CAS
import org.apache.uima.collection.EntityProcessStatus
import org.apache.uima.jcas.cas.TOP

class TestListener extends BaseListener {
    public String typeName;
    public Collection<String> collected = [];

    @Override
    void initializationComplete(EntityProcessStatus aStatus) {
        super.initializationComplete(aStatus)
    }

    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);
        Class<TOP> CollectClass = Class.forName(this.typeName)
        aCas.getJCas().select(type:CollectClass).each {
            this.collected << it.coveredText
        }
    }

    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus)
    }
}
