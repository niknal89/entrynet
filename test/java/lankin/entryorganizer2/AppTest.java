package lankin.entryorganizer2;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import entryorganizer.Commander;
import entryorganizer.entities.Entity;
import entryorganizer.entities.EntityType;
import entryorganizer.entities.Text;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    }
    
    public void testIndex() {
       /* Commander comm = new Commander("configs.txt", "D:\\EMFiles");
        EntityType et = comm.getEntityType("source");  
        for (int i = 0; i <= 10; i++) {  
           Entity e = comm.getDataManager().forgeEntity(et);           
        } */
    }
}
