package org.protege.owl.server.policy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.protege.owl.server.api.AuthToken;
import org.protege.owl.server.policy.generated.UsersAndGroupsLexer;
import org.protege.owl.server.policy.generated.UsersAndGroupsParser;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UserParserTest {

    @Test
    public void parserTest() throws FileNotFoundException, IOException, RecognitionException {
        File usersAndGroups = new File("etc/standalone/configuration/UsersAndGroups");
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(usersAndGroups));
        UsersAndGroupsLexer lexer = new UsersAndGroupsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        UsersAndGroupsParser parser = new UsersAndGroupsParser(tokens);
        parser.top();
        UserDatabase db = parser.getUserDatabase();
        AuthToken redmond = db.getUser("redmond");
        Assert.assertNotNull(redmond);
        Assert.assertTrue(redmond instanceof SimpleAuthToken);
        Assert.assertEquals(((SimpleAuthToken) redmond).getPassword(), "troglodyte");
    }
}
