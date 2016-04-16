import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.ZKPaths;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Created by het on 2016/4/8.
 */
public class PathChildCacheTest {
    public static void main(String[] args) throws Exception {

        final BufferedWriter writer = new BufferedWriter(new FileWriter("D://path-child.txt"));
        String path = "/pcc";

        CuratorFramework curator = CuratorFrameworkFactory.builder()
                .connectString("localhost").retryPolicy(new RetryOneTime(500)).build();
        curator.start();

        PathChildrenCache pcc = new PathChildrenCache(curator, path, true);
        pcc.start();
        pcc.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework curator, PathChildrenCacheEvent event) throws Exception {
                System.out.println(event.toString());
                writer.write(event.toString());
                writer.newLine();
                writer.flush();
            }
        });
        curator.create().forPath(ZKPaths.makePath(path, "c1"));
        Thread.sleep(1000);
        try {
            curator.delete().forPath(ZKPaths.makePath(path, "c1"));
        } catch (Exception e) {
            System.err.println(e.getClass());
        }
        Thread.sleep(1000);
    }
}
