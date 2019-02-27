/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.comparator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Test AttributeValueComparatorByOrderingAndNameIgnore class.
 */
public class AttributeValueComparatorByOrderingAndNameIgnoreCaseTest {
    private AttributeValueComparatorByOrderingAndNameIgnoreCase comparator;

    @Rule
    public final MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private Attribute attribute1;

    @Mock
    private Attribute attribute2;

    @Mock
    private Attribute attribute3;

    /**
     * setup the test.
     *
     */
    @Before
    public void setUp()  {
        final Map<Attribute, Integer> orderingMap = new HashMap<Attribute, Integer>(3);
        orderingMap.put(attribute1, 1);
        orderingMap.put(attribute2, 2);
        orderingMap.put(attribute3, 2);
        comparator = new AttributeValueComparatorByOrderingAndNameIgnoreCase(orderingMap);

    }

    /**
     * Test that when compare two attributeValue1, it uses the ordering info if both ordering numbers are found in the internal map.
     */
    @Test
    public void testCompareWithOrdering() {
        final AttributeValue attributeValue1 = mock(AttributeValue.class);
        final AttributeValue attributeValue2 = mock(AttributeValue.class);
        when(attributeValue1.getAttribute()).thenReturn(attribute1);
        when(attributeValue2.getAttribute()).thenReturn(attribute2);

        assertEquals("attributeValue1 should be less than attributeValue2", -1, //$NON-NLS-1$
                     comparator.compare(attributeValue1, attributeValue2));
    }

    /**
     * Test that when compare two attributeValues, if both have same ordering, then use the key to compare. This is
     * same as AttributeComparator which is used when create the AttributeGroupAttribute.
     */
    @Test
    public void testCompareSameOrdering() {
        final AttributeValue attributeValue2 = mock(AttributeValue.class);
        final AttributeValue attributeValue3 = mock(AttributeValue.class);

        when(attributeValue2.getAttribute()).thenReturn(attribute2);
        when(attributeValue3.getAttribute()).thenReturn(attribute3);
        when(attribute2.getKey()).thenReturn("3"); //$NON-NLS-1$
        when(attribute3.getKey()).thenReturn("2"); //$NON-NLS-1$

        assertEquals("attributeValue2 should be greater than attributeValue2", 1, //$NON-NLS-1$
                comparator.compare(attributeValue2, attributeValue3));

        verify(attribute2).getKey();
        verify(attribute3).getKey();
    }

    /**
    * Test that when compare two attributeValues, if one does not have ordering, then use the name to compare.
    */
   @Test
   public void testCompareNoOrdering() {
       final AttributeValue attributeValue1 = mock(AttributeValue.class);
       final AttributeValue attributeValue4 = mock(AttributeValue.class);
       final Attribute attribute4 = mock(Attribute.class);

       when(attributeValue1.getAttribute()).thenReturn(attribute1);
       when(attributeValue4.getAttribute()).thenReturn(attribute4);
       when(attribute1.getName()).thenReturn("attri1"); //$NON-NLS-1$
       when(attribute4.getName()).thenReturn("attri4"); //$NON-NLS-1$

       assertTrue("attributeValue2 should be greater than attributeValue2",  //$NON-NLS-1$
                  comparator.compare(attributeValue1, attributeValue4) < 0);

       verify(attribute1, times(2)).getName();
       verify(attribute4, times(2)).getName();
   }
}
