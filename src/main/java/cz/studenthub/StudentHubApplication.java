package cz.studenthub;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class StudentHubApplication extends Application<StudentHubConfiguration> {

    public static void main(final String[] args) throws Exception {
        new StudentHubApplication().run(args);
    }

    @Override
    public String getName() {
        return "StudentHub";
    }

    @Override
    public void initialize(final Bootstrap<StudentHubConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final StudentHubConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
