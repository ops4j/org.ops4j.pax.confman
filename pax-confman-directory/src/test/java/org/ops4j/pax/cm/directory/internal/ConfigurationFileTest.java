/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.cm.directory.internal;

import java.io.File;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.ops4j.lang.NullArgumentException;

/**
 * Unit tests to ConfigurationFile.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 20, 2008
 */
public class ConfigurationFileTest
{

    /**
     * Tests that calling constructor with null pid is not allowed.
     */
    @Test( expected = NullArgumentException.class )
    public void constructorWithNullPid()
    {
        new ConfigurationFile( null, "factory.pid", new File( "file" ) );
    }

    /**
     * Tests that calling constructor with null factory pid is allowed.
     */
    @Test
    public void constructorWithNullFactoryPid()
    {
        new ConfigurationFile( "pid", null, new File( "file" ) );
    }

    /**
     * Tests that calling constructor with null file is not allowed.
     */
    @Test( expected = NullArgumentException.class )
    public void constructorWithNullFile()
    {
        new ConfigurationFile( "pid", "factoryPid", null );
    }

    /**
     * Tests pid & factory pid for a file with no extension on a managed service.
     */
    @Test
    public void fileWithNoExtensionOnMS()
    {
        ConfigurationFile underTest = ConfigurationFile.forManagedService( new File( "pid" ) );
        assertThat( "Pid", underTest.getPid(), is( equalTo( "pid" ) ) );
        assertThat( "Factory pid", underTest.getFactoryPid(), is( nullValue() ) );
    }

    /**
     * Tests pid & factory pid for a file with extension on a managed service.
     */
    @Test
    public void fileWithExtensionOnMS()
    {
        ConfigurationFile underTest = ConfigurationFile.forManagedService( new File( "pid.conf" ) );
        assertThat( "Pid", underTest.getPid(), is( equalTo( "pid" ) ) );
        assertThat( "Factory pid", underTest.getFactoryPid(), is( nullValue() ) );
    }

    /**
     * Tests pid & factory pid for a file with extension on a managed service factory.
     */
    @Test
    public void fileWithExtensionOnMSF()
    {
        ConfigurationFile underTest = ConfigurationFile.forManagedServiceFactory(
            new File( new File( "factoryPid" ), "pid.conf" )
        );
        assertThat( "Pid", underTest.getPid(), is( equalTo( "factoryPid-pid" ) ) );
        assertThat( "Factory pid", underTest.getFactoryPid(), is( equalTo( "factoryPid" ) ) );
    }

    /**
     * Tests that a file that only has an extension si not allowed for a managed service factory.
     */
    @Test( expected = NullArgumentException.class )
    public void fileWithExtensionAndNoNameOnMSF()
    {
        ConfigurationFile underTest = ConfigurationFile.forManagedServiceFactory(
            new File( new File( "factoryPid" ), ".conf" )
        );
    }

    /**
     * Tests pid & factory pid for a file with no extension on a managed service factory.
     */
    @Test
    public void fileWithNoExtensionOnMSF()
    {
        ConfigurationFile underTest = ConfigurationFile.forManagedServiceFactory(
            new File( new File( "factoryPid" ), "pid" )
        );
        assertThat( "Pid", underTest.getPid(), is( equalTo( "factoryPid-pid" ) ) );
        assertThat( "Factory pid", underTest.getFactoryPid(), is( equalTo( "factoryPid" ) ) );
    }

    /**
     * Tests that a file with no parent is not allowed for an manages service factory.
     */
    @Test( expected = NullArgumentException.class )
    public void fileWithNoParentOnMSF()
    {
        ConfigurationFile.forManagedServiceFactory( new File( "factoryPid" ) );
    }

}
