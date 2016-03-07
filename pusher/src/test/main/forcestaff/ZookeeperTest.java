package forcestaff;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperTest {
	public static void main(String[] args) throws Exception {
		ZooKeeper zk = new ZooKeeper("localhost:2181", 600000, new Watcher() {
			public void process(WatchedEvent event) {
				System.out.println(event.toString());
			}
		});
		
		if (zk.exists("/het-test", false) == null) {
			zk.create("/het-test", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		zk.create("/het-test/c1", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		System.out.println(zk.getChildren("/het-test", true));
		zk.delete("/het-test/c1", -1);
		zk.close();
	}
}