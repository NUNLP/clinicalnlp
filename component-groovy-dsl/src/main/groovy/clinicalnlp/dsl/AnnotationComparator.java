package clinicalnlp.dsl;

import org.apache.uima.jcas.tcas.Annotation;

import java.util.Comparator;

public class AnnotationComparator implements Comparator<Annotation> {

    /**
     * Compare two uima.tcas.Annotation objects or children based on starting and ending indexes.
     *
     * @param o1 first Annotation to compare
     * @param o2 second Annotation to compare
     * @return sort order -1, 0, or 1 if indexes of o1 are less than, equal, or greater than o2
     */
    @Override
    public int compare(Annotation o1, Annotation o2) {
        if(o1 == null || o1 == null)
            throw new NullPointerException("Arguments cannot be null");

        if(o1.getBegin() < o2.getBegin())
            return -1;
        else if (o1.getBegin() > o2.getBegin())
            return 1;
        else if(o1.getBegin() == o2.getBegin()) {
            if(o1.getEnd() > o2.getEnd())
                return -1;
            else if(o1.getEnd() < o2.getEnd())
                return 1;
        }
        return 0;
    }

    /**
     * Compares this Comparator object to other Comparators. In this case we only return true if the class instance
     * to be compared is an AnnotationComparator class or a child of that class.
     *
     * @param obj object to compare
     * @return true if the object is of the same class or subclass
     */
    @Override
    public boolean equals(Object obj) {
        return obj.getClass().isAssignableFrom(AnnotationComparator.class);
    }
}
