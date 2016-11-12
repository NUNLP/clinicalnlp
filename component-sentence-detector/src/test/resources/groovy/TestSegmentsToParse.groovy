import clinicalnlp.types.Segment

segs = jcas.select(type:Segment, filter:{ it.code in ['FINAL_DIAGNOSIS'] })

