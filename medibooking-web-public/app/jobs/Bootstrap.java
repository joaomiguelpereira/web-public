package jobs;

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

		org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
				"jdbc:hsqldb:mem:playembed", "--noexit" });

		// load dummy users
		
		prepareTestData();
	}
	
	private void prepareTestData() {
		Fixtures.load("liveTestUsers.yml");
		User u = User.find("byEmail", "oadmin@gmail.com").first();
		u.setUserType(UserType.OFFICE_ADMIN);
		u.save();
	}

}
