package hibernate;

import com.fasterxml.jackson.databind.ObjectMapper;
import compute.PathComputation;
import dto.Empire;
import dto.Millenium;
import model.RouteTable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 * Hello world!
 * 
 */
public class App {
	private static SessionFactory sessionFactory = null;
	private static boolean updateTable = false;

	private static void configureSessionFactory() throws HibernateException {

		try {
			StandardServiceRegistry standardRegistry =
					new StandardServiceRegistryBuilder()
							.configure("hibernate.cfg.xml").build();
			Metadata metaData = new MetadataSources(standardRegistry).getMetadataBuilder().build();
			sessionFactory = metaData.getSessionFactoryBuilder().build();
		} catch (Throwable th) {

			System.err.println("Initial SessionFactory creation failed" + th);

			throw new ExceptionInInitializerError(th);

		}
	}

	private static void updateTable(Session session) {
		session.createNativeQuery("create table routes_copy(id integer primary key autoincrement, origin text, destination text, travelTime integer)").executeUpdate();
		session.createNativeQuery("insert into routes_copy(origin, destination, travelTime) select origin, destination, travelTime from routes").executeUpdate();
		session.createNativeQuery("drop table routes").executeUpdate();
		session.createNativeQuery("alter table routes_copy rename to routes").executeUpdate();
		session.flush();
	}
	
	public static void main(String[] args) {
		// Configure the session factory
		configureSessionFactory();

		Transaction tx = null;
		try {
			Session session = sessionFactory.openSession();
			tx = session.beginTransaction();

			// Fetching saved data
			List<RouteTable> routeTableList = session.createQuery("from RouteTable", RouteTable.class).getResultList();

			for (RouteTable routeTable : routeTableList) {
				System.out.println("route origin : " + routeTable.getOrigin() + " | destination : " + routeTable.getDestination()
						+ " | getTravelTime : " + routeTable.getTravelTime());
			}

			String milleniumJson = "{\n" +
					"    \"autonomy\": 6,\n" +
					"    \"departure\": \"Tatooine\",\n" +
					"    \"arrival\": \"Endor\",\n" +
					"    \"routes_db\": \"universe.db\"\n" +
					"}";

			ObjectMapper mapper = new ObjectMapper();
			Millenium millenium = mapper.readValue(milleniumJson, Millenium.class);

			String empireJson = "{\n" +
					"    \"countdown\": 1500,\n" +
					"    \"bounty_hunters\": [\n" +
					"        {\n" +
					"            \"planet\": \"Hoth\",\n" +
					"            \"day\": 6\n" +
					"        },\n" +
					"        {\n" +
					"            \"planet\": \"Hoth\",\n" +
					"            \"day\": 7\n" +
					"        },\n" +
					"        {\n" +
					"            \"planet\": \"Hoth\",\n" +
					"            \"day\": 8\n" +
					"        }\n" +
					"    ]\n" +
					"}";

			Empire empire = mapper.readValue(empireJson, Empire.class);

			// Get values from Json string
			String departure = millenium.getDeparture();
			String arrival = millenium.getArrival();
			int autonomy = millenium.getAutonomy();
			int maxAutonomy = millenium.getAutonomy();
			int actualDay = 0;
			int countdown = empire.getCountdown();
			PathComputation.setBountyHunters(empire.getBounty_hunters());

			double odd = PathComputation.DFS(routeTableList, departure, arrival, autonomy, maxAutonomy, actualDay, countdown, 0);

			System.out.println("odd day ne Thang : " + odd);

		} catch (Exception ex) {
			ex.printStackTrace();

			// Rolling back the changes to make the data consistent in case of any failure
			// in between multiple database write operations.
			assert tx != null;
			tx.rollback();
		}
	}
}
