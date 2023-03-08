package com.mastertheboss.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import compute.PathComputation;
import dto.Empire;
import dto.Millenium;
import model.RouteTable;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UploadServlet", urlPatterns = { "/UploadServlet" })
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static SessionFactory sessionFactory = null;

	private static final String UPLOAD_DIRECTORY = "upload";
	private static final int THRESHOLD_SIZE 	= 1024 * 1024 * 3; 	// 3MB
	private static final int MAX_FILE_SIZE 		= 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE 	= 1024 * 1024 * 50; // 50MB
	private static final String PATH_DB 	= "C:\\Users\\Thang Nguyen\\OneDrive\\Desktop\\Personal\\Test\\helloworld\\universe.db";

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

	/**
	 * handles file upload via HTTP POST method
	 */
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response) throws ServletException, IOException {

		// checks if the request actually contains upload file
		if (!ServletFileUpload.isMultipartContent(request)) {
			PrintWriter writer = response.getWriter();
			writer.println("Request does not contain upload data");
			writer.flush();
			return;
		}

		// configures upload settings
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(THRESHOLD_SIZE);
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(MAX_FILE_SIZE);
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// constructs the directory path to store upload file
		String uploadPath = getServletContext().getRealPath("")
				+ File.separator + UPLOAD_DIRECTORY;
		// creates the directory if it does not exist
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// parses the request's content to extract file data
			List<FileItem> formItems = upload.parseRequest(request);

			// iterates over form's fields
			for (FileItem item : formItems) {
				// processes only fields that are not form fields
				if (!item.isFormField()) {
					String fileName = new File(item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					File storeFile = new File(filePath);

					item.write(storeFile);
				}
			}

			byte[] encoded = Files.readAllBytes(Paths.get(uploadPath + File.separator + "empire.txt"));
			String empireJson = new String(encoded, StandardCharsets.UTF_8);

			encoded = Files.readAllBytes(Paths.get(uploadPath + File.separator + "millenium.txt"));
			String milleniumJson = new String(encoded, StandardCharsets.UTF_8);
			
			double odd = 0;

			// Configure the session factory
			configureSessionFactory();

			Session session;

			Transaction tx = null;
			try {
				session = sessionFactory.openSession();
				tx = session.beginTransaction();
				updateTable();
				session.flush();
				tx.commit();

				List<RouteTable> routeTableList = new ArrayList<>();

				// Fetching saved data
				try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + PATH_DB);
					 Statement stmt = conn.createStatement()) {
					ResultSet rs = stmt.executeQuery("select * from routeTable");

					while (rs.next()) {
						int travelTime = rs.getInt("travelTime");
						String origin = rs.getString("origin");
						String des = rs.getString("destination");

						//Assuming you have a user object
						RouteTable route = new RouteTable(origin, des, travelTime);

						routeTableList.add(route);
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

//			List<RouteTable> routeTableList = session.createQuery("from RouteTable", RouteTable.class).getResultList();


				for (RouteTable routeTable : routeTableList) {
					System.out.println("route origin : " + routeTable.getOrigin() + " | destination : " + routeTable.getDestination()
							+ " | getTravelTime : " + routeTable.getTravelTime());
				}

				odd = computeOdd(routeTableList, empireJson, milleniumJson);

			} catch (Exception ex) {
				ex.printStackTrace();

				// Rolling back the changes to make the data consistent in case of any failure
				// in between multiple database write operations.
				assert tx != null;
				tx.rollback();
			}

			request.setAttribute("message", "The upload of empire and millenium file has been done successfully. The odd is : " + odd);
		} catch (Exception ex) {
			request.setAttribute("message", "There was an error: " + ex.getMessage());
		}



		getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);

//		sessionFactory.close();
	}

	private Double computeOdd(List<RouteTable> routeTableList, String empireJson, String milleniumJson) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		Empire empire = mapper.readValue(empireJson, Empire.class);
		Millenium millenium = mapper.readValue(milleniumJson, Millenium.class);

		// Get values from Json string
		String departure = millenium.getDeparture();
		String arrival = millenium.getArrival();
		int autonomy = millenium.getAutonomy();
		int maxAutonomy = millenium.getAutonomy();
		int actualDay = 0;
		int countdown = empire.getCountdown();
		PathComputation.setBountyHunters(empire.getBounty_hunters());

		double odd = PathComputation.DFS(routeTableList, departure, arrival, autonomy, maxAutonomy, actualDay, countdown, 0);

		return odd;
	}

	private static void updateTable() {

		String sql1 = "drop table IF EXISTS routeTable";
		String sql2 = "create table routeTable(id integer primary key autoincrement, origin text, destination text, travelTime integer)";
		String sql3 = "insert into routeTable(origin, destination, travelTime) select origin, destination, travel_time from routes";

		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + PATH_DB);
			 Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.execute(sql1);
			stmt.execute(sql2);
			stmt.execute(sql3);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
