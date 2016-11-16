import org.junit.Assert;
import org.junit.Test;
import src.ServerRMI;

import java.io.IOException;
import java.rmi.RemoteException;


public class ServerTest {
    @Test
    public void testLogin() throws IOException {
        ServerRMI serverRMI = new ServerRMI();
        Assert.assertNotEquals(0, serverRMI.login("yiran", "123"));
        Assert.assertEquals(0, serverRMI.login("yiran", "1234"));
    }

    @Test(expected = RemoteException.class)
    public void testFakeUserName() throws IOException {
        ServerRMI serverRMI = new ServerRMI();
        serverRMI.login("yiran121", "123");
    }

    @Test
    public void testPrint() throws IOException {
        ServerRMI serverRMI = new ServerRMI();
        int sessionId = serverRMI.login("yiran", "123");
        Assert.assertNotEquals(0, sessionId);
        String result = serverRMI.print("fileName", "Printer", "yiran", sessionId);
        Assert.assertEquals("fileNamePrinter", result);
        serverRMI.restart("yiran", sessionId);
    }

    @Test(expected = RemoteException.class)
    public void testInsufficient() throws IOException {
        ServerRMI serverRMI = new ServerRMI();
        int sessionId = serverRMI.login("ruby", "123");
        Assert.assertNotEquals(0, sessionId);
        String result = serverRMI.print("fileName", "Printer", "ruby", sessionId);
        Assert.assertEquals("fileNamePrinter", result);
    }
}
