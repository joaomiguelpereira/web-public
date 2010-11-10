package jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.mvc.Scope.Session;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job<String> {

	@Override
	public void doJob() throws Exception {
		/*
		 * org.hsqldb.util.DatabaseManagerSwing.main(new String[] { "--url",
		 * "jdbc:hsqldb:mem:playembed", "--noexit" });
		 */
		
		//load dummy users
		Fixtures.load("liveTestUsers.yml");
		

	}

}
