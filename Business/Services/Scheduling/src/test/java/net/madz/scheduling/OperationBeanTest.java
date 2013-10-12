package net.madz.scheduling;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.madz.core.exceptions.AppServiceException;
import net.madz.scheduling.to.ServiceOrderTO;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredEnterpriseArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OperationBeanTest {

    private static OperationBean bean;

    @BeforeClass
    public static void setup() throws NamingException, GlassFishException, IOException {
        glassfish = GlassFishRuntime.bootstrap().newGlassFish();
        URI uri = createScatteredArchive();
        addShutdownHook(glassfish);
        glassfish.start();
        CommandRunner cr = glassfish.getCommandRunner();
        CommandResult result = cr
                .run("create-jdbc-connection-pool",
                        "--datasourceclassname=com.mysql.jdbc.jdbc2.optional.MysqlDataSource",
                        "--restype=javax.sql.DataSource",
                        "--property=Url=jdbc\\:mysql\\://dbserver\\:3306/crmp?zeroDateTimeBehavior\\=convertToNull:User=root:Password=1q2w3e4r5t",
                        "crmp_pool");
        System.out.println(result.getOutput());
        result = cr.run("create-jdbc-resource", "--connectionpoolid=crmp_pool", "jdbc/crmp");
        System.out.println(result.getOutput());
        deployer = glassfish.getDeployer();
        appName = deployer.deploy(uri);
        InitialContext context = new InitialContext();
        bean = (OperationBean) context.lookup("java:global/scheduling-app/Scheduling-0.0.1-SNAPSHOT/OperationBean");
    }

    private static void cleanup() {
    }

    @AfterClass
    public static void tearDown() throws GlassFishException {
        glassfish.stop();
        if ( appName != null ) {
            deployer.undeploy(appName);
        }
        cleanup();
    }

    private static File basedir = null;

    private static GlassFish glassfish;

    private static String appName;

    private static Deployer deployer;

    private static void addShutdownHook(final GlassFish gf) {
        Runtime.getRuntime().addShutdownHook(new Thread("GlassFish Shutdown Hook") {

            public void run() {
                try {
                    if ( gf != null ) {
                        gf.dispose();
                    }
                } catch (Exception ex) {
                }
            }
        });
    }

    private static URI createScatteredArchive() throws IOException {
        File currentDirectory = new File(System.getProperty("user.dir"));
        if ( currentDirectory.getName().equals("target") ) {
            basedir = currentDirectory.getParentFile();
        } else {
            basedir = currentDirectory;
        }
        // Create a scattered web application.
        ScatteredEnterpriseArchive archive = new ScatteredEnterpriseArchive("scheduling-app");
        try {
            // target/classes directory contains my complied servlets
            archive.addArchive(new File(new File(( basedir ), "target"), "Scheduling-0.0.1-SNAPSHOT.jar"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return archive.toURI();
    }

    @Test
    public void testAllocateResourceTo() throws AppServiceException {
        ServiceOrderTO orderTO = bean.allocateResourceTo(1L, 1L, 1L, 30D);
        System.out.println(orderTO);
    }
}
