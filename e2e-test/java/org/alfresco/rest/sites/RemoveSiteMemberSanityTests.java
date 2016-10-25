package org.alfresco.rest.sites;

import org.alfresco.rest.RestTest;
import org.alfresco.rest.requests.RestSitesApi;
import org.alfresco.utility.constants.UserRole;
import org.alfresco.utility.data.DataUser;
import org.alfresco.utility.data.DataUser.ListUserWithRoles;
import org.alfresco.utility.exception.DataPreparationException;
import org.alfresco.utility.model.SiteModel;
import org.alfresco.utility.model.TestGroup;
import org.alfresco.utility.model.UserModel;
import org.alfresco.utility.report.Bug;
import org.alfresco.utility.testrail.ExecutionType;
import org.alfresco.utility.testrail.annotation.TestRail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author iulia.cojocea
 */
@Test(groups = {TestGroup.REST_API, TestGroup.SITES, TestGroup.SANITY})
public class RemoveSiteMemberSanityTests extends RestTest
{

    @Autowired
    RestSitesApi restSitesAPI;
    
    @Autowired
    DataUser dataUser;
    
    private SiteModel siteModel;
    private UserModel adminUserModel;
    private ListUserWithRoles usersWithRoles;
    private UserModel testUserModel;
    
    @BeforeClass(alwaysRun = true)
    public void dataPreparation() throws DataPreparationException{
        adminUserModel = dataUser.getAdminUser();
        siteModel = dataSite.usingUser(adminUserModel).createPublicRandomSite();
        usersWithRoles = dataUser.addUsersWithRolesToSite(siteModel, UserRole.SiteManager, UserRole.SiteCollaborator, 
                UserRole.SiteConsumer, UserRole.SiteContributor);
        restSitesAPI.useRestClient(restClient);
    }
    
    @BeforeMethod
    public void addUserToSite() throws DataPreparationException{
        testUserModel = dataUser.createRandomTestUser();
        dataUser.addUserToSite(testUserModel, siteModel, UserRole.SiteConsumer);
    }

    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that site manager can delete site member and gets status code 204, 'No Content'")
    public void siteManagerIsAbleToDeleteSiteMember() throws Exception{
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteManager));
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.NO_CONTENT);
        restSitesAPI.getAllSites().assertEntriesListDoesNotContain("id", testUserModel.getUsername());
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that site collaborator cannot delete site member and gets status code 403, 'Forbidden'")
    public void siteCollaboratorIsNotAbleToDeleteSiteMember() throws Exception{
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteCollaborator));
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
        restSitesAPI.getAllSites().assertEntriesListContains("id", testUserModel.getUsername());
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that site contributor cannot delete site member and gets status code 403, 'Forbidden'")
    public void siteContributorIsNotAbleToDeleteSiteMember() throws Exception{
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteContributor));
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
        restSitesAPI.getAllSites().assertEntriesListContains("id", testUserModel.getUsername());
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @Bug(id="ACE-5444")
    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that site consumer cannot delete site member and gets status code 403, 'Forbidden'")
    public void siteConsumerIsNotAbleToDeleteSiteMember() throws Exception{
        restClient.authenticateUser(usersWithRoles.getOneUserWithRole(UserRole.SiteConsumer));
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.FORBIDDEN);
        restSitesAPI.getAllSites().assertEntriesListContains("id", testUserModel.getUsername());
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that admin user can delete site member and gets status code 204, 'No Content'")
    public void adminUserIsAbleToDeleteSiteMember() throws Exception{
        restClient.authenticateUser(adminUserModel);
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.NO_CONTENT);
        restSitesAPI.getAllSites().assertEntriesListDoesNotContain("id", testUserModel.getUsername());
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.OK);
    }
    
    @TestRail(section = {TestGroup.REST_API, TestGroup.SITES }, executionType = ExecutionType.SANITY, 
            description = "Verify that unauthenticated user is not able to delete site member")
    public void unauthenticatedUserIsNotAuthorizedToDeleteSiteMember() throws Exception{
        UserModel inexistentUser = new UserModel("inexistent user", "inexistent password");
        restClient.authenticateUser(inexistentUser);
        restSitesAPI.deleteSiteMember(siteModel, testUserModel);
        restSitesAPI.usingRestWrapper().assertStatusCodeIs(HttpStatus.UNAUTHORIZED);
    }
}
