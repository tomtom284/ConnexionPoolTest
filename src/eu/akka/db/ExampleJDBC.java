package eu.akka.db;

/**
 *
 * @author thomas.verbeke
 */
import java.sql.Connection;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * A test project demonstrating the use of BoneCP in a JDBC environment.
 *
 * @author wwadge
 *
 */
public class ExampleJDBC {

    private static final Logger LOGGER = Logger.getLogger(ExampleJDBC.class.getName());

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_DRIVER = "driver";
    private static final String PROPERTY_NOM_UTILISATEUR = "nomutilisateur";
    private static final String PROPERTY_MOT_DE_PASSE = "motdepasse";
    private static final String PROPERTY_MIN_CONNEXION = "min_connexion";
    private static final String PROPERTY_MAX_CONNEXION = "max_connexion";

    private static String url;
    private static String driver;
    private static String nomUtilisateur;
    private static String motDePasse;
    private static int minConnexion;
    private static int maxConnexion;

    /**
     * Test de connexion avec un pool de connexion.
     *
     * @param args Pas d'argument attendu.
     */
    public static void main(final String[] args) {
        BoneCP connectionPool;
        Connection connection = null;

        try {
            retrieveProxyParameters();

            Class.forName(driver);
            final BoneCPConfig config = new BoneCPConfig();
            config.setJdbcUrl(url);
            config.setUsername(nomUtilisateur);
            config.setPassword(motDePasse);
            config.setMinConnectionsPerPartition(minConnexion);
            config.setMaxConnectionsPerPartition(maxConnexion);
            config.setPartitionCount(1);
            connectionPool = new BoneCP(config);

            connection = connectionPool.getConnection();
            connection.setReadOnly(true);
            
            if (connection != null) {
                LOGGER.log(Level.DEBUG, "Connexion réussie !");
                final Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                final ResultSet rs = stmt.executeQuery("SELECT code_station FROM station");
                while (rs.next()) {
                    LOGGER.log(Level.INFO, rs.getString("code_station"));
                }
                
//                PreparedStatement updateemp = connection.prepareStatement("insert into emp values(?,?,?)");
//                updateemp.setInt(1, 23);
//                updateemp.setString(2, "Roshan");
//                updateemp.setString(3, "CEO");
//                updateemp.executeUpdate();
//                updateemp.close();
                
                rs.close();
                stmt.close();
            }
            //connectionPool.shutdown(); // shutdown connection pool.
        } catch (ClassNotFoundException e) {
            LOGGER.fatal("Le driver est introuvable dans le classpath.", e);
        } catch (SQLException e) {
            LOGGER.error("La requete SQL n'a pas pu être realisée.", e);
        } finally {
            if (connection != null) {
                try {                    
                    connection.close();
                    LOGGER.log(Level.DEBUG, "Connexion fermée !");
                } catch (SQLException e) {
                    LOGGER.error("Problème lors de fermeture de la connexion : " + e);
                }
            }
        }
    }

    private static void retrieveProxyParameters() {
        final ResourceBundle bundle = ResourceBundle.getBundle("db");

        url = bundle.getString(PROPERTY_URL);
        driver = bundle.getString(PROPERTY_DRIVER);
        nomUtilisateur = bundle.getString(PROPERTY_NOM_UTILISATEUR);
        motDePasse = bundle.getString(PROPERTY_MOT_DE_PASSE);
        minConnexion = Integer.parseInt(bundle.getString(PROPERTY_MIN_CONNEXION));
        maxConnexion = Integer.parseInt(bundle.getString(PROPERTY_MAX_CONNEXION));
    }
}
