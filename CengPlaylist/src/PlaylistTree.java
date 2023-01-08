import java.util.ArrayList;

public class PlaylistTree {
	
	public PlaylistNode primaryRoot;		//root of the primary B+ tree
	public PlaylistNode secondaryRoot;	//root of the secondary B+ tree
	public PlaylistTree(Integer order) {
		PlaylistNode.order = order;
		primaryRoot = new PlaylistNodePrimaryLeaf(null);
		primaryRoot.level = 0;
		secondaryRoot = new PlaylistNodeSecondaryLeaf(null);
		secondaryRoot.level = 0;
	}
	
	public void addSong(CengSong song) {
		// TODO: Implement this method
		// add methods to fill both primary and secondary tree
		PlaylistNode tmp = primaryRoot;
		Integer count, i;
		while(tmp.getType() != PlaylistNodeType.Leaf) {
			count = ((PlaylistNodePrimaryIndex) tmp).audioIdCount();
			for (i = 0; i < count; ++i) {
				if (song.audioId() < ((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i)) {
					tmp = ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i);
					break;
				}
			}
			if(i == count) {
				tmp = ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i);
			}
		}
		count = ((PlaylistNodePrimaryLeaf) tmp).songCount();
		for(i=0 ; i<count ; ++i){
			if(song.audioId() < ((PlaylistNodePrimaryLeaf) tmp).audioIdAtIndex(i)){
				((PlaylistNodePrimaryLeaf) tmp).addSong(i, song);
				break;
			}
		}
		if(i == count){
			((PlaylistNodePrimaryLeaf) tmp).addSong(count, song);
		}

		treeOrderPrimary(tmp);
		levelsPrimary(primaryRoot);

		tmp = secondaryRoot;

		while(tmp.getType() != PlaylistNodeType.Leaf){
			count = ((PlaylistNodeSecondaryIndex) tmp).genreCount();
			for(i=0 ; i<count ; ++i){
				if(song.genre().compareToIgnoreCase(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i)) < 0){
					tmp = ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i);
					break;
				}
			}
			if(i == count){
				tmp = ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i);
			}
		}

		count = ((PlaylistNodeSecondaryLeaf) tmp).genreCount();
		for(i=0 ; i<count ; ++i){
			if(song.genre().compareToIgnoreCase(((PlaylistNodeSecondaryLeaf) tmp).genreAtIndex(i)) <= 0){
				((PlaylistNodeSecondaryLeaf) tmp).addSong(i, song);
				break;
			}
		}
		if(i == count){
			((PlaylistNodeSecondaryLeaf) tmp).addSong(count, song);
		}

		treeOrderSecondary(tmp);
		levelsSecondary(secondaryRoot);
		return;
	}

	public CengSong searchSong(Integer audioId) {
		// TODO: Implement this method
		// find the song with the searched audioId in primary B+ tree
		// return value will not be tested, just print according to the specifications
		StringBuilder output = new StringBuilder();
		if (primaryRoot == null) {
			System.out.println(output.append("Could not find ").append(audioId.toString()).append("."));
			return null;
		}

		ArrayList<PlaylistNode> songs = new ArrayList<>();
		Integer n;
		PlaylistNode tmp = primaryRoot;
		StringBuilder indent = new StringBuilder();
		songs.add(tmp);
		while (tmp.getType() != PlaylistNodeType.Leaf) {
			n = ((PlaylistNodePrimaryIndex) tmp).audioIdCount();
			output.append(indent).append("<index>").append("\n");
			for (int i = 0; i < n ; i++) {
				output.append(indent).append(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i)).append("\n");
			}
			output.append(indent).append("</index>").append("\n");
			if (audioId < ((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(0)) {
				tmp = ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(0);
			}
			else if (((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(n - 1) <= audioId) {
				tmp = ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(n);
			}
			else {
				for (int i = 0; i <= n - 2; i++) {
					if (((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i) <= audioId && audioId < ((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i + 1)) {
						tmp = ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i + 1);
						break;
					}
				}
			}
			indent.append("\t");
			songs.add(tmp);
		}

		n = ((PlaylistNodePrimaryLeaf) tmp).songCount();
		for (int i = 0; i < n; i++) {
			CengSong song = ((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i);
			if (song.audioId().equals(audioId)) {
				output.append(indent).append("<data>").append("\n");
				output.append(indent).append("<record>").append(song.fullName()).append("</record>").append("\n");
				output.append(indent).append("</data>").append("\n");
				System.out.print(output);
				return song;
			}
		}
		System.out.println("Could not find " + audioId.toString() + ".");
		return null;
	}

	
	public void printPrimaryPlaylist() {
		// TODO: Implement this method
		// print the primary B+ tree in Depth-first order
		printPrimaryHelper(primaryRoot, 0);
		return;
	}
	
	public void printSecondaryPlaylist() {
		// TODO: Implement this method
		// print the secondary B+ tree in Depth-first order
		printSecondaryHelper(secondaryRoot, 0);
		return;
	}
	
	// Extra functions if needed

	public void printPrimaryHelper(PlaylistNode tmp, Integer times){
		int count;
		String indent = "\t".repeat(times);
		if (tmp.getType() == PlaylistNodeType.Leaf) {
			count = ((PlaylistNodePrimaryLeaf) tmp).songCount();
			System.out.println(indent + "<data>");

			for (int i=0 ; i<count ; ++i) {
				System.out.println(indent + "<record>" + ((PlaylistNodePrimaryLeaf) tmp).audioIdAtIndex(i).toString() + "|" +
						((PlaylistNodePrimaryLeaf) tmp).songGenreAtIndex(i) + "|" +
						((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i).songName() + "|" +
						((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i).artist() + "</record>");
			}

			System.out.println(indent + "</data>");
			return;
		}
		count = ((PlaylistNodePrimaryIndex) tmp).audioIdCount();
		System.out.println(indent + "<index>");

		for (int i=0 ; i<count ; ++i) {
			System.out.print(indent);
			System.out.println(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
		}

		System.out.println(indent + "</index>");
		for (int i=0 ; i<count+1 ; ++i) {
			printPrimaryHelper(((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i), times+1);
		}
	}

	public void printSecondaryHelper(PlaylistNode tmp, Integer times){
		int count;
		String indent = "\t".repeat(times);
		if (tmp.getType() == PlaylistNodeType.Leaf) {
			count = ((PlaylistNodeSecondaryLeaf) tmp).genreCount();
			System.out.println(indent + "<data>");

			for (int i=0 ; i<count ; ++i) {
				System.out.println(indent + ((PlaylistNodeSecondaryLeaf) tmp).genreAtIndex(i));
				for(int j=0 ; j<((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).size() ; ++j){
					System.out.println(indent + "\t" + "<record>" + ((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j).audioId().toString() + "|" +
							((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j).genre() + "|" +
							((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j).songName() + "|" +
							((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j).artist() + "</record>");
				}
			}

			System.out.println(indent + "</data>");
			return;
		}
		count = ((PlaylistNodeSecondaryIndex) tmp).genreCount();
		System.out.println(indent + "<index>");

		for (int i=0 ; i<count ; ++i) {
			System.out.print(indent);
			System.out.println(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i));
		}

		System.out.println(indent + "</index>");
		for (int i=0 ; i<count+1 ; ++i) {
			printSecondaryHelper(((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i), times+1);
		}
	}

	public void treeOrderPrimary(PlaylistNode tmp){
		Integer count, i;
		count = ((PlaylistNodePrimaryLeaf) tmp).songCount();
		PlaylistNode node;
		ArrayList<Integer> ids;
		PlaylistNode c1, c2;
		while(count > 2*PlaylistNode.order){
			if(primaryRoot.getType() == PlaylistNodeType.Leaf){
				//System.out.println("root is leaf");
				node = new PlaylistNodePrimaryIndex(null);
				c1 = new PlaylistNodePrimaryLeaf(node);
				c2 = new PlaylistNodePrimaryLeaf(node);
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodePrimaryLeaf) c1).addSong(i, ((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i));
				}
				for( ; i<count ; ++i){
					((PlaylistNodePrimaryLeaf) c2).addSong(i-(count-1)/2, ((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i));
				}
				((PlaylistNodePrimaryIndex) node).addChild(0, c1);
				((PlaylistNodePrimaryIndex) node).addChild(1, c2);
				((PlaylistNodePrimaryIndex) node).addAudioId(((PlaylistNodePrimaryLeaf) tmp).audioIdAtIndex((count-1)/2));
				primaryRoot = node;
				break;
			}
			if(tmp.getType() == PlaylistNodeType.Leaf){
				//System.out.println("we are on leaf");
				node = tmp.getParent();
				c1 = new PlaylistNodePrimaryLeaf(node);
				c2 = new PlaylistNodePrimaryLeaf(node);
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodePrimaryLeaf) c1).addSong(i, ((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i));
				}
				for( ; i<count ; ++i){
					((PlaylistNodePrimaryLeaf) c2).addSong(i-(count-1)/2, ((PlaylistNodePrimaryLeaf) tmp).songAtIndex(i));
				}

				((PlaylistNodePrimaryIndex) node).addAudioId(((PlaylistNodePrimaryLeaf) c2).audioIdAtIndex(0));
				for(i=0 ; i<((PlaylistNodePrimaryIndex) node).getAllChildren().size() ; ++i){
					if(tmp == ((PlaylistNodePrimaryIndex) node).getChildrenAt(i)){
						break;
					}
				}
				((PlaylistNodePrimaryIndex) node).removeChild(i);
				((PlaylistNodePrimaryIndex) node).addChild(i, c1);
				((PlaylistNodePrimaryIndex) node).addChild(i+1, c2);
				tmp = node;
				//System.out.println(((PlaylistNodePrimaryIndex) node).getAllChildren().size());
				//System.out.println(((PlaylistNodePrimaryIndex) node).audioIdCount());
			}else if(tmp.getParent() == null){
				//System.out.println("parent is null");
				node = new PlaylistNodePrimaryIndex(null);
				c1 = new PlaylistNodePrimaryIndex(node);
				c2 = new PlaylistNodePrimaryIndex(node);
				((PlaylistNodePrimaryIndex) node).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex((count-1)/2));
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodePrimaryIndex) c1).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
				}
				for(i=(count+1)/2 ; i<count ; ++i){
					((PlaylistNodePrimaryIndex) c2).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
				}
				for(i=0 ; i<((PlaylistNodePrimaryIndex) tmp).getAllChildren().size()/2 ; ++i){
					((PlaylistNodePrimaryIndex) c1).addChild(i, ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i).setParent(c1);
				}
				for( ; i<((PlaylistNodePrimaryIndex) tmp).getAllChildren().size() ; ++i){
					((PlaylistNodePrimaryIndex) c2).addChild(i-((PlaylistNodePrimaryIndex) tmp).getAllChildren().size()/2, ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i).setParent(c2);
				}
				((PlaylistNodePrimaryIndex) node).addChild(0, c1);
				((PlaylistNodePrimaryIndex) node).addChild(1, c2);
				//System.out.println(((PlaylistNodePrimaryIndex) node).getAllChildren().size());
				//System.out.println(((PlaylistNodePrimaryIndex) node).audioIdCount());
				primaryRoot = node;
				break;
			}else{
				//System.out.println("we still have way to go");
				node = tmp.getParent();
				c1 = new PlaylistNodePrimaryIndex(node);
				c2 = new PlaylistNodePrimaryIndex(node);
				((PlaylistNodePrimaryIndex) node).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex((count-1)/2));
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodePrimaryIndex) c1).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
				}
				for(i=(count+1)/2 ; i<count ; ++i){
					((PlaylistNodePrimaryIndex) c2).addAudioId(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
				}
				for(i=0 ; i<((PlaylistNodePrimaryIndex) tmp).getAllChildren().size()/2 ; ++i){
					((PlaylistNodePrimaryIndex) c1).addChild(i, ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i).setParent(c1);
				}
				for( ; i<((PlaylistNodePrimaryIndex) tmp).getAllChildren().size() ; ++i){
					((PlaylistNodePrimaryIndex) c2).addChild(i-((PlaylistNodePrimaryIndex) tmp).getAllChildren().size()/2, ((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i).setParent(c2);
				}
				for(i=0 ; i<((PlaylistNodePrimaryIndex) node).getAllChildren().size() ; ++i){
					if(tmp == ((PlaylistNodePrimaryIndex) node).getChildrenAt(i)){
						break;
					}
				}
				((PlaylistNodePrimaryIndex) node).removeChild(i);
				((PlaylistNodePrimaryIndex) node).addChild(i, c1);
				((PlaylistNodePrimaryIndex) node).addChild(i+1, c2);
				tmp = node;
			}

			count = ((PlaylistNodePrimaryIndex) tmp).audioIdCount();
			/*for(i=0 ; i<count ; ++i){
				System.out.println(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
			}*/
		}
	}

	public void levelsPrimary(PlaylistNode tmp){
		if(tmp == primaryRoot){
			tmp.level = 0;
		}
		if(tmp.getType() == PlaylistNodeType.Internal){
			for(int i=0 ; i<((PlaylistNodePrimaryIndex) tmp).audioIdCount() ; ++i){
				((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i).level = tmp.level+1;
				levelsPrimary(((PlaylistNodePrimaryIndex) tmp).getChildrenAt(i));
			}
		}
	}

	public void treeOrderSecondary(PlaylistNode tmp){
		Integer count, i, j;
		count = ((PlaylistNodeSecondaryLeaf) tmp).genreCount();
		PlaylistNode node;
		ArrayList<String> genres;
		PlaylistNode c1, c2;
		while(count > 2*PlaylistNode.order){
			if(secondaryRoot.getType() == PlaylistNodeType.Leaf){
				//System.out.println("root is leaf");
				node = new PlaylistNodeSecondaryIndex(null);
				c1 = new PlaylistNodeSecondaryLeaf(node);
				c2 = new PlaylistNodeSecondaryLeaf(node);
				for(i=0 ; i<(count-1)/2 ; ++i){
					for(j=0 ; j<((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).size() ; ++j){
						((PlaylistNodeSecondaryLeaf) c1).addSong(i, ((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j));
					}
				}

				for( ; i<count ; ++i){
					for(j=0 ; j<((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).size() ; ++j){
						((PlaylistNodeSecondaryLeaf) c2).addSong(i-(count-1)/2, ((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j));
					}
				}
				((PlaylistNodeSecondaryIndex) node).addChild(0, c1);
				((PlaylistNodeSecondaryIndex) node).addChild(1, c2);
				((PlaylistNodeSecondaryIndex) node).addGenre(((PlaylistNodeSecondaryLeaf) tmp).genreAtIndex((count-1)/2));
				secondaryRoot = node;
				break;
			}
			if(tmp.getType() == PlaylistNodeType.Leaf){
				//System.out.println("we are on leaf");
				node = tmp.getParent();
				c1 = new PlaylistNodeSecondaryLeaf(node);
				c2 = new PlaylistNodeSecondaryLeaf(node);
				for(i=0 ; i<(count-1)/2 ; ++i){
					for(j=0 ; j<((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).size() ; ++j){
						((PlaylistNodeSecondaryLeaf) c1).addSong(i, ((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j));
					}
				}

				for( ; i<count ; ++i){
					for(j=0 ; j<((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).size() ; ++j){
						((PlaylistNodeSecondaryLeaf) c2).addSong(i-(count-1)/2, ((PlaylistNodeSecondaryLeaf) tmp).songsAtIndex(i).get(j));
					}
				}

				((PlaylistNodeSecondaryIndex) node).addGenre(((PlaylistNodeSecondaryLeaf) c2).genreAtIndex(0));
				for(i=0 ; i<((PlaylistNodeSecondaryIndex) node).getAllChildren().size() ; ++i){
					if(tmp == ((PlaylistNodeSecondaryIndex) node).getChildrenAt(i)){
						break;
					}
				}
				((PlaylistNodeSecondaryIndex) node).removeChild(i);
				((PlaylistNodeSecondaryIndex) node).addChild(i, c1);
				((PlaylistNodeSecondaryIndex) node).addChild(i+1, c2);
				tmp = node;
				//System.out.println(((PlaylistNodePrimaryIndex) node).getAllChildren().size());
				//System.out.println(((PlaylistNodePrimaryIndex) node).audioIdCount());
			}else if(tmp.getParent() == null){
				//System.out.println("parent is null");
				node = new PlaylistNodeSecondaryIndex(null);
				c1 = new PlaylistNodeSecondaryIndex(node);
				c2 = new PlaylistNodeSecondaryIndex(node);
				((PlaylistNodeSecondaryIndex) node).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex((count-1)/2));
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodeSecondaryIndex) c1).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i));
				}
				for(i=(count+1)/2 ; i<count ; ++i){
					((PlaylistNodeSecondaryIndex) c2).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i));
				}
				for(i=0 ; i<((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size()/2 ; ++i){
					((PlaylistNodeSecondaryIndex) c1).addChild(i, ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i).setParent(c1);
				}
				for( ; i<((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size() ; ++i){
					((PlaylistNodeSecondaryIndex) c2).addChild(i-((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size()/2, ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i).setParent(c2);
				}
				((PlaylistNodeSecondaryIndex) node).addChild(0, c1);
				((PlaylistNodeSecondaryIndex) node).addChild(1, c2);
				//System.out.println(((PlaylistNodePrimaryIndex) node).getAllChildren().size());
				//System.out.println(((PlaylistNodePrimaryIndex) node).audioIdCount());
				secondaryRoot = node;
				break;
			}else{
				//System.out.println("we still have way to go");
				node = tmp.getParent();
				c1 = new PlaylistNodeSecondaryIndex(node);
				c2 = new PlaylistNodeSecondaryIndex(node);
				((PlaylistNodeSecondaryIndex) node).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex((count-1)/2));
				for(i=0 ; i<(count-1)/2 ; ++i){
					((PlaylistNodeSecondaryIndex) c1).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i));
				}
				for(i=(count+1)/2 ; i<count ; ++i){
					((PlaylistNodeSecondaryIndex) c2).addGenre(((PlaylistNodeSecondaryIndex) tmp).genreAtIndex(i));
				}
				for(i=0 ; i<((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size()/2 ; ++i){
					((PlaylistNodeSecondaryIndex) c1).addChild(i, ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i).setParent(c1);
				}
				for( ; i<((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size() ; ++i){
					((PlaylistNodeSecondaryIndex) c2).addChild(i-((PlaylistNodeSecondaryIndex) tmp).getAllChildren().size()/2, ((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i));
					((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i).setParent(c2);
				}
				for(i=0 ; i<((PlaylistNodeSecondaryIndex) node).getAllChildren().size() ; ++i){
					if(tmp == ((PlaylistNodeSecondaryIndex) node).getChildrenAt(i)){
						break;
					}
				}
				((PlaylistNodeSecondaryIndex) node).removeChild(i);
				((PlaylistNodeSecondaryIndex) node).addChild(i, c1);
				((PlaylistNodeSecondaryIndex) node).addChild(i+1, c2);
				tmp = node;
			}

			count = ((PlaylistNodeSecondaryIndex) tmp).genreCount();
			/*for(i=0 ; i<count ; ++i){
				System.out.println(((PlaylistNodePrimaryIndex) tmp).audioIdAtIndex(i));
			}*/
		}
	}

	public void levelsSecondary(PlaylistNode tmp){
		if(tmp == secondaryRoot){
			tmp.level = 0;
		}
		if(tmp.getType() == PlaylistNodeType.Internal){
			for(int i=0 ; i<((PlaylistNodeSecondaryIndex) tmp).genreCount() ; ++i){
				((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i).level = tmp.level+1;
				levelsPrimary(((PlaylistNodeSecondaryIndex) tmp).getChildrenAt(i));
			}
		}
	}
}


