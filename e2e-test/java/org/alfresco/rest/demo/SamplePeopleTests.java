package org.alfresco.rest.demo;

import org.alfresco.rest.RestTest;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.testrail.ExecutionType;
import org.alfresco.utility.testrail.annotation.TestRail;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = { "demo" })
public class SamplePeopleTests extends RestTest
{
    private UserModel userModel;
    private UserModel adminUser;

    @BeforeClass(alwaysRun=true)
    public void dataPreparation() throws DataPreparationException
    {
        userModel = dataUser.createUser(RandomStringUtils.randomAlphanumeric(20));
        adminUser = dataUser.getAdminUser();
        restClient.authenticateUser(adminUser);
    }

    @TestRail(section={"demo", "sample-section"}, executionType= ExecutionType.SANITY,
            description = "Verify admin user gets person with Rest API and response is not empty")
    public void adminShouldRetrievePerson() throws Exception
    {
        restClient.withCoreAPI().usingUser(userModel).getPerson().assertThat().field("id").isNotEmpty();
        restClient.assertStatusCodeIs(HttpStatus.OK);
    }

}