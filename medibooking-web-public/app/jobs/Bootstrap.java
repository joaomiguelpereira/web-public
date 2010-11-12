package jobs;

import models.Administrator;
import models.OfficeAdministrator;
import models.User;
import models.enums.UserType;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Scope.Session;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job<String> {

	@Override
	public void doJob() throws Exception {

		loadHSQLConsole();
		// load dummy users
		prepareTestData();
	}

	private void prepareTestData() {
		Fixtures.deleteAll();
		User user = new User();
		
		user.setName("Regular User");
		user.setActive(true);
		user.setEmail("user@gmail.com");
		user.setPassword("12345");
		user.save();
		
		OfficeAdministrator oAdmin = new OfficeAdministrator();
		oAdmin.setName("Office admin");
		oAdmin.setActive(true);
		oAdmin.setEmail("oadmin@gmail.com");
		oAdmin.setPassword("12345");
		oAdmin.save();
		
		Administrator admin = new Administrator();
		admin.setName("Service Admin");
		admin.setActive(true);
		admin.setEmail("admin@gmail.com");
		admin.setPassword("12345");
		admin.save();
		
	}

	private void loadHSQLConsole() {
		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
				"jdbc:hsqldb:mem:playembed", "--noexit" });

	}
}
