
import java.util.*;
import java.lang.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

class Node {
	int x_co;
	int y_co;
	Node parent;
	long path_cost;
	long f_value;

	Node(int y_co, int x_co, Node parent) {
		this.x_co = x_co;
		this.y_co = y_co;
		this.parent = parent;
	}

	Node(int y_co, int x_co, Node parent, long path_cost) {
		this.x_co = x_co;
		this.y_co = y_co;
		this.parent = parent;
		this.path_cost = path_cost;
	}

	Node(int y_co, int x_co, Node parent, long path_cost, long f_value) {
		this.x_co = x_co;
		this.y_co = y_co;
		this.parent = parent;
		this.path_cost = path_cost;
		this.f_value = f_value;
	}

	public int get_x_co() {
		return x_co;
	}

	public int get_y_co() {
		return y_co;
	}

	public long get_path() {
		return path_cost;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Node n1 = (Node) obj;
		return (n1.x_co == this.x_co && n1.y_co == this.y_co);
	}
}

public class homework {

	public static void main(String[] args) throws IOException {

		// Reading file
		String fileName = "input"; // file path

		try (BufferedReader fileBufferReader = new BufferedReader(new FileReader(fileName))) {
			// Variable initialization
			String type = "";
			String dimensions;
			String[] dims;
			int width = 0, height = 0;
			String landing_site;
			String[] landing_co = {};
			int landing_co_x = 0, landing_co_y = 0;
			int elev_diff = 0;
			int no_target_sites = 0;

			// Reading input line by line
			for (int i = 0; i < 5; i++) {
				if (i == 0) {
					type = fileBufferReader.readLine();
				} else if (i == 1) {
					dimensions = fileBufferReader.readLine();
					dims = dimensions.split(" ");
					width = Integer.parseInt(dims[0]);
					height = Integer.parseInt(dims[1]);
				} else if (i == 2) {
					landing_site = fileBufferReader.readLine();
					landing_co = landing_site.split(" ");
					landing_co_x = Integer.parseInt(landing_co[0]);
					landing_co_y = Integer.parseInt(landing_co[1]);
				} else if (i == 3) {
					elev_diff = Integer.parseInt(fileBufferReader.readLine());
				} else if (i == 4) {
					no_target_sites = Integer.parseInt(fileBufferReader.readLine());
				}
			}
			// Reading target matrix
			int target[][] = new int[no_target_sites][2];
			String[] temp;
			String target_line;
			int k = 0;

			while (k < no_target_sites) {
				target_line = fileBufferReader.readLine();
				temp = target_line.split(" ");
				target[k][1] = Integer.parseInt(temp[0]);
				target[k][0] = Integer.parseInt(temp[1]);
				k++;
			}

			// Reading the terrain map
			int m = 0;
			int map[][] = new int[height][width];
			String[] row;
			String row_line;

			while (m < height) {
				row_line = fileBufferReader.readLine();
				row = row_line.split("\\s+");
				for (int i = 0; i < width; i++) {
					map[m][i] = Integer.parseInt(row[i]);
				}
				m++;
			}

			// Goal queue
			HashMap<String, Node> goals_map = new HashMap<>();
			List<Node> goal_list = new ArrayList<Node>();
			for (int i = 0; i < no_target_sites; i++) {
				String s = "";
				s = target[i][0] + "," + target[i][1];
				Node goal = new Node(target[i][0], target[i][1], null, 0, 0);
				goals_map.put(s, goal);
				goal_list.add(goal);
			}

			if (type.equals("BFS")) {
				List<Node> queue = new ArrayList<Node>();
				boolean visited[][] = new boolean[height][width];
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++)
						visited[i][j] = false;
				}
				HashMap<Node, Node> paths = new HashMap<Node, Node>();
				int s1 = landing_co_x;
				int s2 = landing_co_y;
				Node source = new Node(s2, s1, null);
				queue.add(source);
				visited[s2][s1] = true;
				Node current = null;
				while (!goals_map.isEmpty() && !queue.isEmpty()) {
					current = queue.remove(0);
					int c = current.x_co;// column
					int r = current.y_co;// row
					if (goals_map.containsKey(r + "," + c)) {
						paths.put(goals_map.get(r + "," + c), current);
						goals_map.remove(r + "," + c);
						if (goals_map.isEmpty()) {
							break;
						}
					}

					for (int r_inc = -1; r_inc <= 1; r_inc++) {
						for (int c_inc = -1; c_inc <= 1; c_inc++) {
							if (r_inc == 0 && c_inc == 0)
								continue;
							if (c + c_inc >= 0 && c + c_inc < width && r + r_inc >= 0 && r + r_inc < height
									&& Math.abs(map[r + r_inc][c + c_inc] - map[r][c]) <= elev_diff
									&& visited[r + r_inc][c + c_inc] == false) {

								queue.add(new Node(r + r_inc, c + c_inc, current));
								visited[r + r_inc][c + c_inc] = true;
							}
						}
					}

				}
				File file = new File("output");
				FileWriter fos = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fos);
				String[] solution = new String[goal_list.size()];
				int idx = 0;
				while (!goal_list.isEmpty()) {
					int length = 0;
					Node curr_goal = goal_list.remove(0);
					if (paths.containsKey(curr_goal)) {
						Node path_node = paths.get(curr_goal);
						String path = "";

						while (path_node != null) {
							path = path_node.x_co + "," + path_node.y_co + " " + path;
							path_node = path_node.parent;
							length++;
						}
						solution[idx++] = path.substring(0, path.length()-1);
					} else {
						solution[idx++] = "FAIL";
					}
	
				}
				bw.write(solution[0]);
				for (int i = 1; i < idx; i++) {
					bw.newLine();
					bw.write(solution[i]);
				}
				bw.flush();
				bw.close();

			} // end of bfs
			else if (type.equals("UCS")) {
				PriorityQueue<Node> open_queue = new PriorityQueue<Node>(new NodeComparator1());
				boolean visited[][] = new boolean[height][width];
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++)
						visited[i][j] = false;
				}
				HashMap<Node, Node> paths = new HashMap<Node, Node>();
				int s1 = landing_co_x;
				int s2 = landing_co_y;
				Node source = new Node(s2, s1, null, 0);
				open_queue.add(source);
				Node current = null;
				while (!goals_map.isEmpty() && !open_queue.isEmpty()) {
					current = open_queue.poll();
					int c = current.x_co;// column
					int r = current.y_co;// row
					if (goals_map.containsKey(r + "," + c)) {
						paths.put(goals_map.get(r + "," + c), current);
						goals_map.remove(r + "," + c);
						if (goals_map.isEmpty()) {
							break;
						}
					}
					if (visited[r][c] == true) {
						continue;
					}
					visited[r][c] = true;
					for (int r_inc = -1; r_inc <= 1; r_inc++) {
						for (int c_inc = -1; c_inc <= 1; c_inc++) {
							if (r_inc == 0 && c_inc == 0)
								continue;
							if (c + c_inc >= 0 && c + c_inc < width && r + r_inc >= 0 && r + r_inc < height
									&& Math.abs(map[r + r_inc][c + c_inc] - map[r][c]) <= elev_diff
									&& visited[r + r_inc][c + c_inc] == false) {
								int offset = 0;
								if ((r_inc == 1 || r_inc == -1) && (c_inc == 1 || c_inc == -1)) {
									offset = 14;
								} else {
									offset = 10;
								}

								open_queue.add(new Node(r + r_inc, c + c_inc, current, current.path_cost + offset));
							}
						}
					}
				}
				File file = new File("output");
				FileWriter fos = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fos);
				String[] solution = new String[goal_list.size()];
				int idx = 0;
				while (!goal_list.isEmpty()) {
					Node curr_goal = goal_list.remove(0);
					if (paths.containsKey(curr_goal)) {
						Node path_node = paths.get(curr_goal);
						System.out.println(path_node.path_cost);
						String path = "";
						while (path_node != null) {
							path = path_node.x_co + "," + path_node.y_co + " " + path;
							path_node = path_node.parent;
						}
						solution[idx++] = path.substring(0, path.length()-1);
					} else {
						solution[idx++] = "FAIL";
					}

				}
				bw.write(solution[0]);
				for (int i = 1; i < idx; i++) {
					bw.newLine();
					bw.write(solution[i]);
				}
				bw.flush();
				bw.close();
			} // end of ucs
			else {
				PriorityQueue<Node> open_queue = new PriorityQueue<Node>(new NodeComparator2());
				HashMap<String, Node> closed_map = new HashMap<String, Node>();
				boolean visited[][] = new boolean[height][width];
				for (int i = 0; i < height; i++) {
					for (int j = 0; j < width; j++)
						visited[i][j] = false;
				}
				HashMap<Node, Node> paths = new HashMap<Node, Node>();
				int s1 = landing_co_x;
				int s2 = landing_co_y;
				Node source = new Node(s2, s1, null, 0, 0);
				List<Node> goals = new ArrayList<Node>(goal_list);
				while (!goals.isEmpty()) {
					Node curr_target = goals.remove(0);
					open_queue.clear();
					closed_map.clear();
					open_queue.add(source);
					for (int i = 0; i < height; i++) {
						for (int j = 0; j < width; j++)
							visited[i][j] = false;
					}
					Node current = null;
					while (!open_queue.isEmpty()) {
						current = open_queue.poll();
						int c = current.x_co;// column
						int r = current.y_co;// row
						if (curr_target.equals(current)) {
							paths.put(curr_target, current);
							break;

						}
						if (visited[r][c] == true) {
							if (closed_map.get(r + "," + c).path_cost > current.path_cost) {
								closed_map.remove(r + "," + c);

							} else
								continue;
						}
						visited[r][c] = true;
						closed_map.put(r + "," + c, current);
						for (int r_inc = -1; r_inc <= 1; r_inc++) {
							for (int c_inc = -1; c_inc <= 1; c_inc++) {
								if (r_inc == 0 && c_inc == 0)
									continue;
								if (c + c_inc >= 0 && c + c_inc < width && r + r_inc >= 0 && r + r_inc < height
										&& Math.abs(map[r + r_inc][c + c_inc] - map[r][c]) <= elev_diff
										&& visited[r + r_inc][c + c_inc] == false) {
									int offset = 0;
									if ((r_inc == 1 || r_inc == -1) && (c_inc == 1 || c_inc == -1)) {
										offset = Math.abs(map[r + r_inc][c + c_inc] - map[r][c]) + 14;
									} else {
										offset = Math.abs(map[r + r_inc][c + c_inc] - map[r][c]) + 10;
									}
									long h = Math.max(Math.abs(c + c_inc - curr_target.x_co), Math.abs(r + r_inc - curr_target.y_co));
									long f = current.path_cost + offset + h;

									open_queue.add(new Node(r + r_inc, c + c_inc, current, current.path_cost + offset, f));
								}
							}
						}

					}

				}

				File file = new File("output");
				FileWriter fos = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fos);
				String[] solution = new String[goal_list.size()];
				int idx = 0;
				while (!goal_list.isEmpty()) {
					int length = 0;
					Node curr_goal = goal_list.remove(0);
					if (paths.containsKey(curr_goal)) {
						Node path_node = paths.get(curr_goal);
						System.out.println(path_node.path_cost);
						String path = "";
						while (path_node != null) {
							path = path_node.x_co + "," + path_node.y_co + " " + path;
							path_node = path_node.parent;
							length++;
						}

						solution[idx++] = path.substring(0, path.length()-1);

					} else {
						solution[idx++] = "FAIL";
					}

				}
				bw.write(solution[0]);
				for (int i = 1; i < idx; i++) {
					bw.newLine();
					bw.write(solution[i]);
				}
				bw.flush();
				bw.close();
			}
		} // end of try
		catch (IOException e) {
			e.printStackTrace();
		}

	}
}

class NodeComparator2 implements Comparator<Node> {

	public int compare(Node n1, Node n2) {
		if (n1.f_value > n2.f_value)
			return 1;
		else if (n1.f_value < n2.f_value)
			return -1;
		return 0;
	}
}

class NodeComparator1 implements Comparator<Node> {

	public int compare(Node n1, Node n2) {
		if (n1.path_cost > n2.path_cost)
			return 1;
		else if (n1.path_cost < n2.path_cost)
			return -1;
		return 0;
	}
}