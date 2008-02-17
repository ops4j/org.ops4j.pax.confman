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
package org.ops4j.pax.cm.service.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Pattern;
import org.ops4j.lang.NullArgumentException;

/**
 * Dictionary related utils.
 *
 * @author Alin Dreghiciu
 * @since 0.3.0, February 14, 2008
 */
class DictionaryUtils
{

    /**
     * Utility class.
     */
    private DictionaryUtils()
    {
        // utility class
    }

    /**
     * Copy all entries with keys matching regex from source to target.
     *
     * @param spec   key matching specification
     * @param source source dictionary
     * @param target target dictionary
     *
     * @return target dictionary after copy (more for easy use)
     *
     * @throws NullArgumentException - If specification is null
     */
    @SuppressWarnings( "unchecked" )
    static Dictionary copy( final KeySpecification spec,
                            final Dictionary source,
                            final Dictionary target )
    {
        NullArgumentException.validateNotNull( spec, "Specification" );

        if( source != null && target != null && !source.isEmpty() )
        {
            final Enumeration keys = source.keys();
            while( keys.hasMoreElements() )
            {
                Object key = keys.nextElement();
                if( spec.isSatisfiedBy( key ) )
                {
                    target.put( key, source.get( key ) );
                }
            }
        }
        return target;
    }

    /**
     * Copy all entries from source to target.
     *
     * @param source source dictionary
     * @param target target dictionary
     *
     * @return target dictionary after copy (more for easy use)
     */
    static Dictionary copy( final Dictionary source,
                            final Dictionary target )
    {
        return copy( new AllSpecification(), source, target );
    }

    /**
     * Deep compare of two dictionaries.
     *
     * @param target target dictionary to compare
     * @param source source dictionary to compare
     *
     * @return if dictionaris are equal
     */
    static boolean equal( final Dictionary source,
                          final Dictionary target )
    {
        if( source == null )
        {
            return target == null;
        }
        // source is already not null, so if target is null we have a difference
        if( target == null )
        {
            return false;
        }
        if( source.size() != target.size() )
        {
            return false;
        }
        Hashtable sourceAsHashtable;
        if( source instanceof Hashtable )
        {
            sourceAsHashtable = (Hashtable) source;
        }
        else
        {
            sourceAsHashtable = new Hashtable();
            copy( source, sourceAsHashtable );
        }
        Hashtable targetAsHashtable;
        if( target instanceof Hashtable )
        {
            targetAsHashtable = (Hashtable) target;
        }
        else
        {
            targetAsHashtable = new Hashtable();
            copy( target, targetAsHashtable );
        }
        return sourceAsHashtable.equals( targetAsHashtable );
    }

    /**
     * Dictionary key specification.
     */
    public static interface KeySpecification
    {

        /**
         * Returns true if the specification is satisfied by key
         *
         * @param key dictionary key
         *
         * @return true is satisfied
         */
        boolean isSatisfiedBy( Object key );
    }

    /**
     * Key specification that matches any key.
     */
    public static class AllSpecification
        implements KeySpecification
    {

        /**
         * @see org.ops4j.pax.cm.service.internal.DictionaryUtils.KeySpecification#isSatisfiedBy(Object)
         */
        public boolean isSatisfiedBy( final Object key )
        {
            return true;
        }
    }

    /**
     * Key specification that matches keys based on a regular expression.
     */
    public static class RegexSpecification
        implements KeySpecification
    {

        /**
         * Regular expression pattern to be matched.
         */
        private final Pattern m_pattern;

        /**
         * Constructor.
         *
         * @param regex regular expression to be matched by keys.
         *
         * @throws NullArgumentException - If regex is null or empty
         */
        public RegexSpecification( final String regex )
        {
            NullArgumentException.validateNotEmpty( regex, true, "Regular expression" );

            m_pattern = Pattern.compile( regex );
        }

        /**
         * @see org.ops4j.pax.cm.service.internal.DictionaryUtils.KeySpecification#isSatisfiedBy(Object)
         */
        public boolean isSatisfiedBy( final Object key )
        {
            return key != null && m_pattern.matcher( key.toString() ).matches();
        }
    }

    /**
     * Key specification that acts as a logical NOT.
     */
    public static class NotSpecification
        implements KeySpecification
    {

        /**
         * Specification to be negated.
         */
        private final KeySpecification m_specification;

        /**
         * Constructor.
         *
         * @param specification to be negated
         *
         * @throws NullArgumentException - If specification is null or empty
         */
        public NotSpecification( final KeySpecification specification )
        {
            NullArgumentException.validateNotNull( specification, "Specification" );

            m_specification = specification;
        }

        /**
         * @see org.ops4j.pax.cm.service.internal.DictionaryUtils.KeySpecification#isSatisfiedBy(Object)
         */
        public boolean isSatisfiedBy( final Object key )
        {
            return !m_specification.isSatisfiedBy( key );
        }
    }

    /**
     * Key specification that acts as a logical AND between specifications.
     */
    public static class AndSpecification
        implements KeySpecification
    {

        /**
         * Specification to be ANDed.
         */
        private final KeySpecification[] m_specifications;

        /**
         * Constructor.
         *
         * @param specifications to be ANDed
         *
         * @throws NullArgumentException - If specifications is null or empty
         */
        public AndSpecification( final KeySpecification... specifications )
        {
            NullArgumentException.validateNotEmpty( specifications, "Specifications" );

            m_specifications = specifications;
        }

        /**
         * @see org.ops4j.pax.cm.service.internal.DictionaryUtils.KeySpecification#isSatisfiedBy(Object)
         */
        public boolean isSatisfiedBy( final Object key )
        {
            for( KeySpecification spec : m_specifications )
            {
                if( !spec.isSatisfiedBy( key ) )
                {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Key specification that acts as a logical OR between specifications.
     */
    public static class OrSpecification
        implements KeySpecification
    {

        /**
         * Specification to be ORed.
         */
        private final KeySpecification[] m_specifications;

        /**
         * Constructor.
         *
         * @param specifications to be ORed
         *
         * @throws NullArgumentException - If specifications is null or empty
         */
        public OrSpecification( final KeySpecification... specifications )
        {
            NullArgumentException.validateNotEmpty( specifications, "Specifications" );

            m_specifications = specifications;
        }

        /**
         * @see org.ops4j.pax.cm.service.internal.DictionaryUtils.KeySpecification#isSatisfiedBy(Object)
         */
        public boolean isSatisfiedBy( final Object key )
        {
            for( KeySpecification spec : m_specifications )
            {
                if( spec.isSatisfiedBy( key ) )
                {
                    return true;
                }
            }
            return false;
        }
    }

}
