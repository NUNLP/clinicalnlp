package clinicalnlp.dict.trie

import clinicalnlp.dict.DictEntry
import clinicalnlp.dict.DictModel
import clinicalnlp.dict.DictModelFactory
import clinicalnlp.dict.TokenMatch
import clinicalnlp.dict.stringdist.DynamicStringDist
import clinicalnlp.dict.stringdist.Match
import clinicalnlp.dict.stringdist.MinEditDist

class TrieDictModel implements DictModel {

	// ------------------------------------------------------------------------
	// Inner Classes
	// ------------------------------------------------------------------------
	
	private static class Node implements Comparable {
		char c
		DictEntry value
		@SuppressWarnings("rawtypes")
		Node[] next = new Node[0]
		
		@Override
		int compareTo(Object other) {
			if (other instanceof Node) {
				return Character.compare(c, ((Node)other).c)
			}
			else if (other instanceof Character) {
				return Character.compare(c, (Character)other)
			}
		}
	}	
	
	private static class SearchState {
		Node node
		int index = 0
		SearchState(Node node) { this.node = node }
	}
	
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------

	Node root = new Node<>()
	int numEntries = 0

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------
	
	@Override
	Integer getNumEntries() { return numEntries }

	@Override	
	Set<DictEntry> get (final Collection<CharSequence> tokens) {
		// TODO: modify trie structure to allow for multiple DictEntry instances, allowing for ambiguity in dictionary
		return [(getNode(root,DictModelFactory.join(tokens), 0))?.value] as Set<DictEntry>
	}
	
	private Node getNode(Node node, CharSequence key, int index) {
		if (index == key.length()) { return node }
		char c = key.charAt(index)
		int idx = Arrays.binarySearch(node.next, c)
		return (idx < 0 ? null : getNode(node.next[idx], key, index+1))
	}
	
	@Override
	void put(final Collection<CharSequence> keyTokens, final DictEntry value) {
		root = putNode(root, DictModelFactory.join(keyTokens), value, 0)
	}

	@Override
	void complete() {}
			
	private Node putNode(Node node, CharSequence key, DictEntry value, int index) {
		if (index == key.length()) {
			if (node.value == null) { this.numEntries++ }
			node.value = value
			return node
		}
		char c = key.charAt(index)
		int idx = Arrays.binarySearch(node.next, c)
		if (idx < 0) {
			Node[] newNext = Arrays.copyOf(node.next, node.next.length+1)
			newNext[node.next.length] = new Node(c:c, value:null)
			Arrays.sort(newNext)
			node.next = newNext
			idx = Arrays.binarySearch(node.next, c)
		}
		node.next[idx] = putNode(node.next[idx], key, value, index+1)
		return node
	}


	@Override
	TreeSet<TokenMatch> matches(final List<CharSequence> tokens,
								final Float tolerance,
								final Integer maxDistance) {

		DynamicStringDist dist = new MinEditDist()

		Set<TokenMatch> matches = new TreeSet<>()

		// initialize string distance instance with tokens
		dist.set(tokens)
		
		// traverse trie to find matches
		Stack<SearchState> agenda = new Stack<>()
		agenda.push(new SearchState(this.root))
		while (!agenda.isEmpty()) {
			SearchState topSS = agenda.peek()

            // if the top search state is exhausted, pop it off the agenda
			if (topSS.index >= topSS.node.next.length) { 
				dist.pop(); agenda.pop()
			}
			// evaluate top search state
			else if (dist.push(topSS.node.next[topSS.index].c) > maxDistance) {
				dist.pop()
			}
			// examine current search state and look for matches
			else {
				Node nextNode = topSS.node.next[topSS.index]
				agenda.push(new SearchState(nextNode))
				if (nextNode.value != null) {
					Collection strm = dist.matches(maxDistance)
					for (Match m : strm) {
                        Float normScore = m.score/m.matchString.length()
                        if (normScore <= tolerance) {
                            matches.add(new TokenMatch(begin:m.begin,
                                end:m.end, score:normScore,
                                value:nextNode.value))
                        }
					}
				}
			}
			// advance index to next child state
			topSS.index++
		}

		// return matches with scores inside tolerance
		return matches
	}
}
