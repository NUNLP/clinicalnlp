package clinicalnlp.dict

class DictModelPool {
	private static Map<Integer, DictModel> dicts;
	
	static {
		dicts = new HashMap<>()
	}

	public synchronized static void put(Integer id, DictModel dict) {
		dicts[id] = dict
	}

	public synchronized static DictModel get(Integer id) {
		return dicts[id]
	}
}
