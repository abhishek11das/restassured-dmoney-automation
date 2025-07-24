package testrunner;

import com.github.javafaker.Faker;
import config.Setup;
import config.UserModel;
import controller.UserController;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.Utils;
//import org.junit.jupiter.api.Test;

public class UserTestRunner extends Setup {
    UserController userController;

    @BeforeClass
    public void createUserControllerObj(){
        userController = new UserController(prop);
    }

    @Test(priority = 1, description = "User Login")
    public void doLogin() throws ConfigurationException {
        UserModel userModel = new UserModel();
        userModel.setEmail("admin@roadtocareer.net");
        userModel.setPassword("1234");
        Response res = userController.doLogin(userModel);
        JsonPath jsonObj = res.jsonPath();
        String token = jsonObj.get("token");
        Utils.setEnv("token", token);
    }

    @Test(priority = 2, description = "Create new user")
    public void createUser() throws ConfigurationException {
        UserModel userModel = new UserModel();
        Faker fk = new Faker();
        userModel.setName(fk.name().fullName());
        userModel.setEmail(fk.internet().emailAddress());
        userModel.setPassword("1234");
        String phn = "0167"+Utils.generateRandomNumber(1000000,9999999);
        userModel.setPhone_number(phn);
        userModel.setNid("123456789");
        userModel.setRole("Customer");
        Response res = userController.createUser(userModel);
        System.out.println(res.asString());
        JsonPath jsonObj = res.jsonPath();
        int userId = jsonObj.get("user.id");
        System.out.println(userId);
        Utils.setEnv("userId",String.valueOf(userId));
    }

    @Test(priority = 3, description = "Search user")
    public void searchUser(){
        Response res = userController.searchUser(prop.getProperty("userId"));
        System.out.println(res.asString());

        JsonPath jsonPath = res.jsonPath();
        String msgActual = jsonPath.get("message");
        String msgExpected = "User found";
        Assert.assertEquals(msgActual,msgExpected);

    }

    @Test(priority = 4, description = "Delete user")
    public void deleteUser(){
        System.out.println(prop.getProperty("userId"));
        Response res = userController.deleteUser(prop.getProperty("userId"));
        System.out.println(res.asString());

    }
}
