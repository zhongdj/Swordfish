package net.madz.scheduling;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.madz.scheduling.sessions.OperationBean;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredEnterpriseArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class OperationBeanTestBase {

    protected static OperationBean bean;

    private static String EJB_MODULE_NAME = "scheduling";

    private static String APP_NAME = "scheduling-app";

    @BeforeClass
    public static void setup() throws NamingException, GlassFishException, IOException {
        startGlassfish(APP_NAME, EJB_MODULE_NAME);
        InitialContext context = new InitialContext();
        bean = (OperationBean) context.lookup("java:global/" + APP_NAME + "/" + EJB_MODULE_NAME + "/OperationBean");
    }

    protected static void cleanup() {
    }

    @AfterClass
    public static void tearDown() throws GlassFishException {
        glassfish.stop();
        if ( appName != null ) {
            deployer.undeploy(appName);
        }
        cleanup();
    }

    protected static File basedir = null;

    protected static GlassFish glassfish;

    protected static String appName;

    protected static Deployer deployer;

    protected static void addShutdownHook(final GlassFish gf) {
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

    private static void startGlassfish(String appName, String moduleName) throws GlassFishException, IOException,
            NamingException {
        glassfish = GlassFishRuntime.bootstrap().newGlassFish();
        final URI uri = createScatteredArchive(appName, moduleName + ".jar");
        addShutdownHook(glassfish);
        glassfish.start();
        final CommandRunner cr = glassfish.getCommandRunner();
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
    }

    protected static URI createScatteredArchive(String appName, String moduleName) throws IOException {
        File currentDirectory = new File(System.getProperty("user.dir"));
        if ( currentDirectory.getName().equals("target") ) {
            basedir = currentDirectory.getParentFile();
        } else {
            basedir = currentDirectory;
        }
        // Create a scattered web application.
        ScatteredEnterpriseArchive archive = new ScatteredEnterpriseArchive(appName);
        try {
            // target/classes directory contains my complied servlets
            archive.addArchive(new File(new File(( basedir ), "target"), "classes"), moduleName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return archive.toURI();
    }

    public OperationBeanTestBase() {
        super();
    }
}