import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

import java.io.FileWriter;

/**
 * Created by het on 2016/4/7.
 */
public class TreeCacheTest {
    public static void main(String[] args) throws Exception {

        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString("localhost").retryPolicy(new RetryOneTime(500)).build();
        curator.start();

        String path = "/ex/het";
        TreeCache cache = new TreeCache(curator, path);
        cache.start();

        final FileWriter writer = new FileWriter("D:/tree.txt");
        writer.write(cache.getCurrentData(path) + "\r\n");

        System.out.println(curator.checkExists().forPath(path));
        cache.getListenable().addListener(new TreeCacheListener() {
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
                writer.write(event.toString());
                writer.write("\r\n");
                writer.flush();
            }
        });

        try {
            curator.create().withMode(CreateMode.PERSISTENT).forPath(ZKPaths.makePath(path, "c1"), "okay".getBytes());
            curator.checkExists().forPath(ZKPaths.makePath(path, "c1"));
            curator.delete().forPath(ZKPaths.makePath(path, "c1"));
            Thread.sleep(1000);
        } finally {
            CloseableUtils.closeQuietly(curator);
            CloseableUtils.closeQuietly(cache);
        }
        writer.close();
    }
}
