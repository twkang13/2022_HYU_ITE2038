import java.io.*;
import java.util.ArrayList;

public class BPlusTree {
	
	public static class Pair {
		private int key;
		private int value;
		private Node left_child_node;
		
		public Pair() {
			this.key = 0;
			this.value = 0;
			this.left_child_node = null;
		}
		
		public Pair(int key, int value, Node left_child_node) {
			this.key = key;
			this.value = value;
			this.left_child_node = left_child_node;
		}
	}
	
	public static class Node {
		/* number of keys in node */
		private int m;
		
		/* an array of <key, left_child_node> pairs (non-leaf) || an array of <key, value(or pointer to the value)> pairs */
		private ArrayList<Pair> p;
		
		/* a pointer to the rightmost child node (non-leaf) || a pointer to the sibling node (leaf) */
		private Node r;
		
		private Node parent;
		
		public Node() {
			this.m = 0;
			this.p = new ArrayList<Pair>();
			this.r = null;
			this.parent = null;
		}
		
		/* child node를 구하는 함수 */
		public Node getChildNode(int index) {
			if (index == this.m) {
				return this.r;
			}
			else {
				return this.p.get(index).left_child_node;
			}
		}
		
		/* child node를 설정하는 함수 */
		public void setChildNode(int index, Node node) {
			if (index == this.m) {
				this.r = node;
			}
			else {
				this.p.get(index).left_child_node = node;
			}
		}
		
		public Node findLeft() {
			if (this.parent == null || this.parent.p.get(0).left_child_node == this) { // node가 가장 왼쪽 node
				return null;
			}
			
			if (this.parent.r == this) { // node가 가장 오른쪽 node
				return this.parent.p.get(this.parent.m - 1).left_child_node;
			}
			
			for (int i = 1; i < this.parent.m; i++) {
				if (this.parent.p.get(i-1).left_child_node != null && this.parent.p.get(i).left_child_node == this) {
					 return this.parent.p.get(i-1).left_child_node;
				}
			}
			
			return null;
		}
		
		public Node findRight() {
			if (this.parent == null || this.parent.r == this) { // node가 가장 오른쪽 node
				return null;
			}
			
			if (this.parent.m == 1 && this.parent.p.get(0).left_child_node == this) { // parent의 m이 1일때 
				return this.parent.r;
			}
			
			for (int i = 0; i < this.parent.m - 1; i++) {
				if (this.parent.p.get(i+1).left_child_node != null && this.parent.p.get(i).left_child_node == this) {
					return this.parent.p.get(i+1).left_child_node;
				}
			}
			return null;
		}
	}
	
	public static Node bPlusTree = new Node(); /* B+ Tree */
	
	public static int M;
	
	public static BufferedReader br;
	public static BufferedWriter bw;

	public static void main(String[] args) {
		
		if (args[0].equals("-c")) { //data file creation
			M = Integer.parseInt(args[2]);
			saveTree(args[1]);
		}
		else if (args[0].equals("-i")) { //insertion
			insert(args[1], args[2]);
		}
		else if (args[0].equals("-d")) { //deletion
			delete(args[1], args[2]);
		}
		else if (args[0].equals("-s")) { //single key search
			singleSearch(args[1], Integer.parseInt(args[2]));
		}
		else if (args[0].equals("-r")) { //ranged search
			rangedSearch(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		}
		
		return;
	}
	
	/* Load Tree */
	public static void loadTree(String index_file) {
		File indexFile = new File(index_file);
		
		try {
			br = new BufferedReader(new FileReader(indexFile));
			
			M = Integer.parseInt(br.readLine());
			bPlusTree = buildTree();
			
			if (bPlusTree == null) { //.dat 파일이 처음 만들어진 경우 
				bPlusTree = new Node();
			}
			
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Build Tree */
	public static Node buildTree() throws IOException {
		String data = br.readLine();
		if (data == null) {
			return null;
		}
		
		String[] nodeInfo = data.split(" ");
		
		Node node = new Node();
		Node tmpNode1, tmpNode2, leftmostNode, previousRightmostNode;
		
		int m = Integer.parseInt(nodeInfo[0]);
		node.m = m;
		
		int isLeaf = Integer.parseInt(nodeInfo[1]);
		
		for (int i = 2; i < nodeInfo.length; i++) {
			String[] dataArr = nodeInfo[i].split(",");
			node.p.add(new Pair(Integer.parseInt(dataArr[0]), Integer.parseInt(dataArr[1]), null)); //<key, value>
		}
		
		if (isLeaf == 0) { //non-leaf
			for (int i = 0; i <= m; i++) {
				Node child = buildTree();
				node.setChildNode(i, child);
				child.parent = node; // parent node 연결 
				
				if (i > 0 && child != null) { //leaf node끼리 연결
					tmpNode1 = node.getChildNode(i);
					tmpNode2 = node.getChildNode(i-1);
					
					while (tmpNode1.getChildNode(0) != null) {
						tmpNode1 = tmpNode1.getChildNode(0); //leaf node 탐색
					}
					leftmostNode = tmpNode1;
					
					while (tmpNode2.getChildNode(0) != null) {
						tmpNode2 = tmpNode2.r; //leaf node 탐색
					}
					
					previousRightmostNode = tmpNode2;
					
					previousRightmostNode.r = leftmostNode;
				}
			}
		}
		else { //leaf
			for (int i = 0; i <= m; i++) {
				node.setChildNode(i, null);
			}
		}
		
		return node;
	}
	
	/* Save Tree */
	public static void saveTree(String index_file) {
		File indexFile = new File(index_file);
		
		try {
			bw = new BufferedWriter(new FileWriter(indexFile));
			
			bw.write(Integer.toString(M) + "\n");
			
			saveNode(bPlusTree);
			
			bw.flush();
			bw.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveNode(Node node) throws IOException { // key개수 leaf확인 key1,value1 key2,value2 key3,value3... \n 식으로 저장 
		if (node == null || node.m == 0) { // escape condition
			return;
		}
		
		bw.write(Integer.toString(node.m) + " ");
		
		if (node.getChildNode(0) == null) {
			bw.write(Integer.toString(1) + " "); //leaf
		}
		else {
			bw.write(Integer.toString(0) + " "); //non-leaf
		}
		
		for (int i = 0; i < node.m; i++) {
			String key = Integer.toString(node.p.get(i).key);
			String value = Integer.toString(node.p.get(i).value);
			
			bw.write(key + "," + value + " ");
		}
		bw.write("\n");
		
		if (node.getChildNode(0) != null) {
			for (int i = 0; i <= node.m; i++) {
				saveNode(node.getChildNode(i)); //DFS
			}
		}
	}
	
	/* Insertion */
	public static void insert(String index_file, String data_file) {
		loadTree(index_file);
		
		File dataFile = new File(data_file);
		
		try {
			br = new BufferedReader(new FileReader(dataFile));
			String data = null;
			
			while (true) {
				data = br.readLine();
				if (data == null) {
					break;
				}
				
				String[] dataArr = data.split(",");
				
				int key = Integer.parseInt(dataArr[0]);
				int value = Integer.parseInt(dataArr[1]);
				
				insertNode(key, value);
			}
			
			saveTree(index_file);
			
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void insertNode(int key, int value) {
		Node leafNode = findOperateNode(key, "leaf");
		
		for (int i = 0; i < leafNode.m; i++) {
			if (leafNode.p.get(i).key == key) {
				System.out.println("Insertion error : " + key + " is already existing.");
				return;
			}
		}
		
		int index = findIndex(leafNode, key);
		
		leafNode.p.add(index, new Pair(key, value, null));
		++leafNode.m;
		
		if (M <= leafNode.m) { // split이 일어나는 경우  
			if (leafNode == bPlusTree) {
				splitRoot(leafNode);
			}
			else {
				splitLeaf(leafNode);
			}
		}
	}

	public static Node findOperateNode(int key, String command) {
		Node node = bPlusTree;
		
		while (true) {
			boolean biggest = true;
			
			if (node.m == 0 || node.p.get(0).left_child_node == null) {
				return node;
			}
			
			if (command.equals("leaf")) {
				for (int i = 0; i < node.m; i++) {
					if (key < node.p.get(i).key) {
						node = node.p.get(i).left_child_node;
						biggest = false;
						break;
					}
				}
			}
			
			else if (command.equals("leftLeaf")) {
				for (int i = 0; i < node.m; i++) {
					if (key <= node.p.get(i).key) {
						node = node.p.get(i).left_child_node;
						biggest = false;						
						break;
					}
				}
			}
			
			if (biggest) {
				node = node.r;
			}
		}
	}
	
	public static int findIndex(Node node, int key) {
		int index = 0;
		
		if (node.m == 0) {
			index = 0;
		}
		else if (node.p.get(node.m - 1).key < key) { 
			index = node.m;
		}
		else {
			for (int i = 0; i < node.m; i++) {
				if (key < node.p.get(i).key) {
					index = i;
					break;
				}
			}
		}
		
		return index;
	}
	
	public static void splitRoot(Node root) { //root를 split 
		Node right = new Node();
		int divideNum = M / 2;
		
		for (int i = divideNum; i < M; i++) {
			right.p.add(new Pair(root.p.get(i).key, root.p.get(i).value, null));
			++right.m;
		}
		
		for (int i = M - 1; i >= divideNum; i--) {
			root.p.remove(i);
			--root.m;
		}
		
		Node parent = new Node();
		
		parent.m = 1; // initialize parent node
		parent.p.add(new Pair(right.p.get(0).key, right.p.get(0).value, null));
		
		parent.r = right; // set child node
		parent.p.get(0).left_child_node = root;
		
		root.parent = parent; // set parent node
		right.parent = parent;
		
		right.r = root.r; // set r
		root.r = right;
		
		bPlusTree = parent;
	}
	
	public static void splitLeaf(Node leafNode) {
		Node right = new Node();
		int divideNum = M / 2;
		
		for (int i = divideNum; i < M; i++) {
			right.p.add(new Pair(leafNode.p.get(i).key, leafNode.p.get(i).value, null));
			++right.m;
		}
		
		for (int i = M - 1; i >= divideNum; i--) {
			leafNode.p.remove(i);
			--leafNode.m;
		}
		
		if (findIndex(leafNode.parent, right.p.get(0).key) == leafNode.parent.m) { //parent node의 맨 오른쪽에 삽입 
			leafNode.parent.p.add(new Pair(right.p.get(0).key, right.p.get(0).value, leafNode));
			leafNode.parent.r = right;
		}
		else {
			int index = findIndex(leafNode.parent, right.p.get(0).key);
				
			leafNode.parent.p.add(index, new Pair(right.p.get(0).key, right.p.get(0).value, leafNode));
			leafNode.parent.p.get(index + 1).left_child_node = right;
		}
		++leafNode.parent.m;
			
		right.r = leafNode.r;
		leafNode.r = right;
			
		right.parent = leafNode.parent;
		
		if (M <= leafNode.parent.m) { // parent node에서 split이 일어나는 경우
			splitNonLeaf(leafNode.parent);
		}
	}
	
	public static void splitNonLeaf(Node node) {
		Node right = new Node();
		int len = node.m;
		int divideNum = len / 2;
		
		int insertKey = node.p.get(divideNum).key;
		int insertValue = node.p.get(divideNum).value;
		Node insertNode = node.p.get(divideNum).left_child_node;
		
		for (int i = divideNum + 1; i < M; i++) {
			right.p.add(new Pair(node.p.get(i).key, node.p.get(i).value, node.p.get(i).left_child_node));
			++right.m;
		}
		
		for (int i = len - 1; i >= divideNum; i--) {
			node.p.remove(i);
			--node.m;
		}
		
		right.r = node.r;
		node.r = insertNode;
		
		for (int i = 0; i < right.m; i++) {
			right.p.get(i).left_child_node.parent = right;
		}
		
		right.r.parent = right;
		node.r.parent = node;
		
		if (node.parent == null) { // parent node를 새로 만드는 경우 
			Node parent = new Node();
			
			parent.m = 1;
			
			parent.p.add(new Pair(insertKey, insertValue, node));
			parent.r = right;
			
			node.parent = parent;
			right.parent = parent;
			
			bPlusTree = parent;
		}
		
		else { // parent node가 이미 존재하는 경우 
			if (findIndex(node.parent, insertKey) == node.parent.m) { // 맨 오른쪽에 삽입 
				node.parent.p.add(new Pair(insertKey, insertValue, node));
				node.parent.r = right;
			}
			else{
				int index = 0;
				
				for (int i = 0; i < node.parent.m; i++) {
					if (node.parent.p.get(i).left_child_node == node) {
						index = i;
					}
				}
				
				node.parent.p.add(index, new Pair(insertKey, insertValue, node));
				node.parent.p.get(index + 1).left_child_node = right;
			}
			
			++node.parent.m;
			
			right.parent = node.parent;
			
			if (M <= node.parent.m) {
				splitNonLeaf(node.parent);
			}
		}
	}
	
	/* Deletion */
	public static void delete(String index_file, String data_file) {
		loadTree(index_file);
		
		File dataFile = new File(data_file);
		
		try {
			br = new BufferedReader(new FileReader(dataFile));
			String data = null;
			
			while (true) {
				data = br.readLine();
				if (data == null) {
					break;
				}
				
				int key = Integer.parseInt(data);
				
				deleteNode(key);
			}
			
			saveTree(index_file);
			
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteNode(int key) {
		Node node = findOperateNode(key, "leaf");
		Node leftLeaf = findOperateNode(key, "leftLeaf");
		Node traverseNode = node;
		
		boolean flag = false; // B+Tree에 key 들어있는지 확인 
		for (int i = 0; i < node.m; i++) {
			if (node.p.get(i).key == key) {
				flag = true;
				break;
			}
		}
		if (flag == false) {
			return;
		}
		
		int firstKey= node.p.get(0).key;
		int divideNum = (M - 1) / 2;
		
		for (int i = 0; i < node.m; i++) {
			if (node.p.get(i).key == key) {
				node.p.remove(i);
				--node.m;
				break;
			}
		}
		
		if (0 < node.m && key < node.p.get(0).key) {
			substituteKey(node.p.get(0).key, key, traverseNode);
		}
		
		if (divideNum <= node.m) { // 최소 key 개수 조건 충족 여부 확인 
			return;
		}
		
		Node left = node.findLeft();
		Node right = node.findRight();
		
		if (left != null && divideNum < left.m) { // borrow from left
			node.p.add(0, new Pair(left.p.get(left.m - 1).key, left.p.get(left.m - 1).value, null));
			++node.m;
			
			left.p.remove(left.m - 1);
			--left.m;
			
			substituteKey(node.p.get(0).key, firstKey, node.parent);
			
			return;
		}
		else if (right != null && divideNum < right.m) { // borrow from right
			node.p.add(new Pair(right.p.get(0).key, right.p.get(0).value, null));
			++node.m;
			
			right.p.remove(0);
			--right.m;
			
			substituteKey(right.p.get(0).key, node.p.get(node.m-1).key, node.parent);
			
			return;
		}
		
		merge(node, leftLeaf);
	}
	
	public static int findIndexParent(Node node) {
		if (node.parent != null) {
			for (int i = 0; i < node.parent.m; i++) {
				if (node.parent.p.get(i).left_child_node == node) {
					return i;
				}
			}
			if (node.parent.r == node) {
				return node.parent.m;
			}
		}
		
		return -1; // index가 존재하지 않는 경우 
	}
	
	public static void substituteKey(int change, int delete, Node node) { //non-leaf에 삭제될 key(delete)를 새로운 key(change)로 바꾸는 method
		while (true) {
			for (int i = 0; i < node.m; i++) {
				if (delete == node.p.get(i).key) {
					node.p.get(i).key = change; // value는 원래 상관 없으므로 value는 바꾸지 않아도 된다 
					break;
				}
			}
			
			if (node == bPlusTree) {
				return;
			}
			
			node = node.parent;
		}
	}
	
	public static void merge(Node node, Node leftLeaf) {
		Node left = node.findLeft();
		Node right = node.findRight();
		
		if (left != null) {
			for (int i = 0; i < node.m; i++) { // left로 node pair 올기기 
				left.p.add(new Pair(node.p.get(i).key, node.p.get(i).value, null));
				++left.m;
			}
				
			left.r = node.r;
				
			int index = findIndexParent(node);
			node.parent.setChildNode(index, left);
				
			if (index >= 0) { // 부모 node에서 값 제거 
				node.parent.p.remove(index - 1);
				--node.parent.m;
			}
				
			if (node.parent == bPlusTree) {
				if (node.parent.m == 0) { // Tree의 height가 1이 되는 경우 
					left.parent = null;
					bPlusTree = left;
				}
			}
			else {
				if (node.parent.m < (M - 1) / 2) {
					mergeParent(node.parent);
				}
			}
		}
		
		else if (right != null) {
			int tmpMin = right.p.get(0).key;
			
			for (int i = node.m - 1; i >= 0; i--) { // right로 node의 pair 옮기기 	
				right.p.add(0, new Pair(node.p.get(i).key, node.p.get(i).value, null));
				++right.m;
			}
			
			if (node.parent.p.get(0).left_child_node == node) {
				substituteKey(right.p.get(0).key, tmpMin, right.parent);
			}
			
			int index = findIndexParent(node);
			node.parent.setChildNode(index, right);
			
			if (leftLeaf != null) {
				leftLeaf.r = right;
			}
			
			if (index >= 0) {
				node.parent.p.remove(index);
				--node.parent.m;
			}
			
			if (node.parent == bPlusTree) {
				if (node.parent.m == 0) { // Tree의 height가 1이 되는 경우 
					right.parent = null;
					bPlusTree = right;
				}
			}
			else {
				if (node.parent.m < (M - 1) / 2) {
					mergeParent(node.parent);
				}
			}
		}
	}
	
	public static void mergeParent(Node node) {
		Node left = node.findLeft();
		Node right = node.findRight();
		
		if (left != null) {
			int index = findIndexParent(left);
			
			left.p.add(new Pair(node.parent.p.get(index).key, node.parent.p.get(index).value, left.r)); // 부모 node의 key를 left로 옮김 
			++left.m;
			
			left.parent.p.remove(index);
			--left.parent.m;
			
			for (int i = 0; i < node.m; i++) { // node의 pair를 left로 옮김 
				left.p.add(new Pair(node.p.get(i).key, node.p.get(i).value, node.p.get(i).left_child_node));
				left.p.get(i).left_child_node.parent = left;
				++left.m;
			}
			
			left.r = node.r;
			left.r.parent = left;
			
			if (left.parent.m == 0 || left.parent.m == index) { // parent node의 자식 설
				left.parent.r = left;
			}
			else {
				left.parent.p.get(index).left_child_node = left;
			}
			
			for (int i = 0; i < left.m; i++) { // child node의 parent 설정 
				left.getChildNode(i).parent = left;
			}
			
			if (M <= left.m) {
				splitNonLeaf(left);
			}
			
			if (left.parent == bPlusTree) {
				if (left.parent.m == 0) {
					left.parent = null;
					bPlusTree = left;
				}
			}
			else if (left.parent.m < (M - 1) / 2) {
				mergeParent(left.parent);
			}
		}
		else if (right != null) {
			int index = findIndexParent(node);
			
			node.p.add(new Pair(node.parent.p.get(index).key, node.parent.p.get(index).value, node.r)); // 부모 node의 key를 node로 옮김
			++node.m;
			
			node.parent.p.remove(index);
			--node.parent.m;
			
			for (int i = 0; i < right.m; i++) { //right의 pair를 node로 옮김 
				node.p.add(new Pair(right.p.get(i).key, right.p.get(i).value, right.p.get(i).left_child_node));
				right.p.get(i).left_child_node.parent = node;
				++node.m;
			}
			
			node.r = right.r;
			node.r.parent = node;
			
			if (node.parent.m == 0 || node.parent.m == index) { // parent node의 자식 설정 
				node.parent.r = node;
			}
			else {
				node.parent.p.get(index).left_child_node = node;
			}
			
			for (int i = 0; i < node.m; i++) { // child node의 parent 설정 
				node.getChildNode(i).parent = node;
			}
			
			if (M <= node.m) {
				splitNonLeaf(node);
			}
			
			if (node.parent == bPlusTree) {
				if (node.parent.m == 0) {
					node.parent = null;		
					bPlusTree = node;
				}
			}
			else if (node.parent.m < (M - 1) / 2) {
				mergeParent(node.parent);
			}
		}
	}
	
	/* Single Key Search */
	public static void singleSearch(String index_file, int key) {
		loadTree(index_file);
		
		Node node = bPlusTree;
		ArrayList<Integer> path = new ArrayList<Integer>();
		
		if (node == bPlusTree && node.getChildNode(0) == null) { // root node가 leaf node인 경우 
			for (int i = 0; i < node.m; i++) {
				if (node.p.get(i).key == key) {
					System.out.println(node.p.get(i).value);
					return;
				}
			}
			
			System.out.println("Not found");
			return;
		}
		
		while (true) {
			if (node == null) {
				for (int i = 0; i < path.size()-1; i++) {
					System.out.print(path.get(i) + ", ");
				}
				System.out.println(path.get(path.size()-1));
				
				System.out.println("Not found");
				return;
			}
			else if (node.getChildNode(0) == null) {
				for (int i = 0; i < path.size()-1; i++) { // path 출력 
					System.out.print(path.get(i) + ", ");
				}
				System.out.println(path.get(path.size()-1));
				
				if (key == node.p.get(node.m - 1).key) { // key가 leaf node의 맨 오른쪽에 있는 경우 
					System.out.println(node.p.get(node.m - 1).value);
					return;
				}
				
				for (int i = 0; i < node.m; i++) { // key가 0 ~ node.m - 2 사이에 있는 경우 
					if (key == node.p.get(i).key) {
						System.out.println(node.p.get(i).value);
						return;
					}
				}
				
				System.out.println("Not found");
				return;
			}
			
			if (node.m == 1) {
				path.add(node.p.get(0).key);
				
				if (key < node.p.get(0).key) {
					node = node.p.get(0).left_child_node;
				}
				else {
					node = node.r;
				}
			}
			else {
				if (node.p.get(node.m - 1).key <= key) {
					path.add(node.p.get(node.m - 1).key);
					node = node.r;
				}
				else if (key < node.p.get(0).key) { //leftmost 
					path.add(node.p.get(0).key);
					node = node.p.get(0).left_child_node;
				}
				else {
					for (int i = 1; i < node.m; i++) {
						if (key < node.p.get(i).key) {
							path.add(node.p.get(i-1).key); // search 중 지나는 전 key
							path.add(node.p.get(i).key); // search 중 지나는 그 다음 key
							
							node = node.p.get(i).left_child_node;
							break;
						}
					}
				}
				
				// key가 맨 오른쪽으로 갈 때 
				if (node.p.get(node.m - 1).left_child_node != null && node.p.get(node.m - 1).key <= key) {
					node = node.r;
				}
				else if (node.p.get(node.m - 1).left_child_node == null && node.p.get(node.m - 1).key < key) {
					node = node.r;
				}
			}
		}
	}
	
	/* Ranged Search*/
	public static void rangedSearch(String index_file, int start_key, int end_key) {
		loadTree(index_file);
		
		if (bPlusTree.m == 0) { // B+Tree가 빈 경우 
			System.out.println("Not found");
			return;
		}
		
		Node node = findOperateNode(start_key, "leaf");
		int index = 0;
		boolean flag = false;
		
		for (int i = 0; i < node.m; i++) {
			if (start_key < node.p.get(i).key) { // 처음 index 설정 
				index = i;
				break;
			}
		}
		
		if (node.p.get(node.m - 1).key < start_key) { // node에 index가 없어 오른쪽 node로 넘어가는 경우 
			node = node.r;
			if (node == null) { // start_key가 index에 저장된 key 값을 넘어선 경우 
				System.out.println("Not found");
				return;
			}
			
			index = 0;
		}
		
		while (true) {
			for (int i = 0; i < node.m; i++) {
				if (end_key < node.p.get(i).key) {
					if (!flag) { // range 안의 key가 없는 경우 
						System.out.println("Not found");
					}
					return;
				}
				
				if (start_key <= node.p.get(i).key) {
					System.out.println(node.p.get(i).key + ", " + node.p.get(i).value);
					flag = true;
				}
			}
			
			if (node.p.get(node.m - 1).key <= end_key) {
				if (node.r == null) { // 맨 오른쪽 leaf node의 끝에 도달한 경우 
					return;
				}
				node = node.r;
			}
		}
	}
}
